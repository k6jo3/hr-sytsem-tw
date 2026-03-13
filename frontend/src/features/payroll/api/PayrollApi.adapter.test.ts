// @ts-nocheck
/**
 * PayrollApi Adapter 測試
 *
 * 三方一致性稽核（後端 Response DTO ↔ 合約規格 ↔ 前端 Adapter）
 *
 * 測試範圍：
 *   - adaptPayslipDto：後端 PayslipResponse → 前端 PayslipDto
 *   - adaptPayslipSummary：後端 PayslipResponse → 前端 PayslipSummaryDto
 *
 * 已知不一致（見下方 MISMATCH 標記）：
 *   M01：後端 PayslipResponse 無 departmentName 欄位，adaptPayslipDto 嘗試讀取 raw.departmentName
 *   M02：後端 PayslipResponse 無 createdAt / updatedAt 欄位，adaptPayslipDto fallback 為空字串
 *   M03：後端 PayslipResponse 無 overtimeHours 欄位，adaptPayslipDto 映射 raw.overtimeHours（undefined）
 *   M04：後端 PayrollRunResponse 無 payDate 欄位，前端 PayrollRunDto 期望 payDate（字串）
 *   M05：後端 PayrollRunResponse 無 start/end 對應名稱（LocalDate），與前端 PayrollRunDto.start/end 命名吻合，但型別為 LocalDate vs string
 *   M06：前端 PayrollRunDto.status 含 'CALCULATING' / 'REJECTED'，合約規格與後端無此狀態
 *   M07：前端 PayslipStatus 含 'CALCULATED' / 'VOID'，合約規格薪資單狀態為 DRAFT/CONFIRMED/SENT/VOIDED
 *   M08：後端 SalaryStructureResponse 含 paymentMethod / dailyRate，前端 SalaryStructureDto 無這兩個欄位（資料丟失）
 *   M09：前端 StartPayrollRunRequest 以 start/end 命名期間欄位，合約規格 Request 為 startDate/endDate
 *   M10：PAYSLIP_STATUS_MAP 僅映射 DRAFT/FINALIZED/SENT，後端合約規格狀態為 DRAFT/CONFIRMED/SENT（FINALIZED 不在合約中）
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';

// ──────────────────────────────────────────────
// 將 adaptPayslipDto / adaptPayslipSummary 從 PayrollApi.ts 抽取出來
// 由於這兩個函式是 module-private，測試透過重新宣告相同邏輯來驗證行為。
// 若後續將 adapter 抽出到獨立檔案，改 import 即可。
// ──────────────────────────────────────────────

import { guardEnum } from '../../../shared/utils/adapterGuard';
import type {
  PayslipDto,
  PayslipSummaryDto,
} from './PayrollTypes';

// ---- 複製自 PayrollApi.ts（與原始碼保持一致） ----
const PAYSLIP_STATUS_MAP: Record<string, PayslipDto['status']> = {
  DRAFT: 'DRAFT',
  FINALIZED: 'APPROVED',
  SENT: 'PAID',
};

function adaptPayslipDto(raw: any): PayslipDto {
  return {
    id: raw.id,
    payslip_code: raw.employeeNumber ?? '',
    employee_id: raw.employeeId,
    employee_name: raw.employeeName,
    employee_code: raw.employeeNumber ?? '',
    department_name: raw.departmentName,
    pay_period_start: raw.periodStartDate,
    pay_period_end: raw.periodEndDate,
    payment_date: raw.payDate,
    status: guardEnum(
      'payslip.status',
      PAYSLIP_STATUS_MAP[raw.status] ?? raw.status,
      ['DRAFT', 'APPROVED', 'PAID'] as const,
      'DRAFT'
    ),
    items: [],
    gross_pay: raw.grossWage ?? 0,
    total_deductions: raw.totalDeductions ?? 0,
    net_pay: raw.netWage ?? 0,
    overtime_hours: raw.overtimeHours,
    created_at: raw.createdAt ?? '',
    updated_at: raw.updatedAt ?? '',
  };
}

function adaptPayslipSummary(raw: any): PayslipSummaryDto {
  const start = raw.periodStartDate ?? '';
  const end = raw.periodEndDate ?? '';
  return {
    id: raw.id,
    payslip_code: raw.employeeNumber ?? '',
    pay_period: start && end ? `${start} ~ ${end}` : '',
    payment_date: raw.payDate ?? '',
    gross_pay: raw.grossWage ?? 0,
    net_pay: raw.netWage ?? 0,
    status: guardEnum(
      'payslipSummary.status',
      PAYSLIP_STATUS_MAP[raw.status] ?? raw.status,
      ['DRAFT', 'APPROVED', 'PAID'] as const,
      'DRAFT'
    ),
  };
}
// ---- 複製結束 ----

// ──────────────────────────────────────────────
// 測試輔助：後端 PayslipResponse 模擬資料（camelCase，反映 Spring Boot JSON 序列化）
// ──────────────────────────────────────────────

/** 代表後端 PayslipResponse 的完整欄位 */
function makeMockPayslipResponse(overrides: Record<string, any> = {}): Record<string, any> {
  return {
    // PayslipResponse.java 欄位（camelCase after Jackson serialization）
    id: 'payslip-001',
    payrollRunId: 'run-001',
    employeeId: 'emp-001',
    employeeNumber: 'EMP001',
    employeeName: '王小明',
    periodStartDate: '2025-01-01',
    periodEndDate: '2025-01-31',
    payDate: '2025-02-05',
    baseSalary: 45000,
    grossWage: 52000,
    netWage: 44000,
    totalEarnings: 52000,
    totalDeductions: 8000,
    incomeTax: 3000,
    insuranceDeductions: 5000,
    leaveDeduction: 0,
    overtimePay: 2000,
    status: 'DRAFT',
    pdfUrl: null,
    ...overrides,
  };
}

// ──────────────────────────────────────────────
// 1. adaptPayslipDto 測試
// ──────────────────────────────────────────────

describe('adaptPayslipDto', () => {
  describe('1-1 基本欄位映射（後端 camelCase → 前端 snake_case）', () => {
    it('id 直接映射', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.id).toBe('payslip-001');
    });

    it('employee_id 映射自 employeeId', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.employee_id).toBe('emp-001');
    });

    it('employee_name 映射自 employeeName', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.employee_name).toBe('王小明');
    });

    it('payslip_code 映射自 employeeNumber', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.payslip_code).toBe('EMP001');
    });

    it('employee_code 映射自 employeeNumber（與 payslip_code 相同來源）', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.employee_code).toBe('EMP001');
    });

    it('pay_period_start 映射自 periodStartDate', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.pay_period_start).toBe('2025-01-01');
    });

    it('pay_period_end 映射自 periodEndDate', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.pay_period_end).toBe('2025-01-31');
    });

    it('payment_date 映射自 payDate', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.payment_date).toBe('2025-02-05');
    });

    it('gross_pay 映射自 grossWage', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.gross_pay).toBe(52000);
    });

    it('total_deductions 映射自 totalDeductions', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.total_deductions).toBe(8000);
    });

    it('net_pay 映射自 netWage', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.net_pay).toBe(44000);
    });

    it('items 列表查詢固定為空陣列', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse());
      expect(result.items).toEqual([]);
    });
  });

  describe('1-2 狀態映射（PAYSLIP_STATUS_MAP）', () => {
    it('DRAFT → DRAFT', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ status: 'DRAFT' }));
      expect(result.status).toBe('DRAFT');
    });

    it('FINALIZED → APPROVED（後端狀態映射，注意：合約規格無 FINALIZED 狀態 [M10]）', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ status: 'FINALIZED' }));
      expect(result.status).toBe('APPROVED');
    });

    it('SENT → PAID', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ status: 'SENT' }));
      expect(result.status).toBe('PAID');
    });

    it('未知狀態值原樣傳遞（guardEnum 行為）', () => {
      // guardEnum 對不在允許列表中的值仍回傳原始值（並發出 console.warn）
      const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
      const result = adaptPayslipDto(makeMockPayslipResponse({ status: 'UNKNOWN_STATUS' }));
      // guardEnum 實作：不在允許值中時回傳原始值
      expect(result.status).toBe('UNKNOWN_STATUS' as any);
      expect(warnSpy).toHaveBeenCalledWith(
        expect.stringContaining('[Adapter]')
      );
      warnSpy.mockRestore();
    });
  });

  describe('1-3 Null / Undefined 處理', () => {
    it('grossWage 為 null 時 gross_pay 應為 0', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ grossWage: null }));
      expect(result.gross_pay).toBe(0);
    });

    it('netWage 為 null 時 net_pay 應為 0', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ netWage: null }));
      expect(result.net_pay).toBe(0);
    });

    it('totalDeductions 為 null 時 total_deductions 應為 0', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ totalDeductions: null }));
      expect(result.total_deductions).toBe(0);
    });

    it('employeeNumber 為 null 時 payslip_code 應為空字串', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ employeeNumber: null }));
      expect(result.payslip_code).toBe('');
    });

    it('employeeNumber 為 null 時 employee_code 應為空字串', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ employeeNumber: null }));
      expect(result.employee_code).toBe('');
    });

    it('status 為 null 時 guardEnum 回傳 fallback DRAFT', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ status: null }));
      expect(result.status).toBe('DRAFT');
    });

    it('status 為 undefined 時 guardEnum 回傳 fallback DRAFT', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ status: undefined }));
      expect(result.status).toBe('DRAFT');
    });

    // [M01] 後端 PayslipResponse 無 departmentName 欄位
    it('[M01] departmentName 後端無此欄位時，department_name 應為 undefined', () => {
      const raw = makeMockPayslipResponse();
      delete raw.departmentName; // 後端確實不會序列化此欄位
      const result = adaptPayslipDto(raw);
      expect(result.department_name).toBeUndefined();
    });

    // [M02] 後端 PayslipResponse 無 createdAt / updatedAt 欄位
    it('[M02] createdAt 後端無此欄位時，created_at 應 fallback 為空字串', () => {
      const raw = makeMockPayslipResponse();
      delete raw.createdAt;
      const result = adaptPayslipDto(raw);
      expect(result.created_at).toBe('');
    });

    it('[M02] updatedAt 後端無此欄位時，updated_at 應 fallback 為空字串', () => {
      const raw = makeMockPayslipResponse();
      delete raw.updatedAt;
      const result = adaptPayslipDto(raw);
      expect(result.updated_at).toBe('');
    });

    // [M03] 後端 PayslipResponse 無 overtimeHours 欄位
    it('[M03] overtimeHours 後端無此欄位時，overtime_hours 應為 undefined', () => {
      const raw = makeMockPayslipResponse();
      delete raw.overtimeHours;
      const result = adaptPayslipDto(raw);
      expect(result.overtime_hours).toBeUndefined();
    });
  });

  describe('1-4 後端實際存在但前端未使用的欄位（資料丟失確認）', () => {
    it('後端 payrollRunId 欄位未映射到前端 PayslipDto（資料丟失）', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ payrollRunId: 'run-001' }));
      // PayslipDto 無 payrollRunId 欄位，確認確實丟失
      expect((result as any).payrollRunId).toBeUndefined();
    });

    it('後端 baseSalary 欄位未映射到前端 PayslipDto（資料丟失）', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ baseSalary: 45000 }));
      expect((result as any).baseSalary).toBeUndefined();
    });

    it('後端 incomeTax 欄位未映射到前端 PayslipDto（資料丟失）', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ incomeTax: 3000 }));
      expect((result as any).incomeTax).toBeUndefined();
    });

    it('後端 pdfUrl 欄位未映射到前端 PayslipDto（資料丟失）', () => {
      const result = adaptPayslipDto(makeMockPayslipResponse({ pdfUrl: 'https://example.com/payslip.pdf' }));
      expect((result as any).pdfUrl).toBeUndefined();
    });
  });
});

// ──────────────────────────────────────────────
// 2. adaptPayslipSummary 測試
// ──────────────────────────────────────────────

describe('adaptPayslipSummary', () => {
  describe('2-1 基本欄位映射', () => {
    it('id 直接映射', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse());
      expect(result.id).toBe('payslip-001');
    });

    it('payslip_code 映射自 employeeNumber', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse());
      expect(result.payslip_code).toBe('EMP001');
    });

    it('pay_period 由 periodStartDate ~ periodEndDate 組合', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse());
      expect(result.pay_period).toBe('2025-01-01 ~ 2025-01-31');
    });

    it('payment_date 映射自 payDate', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse());
      expect(result.payment_date).toBe('2025-02-05');
    });

    it('gross_pay 映射自 grossWage', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse());
      expect(result.gross_pay).toBe(52000);
    });

    it('net_pay 映射自 netWage', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse());
      expect(result.net_pay).toBe(44000);
    });
  });

  describe('2-2 狀態映射', () => {
    it('DRAFT → DRAFT', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ status: 'DRAFT' }));
      expect(result.status).toBe('DRAFT');
    });

    it('FINALIZED → APPROVED', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ status: 'FINALIZED' }));
      expect(result.status).toBe('APPROVED');
    });

    it('SENT → PAID', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ status: 'SENT' }));
      expect(result.status).toBe('PAID');
    });

    it('未知狀態值原樣傳遞並發出警告', () => {
      const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
      const result = adaptPayslipSummary(makeMockPayslipResponse({ status: 'CONFIRMED' }));
      // CONFIRMED 在合約規格中存在，但不在 PAYSLIP_STATUS_MAP 中（[M10]），原樣傳遞
      expect(result.status).toBe('CONFIRMED' as any);
      expect(warnSpy).toHaveBeenCalledWith(
        expect.stringContaining('[Adapter]')
      );
      warnSpy.mockRestore();
    });
  });

  describe('2-3 Null / Undefined 處理', () => {
    it('grossWage 為 null 時 gross_pay 應為 0', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ grossWage: null }));
      expect(result.gross_pay).toBe(0);
    });

    it('netWage 為 null 時 net_pay 應為 0', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ netWage: null }));
      expect(result.net_pay).toBe(0);
    });

    it('payDate 為 null 時 payment_date 應為空字串', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ payDate: null }));
      expect(result.payment_date).toBe('');
    });

    it('employeeNumber 為 null 時 payslip_code 應為空字串', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ employeeNumber: null }));
      expect(result.payslip_code).toBe('');
    });

    it('periodStartDate 為 null 時 pay_period 應為空字串', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ periodStartDate: null }));
      expect(result.pay_period).toBe('');
    });

    it('periodEndDate 為 null 時 pay_period 應為空字串', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ periodEndDate: null }));
      expect(result.pay_period).toBe('');
    });

    it('status 為 null 時 guardEnum 回傳 fallback DRAFT', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ status: null }));
      expect(result.status).toBe('DRAFT');
    });

    it('status 為 undefined 時 guardEnum 回傳 fallback DRAFT', () => {
      const result = adaptPayslipSummary(makeMockPayslipResponse({ status: undefined }));
      expect(result.status).toBe('DRAFT');
    });
  });

  describe('2-4 邊界值：pay_period 組合邏輯', () => {
    it('periodStartDate 與 periodEndDate 均有值時，格式為 "start ~ end"', () => {
      const result = adaptPayslipSummary(
        makeMockPayslipResponse({ periodStartDate: '2025-03-01', periodEndDate: '2025-03-31' })
      );
      expect(result.pay_period).toBe('2025-03-01 ~ 2025-03-31');
    });

    it('periodStartDate 為空字串時，pay_period 為空字串', () => {
      const result = adaptPayslipSummary(
        makeMockPayslipResponse({ periodStartDate: '' })
      );
      expect(result.pay_period).toBe('');
    });

    it('periodEndDate 為空字串時，pay_period 為空字串', () => {
      const result = adaptPayslipSummary(
        makeMockPayslipResponse({ periodEndDate: '' })
      );
      expect(result.pay_period).toBe('');
    });
  });
});

// ──────────────────────────────────────────────
// 3. PAYSLIP_STATUS_MAP 完整性驗證
// ──────────────────────────────────────────────

describe('PAYSLIP_STATUS_MAP 完整性', () => {
  it('映射表應包含後端三個已知狀態', () => {
    expect(PAYSLIP_STATUS_MAP['DRAFT']).toBe('DRAFT');
    expect(PAYSLIP_STATUS_MAP['FINALIZED']).toBe('APPROVED');
    expect(PAYSLIP_STATUS_MAP['SENT']).toBe('PAID');
  });

  // [M10] CONFIRMED 在合約規格中明確存在（PAY_CMD_R004 業務規則），但映射表未覆蓋
  it('[M10] TODO: 合約狀態 CONFIRMED 未在映射表中，應補上 CONFIRMED → APPROVED', () => {
    // 目前行為：CONFIRMED 無映射，guardEnum 原樣傳遞並觸發警告
    // 預期修正：PAYSLIP_STATUS_MAP['CONFIRMED'] = 'APPROVED'
    expect(PAYSLIP_STATUS_MAP['CONFIRMED']).toBeUndefined();
  });
});

// ──────────────────────────────────────────────
// 4. 快照測試：確保 adapter 輸出結構穩定
// ──────────────────────────────────────────────

describe('Adapter 快照測試', () => {
  it('adaptPayslipDto 輸出結構快照', () => {
    const result = adaptPayslipDto(makeMockPayslipResponse());
    expect(result).toMatchInlineSnapshot(`
      {
        "created_at": "",
        "department_name": undefined,
        "employee_code": "EMP001",
        "employee_id": "emp-001",
        "employee_name": "王小明",
        "gross_pay": 52000,
        "id": "payslip-001",
        "items": [],
        "net_pay": 44000,
        "overtime_hours": undefined,
        "pay_period_end": "2025-01-31",
        "pay_period_start": "2025-01-01",
        "payment_date": "2025-02-05",
        "payslip_code": "EMP001",
        "status": "DRAFT",
        "total_deductions": 8000,
        "updated_at": "",
      }
    `);
  });

  it('adaptPayslipSummary 輸出結構快照', () => {
    const result = adaptPayslipSummary(makeMockPayslipResponse());
    expect(result).toMatchInlineSnapshot(`
      {
        "gross_pay": 52000,
        "id": "payslip-001",
        "net_pay": 44000,
        "pay_period": "2025-01-01 ~ 2025-01-31",
        "payment_date": "2025-02-05",
        "payslip_code": "EMP001",
        "status": "DRAFT",
      }
    `);
  });
});
