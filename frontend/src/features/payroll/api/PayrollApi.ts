import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { guardEnum } from '../../../shared/utils/adapterGuard';
import { MockPayrollApi } from './MockPayrollApi';
import type {
    CreateSalaryStructureRequest,
    GetMyPayslipsRequest,
    GetMyPayslipsResponse,
    GetPayslipDetailResponse,
    GetPayslipListRequest,
    GetPayslipListResponse,
    PayrollItemDefinitionDto,
    PayrollRunDto,
    PayslipDto,
    PayslipSummaryDto,
    SalaryStructureDto,
    StartPayrollRunRequest,
    UpdateSalaryStructureRequest,
} from './PayrollTypes';

// ========== Response Adapters ==========
// 後端 camelCase → 前端 snake_case

/** 後端薪資單狀態 → 前端狀態映射
 *  後端狀態流轉: DRAFT → CONFIRMED(核准後) → SENT(發薪後) → VOIDED(作廢)
 *  前端狀態:      DRAFT → APPROVED          → PAID        → VOID
 */
const PAYSLIP_STATUS_MAP: Record<string, PayslipDto['status']> = {
  DRAFT: 'DRAFT',
  CONFIRMED: 'APPROVED',
  FINALIZED: 'APPROVED',
  SENT: 'PAID',
  PAID: 'PAID',
  VOIDED: 'VOID',
  VOID: 'VOID',
  CALCULATED: 'CALCULATED',
  APPROVED: 'APPROVED',
};

/**
 * 後端 PayslipResponse → 前端 PayslipDto
 * 支援後端欄位名稱變體（periodStartDate / startDate 等）
 */
function adaptPayslipDto(raw: any): PayslipDto {
  return {
    id: raw.id,
    payslip_code: raw.payslipCode ?? raw.employeeNumber ?? '',
    employee_id: raw.employeeId,
    employee_name: raw.employeeName,
    employee_code: raw.employeeNumber ?? '',
    department_name: raw.departmentName,
    pay_period_start: raw.periodStartDate ?? raw.startDate ?? '',
    pay_period_end: raw.periodEndDate ?? raw.endDate ?? '',
    payment_date: raw.payDate ?? raw.paymentDate ?? '',
    status: guardEnum('payslip.status', PAYSLIP_STATUS_MAP[raw.status] ?? raw.status, ['DRAFT', 'CALCULATED', 'APPROVED', 'PAID', 'VOID'] as const, 'DRAFT'),
    items: [], // 後端列表查詢不含 items 明細
    base_salary: raw.baseSalary ?? 0,
    gross_pay: raw.grossWage ?? raw.grossPay ?? raw.totalEarnings ?? 0,
    total_deductions: raw.totalDeductions ?? raw.insuranceDeductions ?? 0,
    net_pay: raw.netWage ?? raw.netPay ?? 0,
    overtime_hours: raw.overtimeHours,
    created_at: raw.createdAt ?? '',
    updated_at: raw.updatedAt ?? '',
  };
}

/**
 * 後端 PayslipResponse → 前端 PayslipSummaryDto（列表摘要）
 * 支援後端欄位名稱變體（periodStartDate / startDate 等）
 */
function adaptPayslipSummary(raw: any): PayslipSummaryDto {
  const start = raw.periodStartDate ?? raw.startDate ?? '';
  const end = raw.periodEndDate ?? raw.endDate ?? '';
  return {
    id: raw.id,
    payslip_code: raw.payslipCode ?? raw.employeeNumber ?? '',
    pay_period: start && end ? `${start} ~ ${end}` : '',
    payment_date: raw.payDate ?? raw.paymentDate ?? '',
    gross_pay: raw.grossWage ?? raw.grossPay ?? raw.totalEarnings ?? 0,
    net_pay: raw.netWage ?? raw.netPay ?? 0,
    status: guardEnum('payslipSummary.status', PAYSLIP_STATUS_MAP[raw.status] ?? raw.status, ['DRAFT', 'CALCULATED', 'APPROVED', 'PAID', 'VOID'] as const, 'DRAFT'),
  };
}

/** 後端薪資批次狀態 → 前端狀態映射 */
const PAYROLL_RUN_STATUS_MAP: Record<string, PayrollRunDto['status']> = {
  DRAFT: 'DRAFT',
  CALCULATING: 'CALCULATING',
  COMPLETED: 'COMPLETED',
  SUBMITTED: 'SUBMITTED',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
  PAID: 'PAID',
  CANCELLED: 'CANCELLED',
};

/**
 * 後端 PayrollRunResponse → 前端 PayrollRunDto
 * 支援後端欄位名稱變體（start / startDate、end / endDate）
 */
function adaptPayrollRunDto(raw: any): PayrollRunDto {
  return {
    runId: raw.runId ?? raw.id ?? '',
    organizationId: raw.organizationId ?? '',
    name: raw.name ?? '',
    status: guardEnum('payrollRun.status', PAYROLL_RUN_STATUS_MAP[raw.status] ?? raw.status, ['DRAFT', 'CALCULATING', 'COMPLETED', 'SUBMITTED', 'APPROVED', 'REJECTED', 'PAID', 'CANCELLED'] as const, 'DRAFT'),
    payrollSystem: raw.payrollSystem ?? '',
    start: raw.start ?? raw.startDate ?? '',
    end: raw.end ?? raw.endDate ?? '',
    payDate: raw.payDate ?? raw.paymentDate ?? '',
    totalDays: raw.totalDays ?? 0,
    totalEmployees: raw.totalEmployees ?? 0,
    processedEmployees: raw.processedEmployees ?? 0,
    successCount: raw.successCount ?? 0,
    failureCount: raw.failureCount ?? 0,
    totalGrossPay: raw.totalGrossPay ?? 0,
    totalNetPay: raw.totalNetPay ?? 0,
    totalDeductions: raw.totalDeductions ?? 0,
    executedAt: raw.executedAt,
    completedAt: raw.completedAt,
    approvedAt: raw.approvedAt,
    paidAt: raw.paidAt,
  };
}

/**
 * Payroll API (薪資管理 API)
 * Domain Code: HR04
 */
export class PayrollApi {
  private static readonly BASE_PATH = '/payslips';

  /**
   * 取得我的薪資單列表
   * 後端無 /my 端點，改用 /payslips 查詢並轉換格式
   */
  static async getMyPayslips(params: GetMyPayslipsRequest): Promise<GetMyPayslipsResponse> {
    if (MockConfig.isEnabled('PAYROLL')) {
      const result = await MockPayrollApi.getPayslips(params);
      return {
        payslips: result.payslips.map(p => ({
          id: p.id,
          payslip_code: p.payslip_code,
          pay_period: `${p.pay_period_start} ~ ${p.pay_period_end}`,
          payment_date: p.payment_date,
          gross_pay: p.gross_pay,
          net_pay: p.net_pay,
          status: p.status
        })),
        total: result.total,
        page: result.page,
        page_size: result.page_size
      };
    }
    const raw: any = await apiClient.get(`${this.BASE_PATH}`, { params });
    const items = raw.items ?? [];
    return {
      payslips: items.map(adaptPayslipSummary),
      total: raw.totalElements ?? items.length,
      page: raw.page ?? 1,
      page_size: raw.size ?? 20,
    };
  }

  /**
   * 取得薪資單詳情
   */
  static async getPayslipDetail(id: string): Promise<GetPayslipDetailResponse> {
    if (MockConfig.isEnabled('PAYROLL')) {
      const payslip = await MockPayrollApi.getPayslipById(id);
      return { payslip };
    }
    const raw: any = await apiClient.get(`${this.BASE_PATH}/${id}`);
    return { payslip: adaptPayslipDto(raw) };
  }

  /**
   * 下載薪資單PDF
   */
  static async downloadPayslipPdf(id: string): Promise<Blob> {
    if (MockConfig.isEnabled('PAYROLL')) return MockPayrollApi.downloadPayslip(id);
    const response = await apiClient.get<Blob>(`${this.BASE_PATH}/${id}/pdf`, {
      responseType: 'blob',
    });
    return response as unknown as Blob;
  }

  // ========== Salary Structure Management ==========

  /**
   * 查詢薪資結構列表
   */
  static async getSalaryStructures(params?: any): Promise<any> {
    if (MockConfig.isEnabled('PAYROLL')) return { items: [], total: 0 };
    return apiClient.get('/salary-structures', { params });
  }

  /**
   * 查詢員工薪資結構
   */
  static async getEmployeeSalaryStructure(employeeId: string): Promise<SalaryStructureDto> {
    if (MockConfig.isEnabled('PAYROLL')) return MockPayrollApi.getSalaryStructure(employeeId);
    return apiClient.get(`/salary-structures/employee/${employeeId}`);
  }

  /**
   * 建立薪資結構
   */
  static async createSalaryStructure(request: CreateSalaryStructureRequest): Promise<SalaryStructureDto> {
    if (MockConfig.isEnabled('PAYROLL')) return MockPayrollApi.createSalaryStructure(request);
    return apiClient.post('/salary-structures', request);
  }

  /**
   * 更新薪資結構
   */
  static async updateSalaryStructure(id: string, request: UpdateSalaryStructureRequest): Promise<SalaryStructureDto> {
    if (MockConfig.isEnabled('PAYROLL')) return { id, ...request } as any;
    return apiClient.put(`/salary-structures/${id}`, request);
  }

  // ========== Payroll Run (Batch) Management ==========

  /**
   * 查詢薪資批次列表
   */
  static async getPayrollRuns(params?: any): Promise<any> {
    if (MockConfig.isEnabled('PAYROLL')) return MockPayrollApi.getPayrollRuns(params);
    const raw: any = await apiClient.get('/payroll-runs', { params });
    return {
      items: (raw.items ?? []).map(adaptPayrollRunDto),
      total: raw.totalElements ?? 0,
    };
  }

  /**
   * 建立薪資批次
   */
  static async startPayrollRun(request: StartPayrollRunRequest): Promise<PayrollRunDto> {
    if (MockConfig.isEnabled('PAYROLL')) return MockPayrollApi.createPayrollRun(request);
    return apiClient.post('/payroll-runs', request);
  }

  /**
   * 執行薪資計算 batch
   */
  static async calculatePayroll(runId: string): Promise<PayrollRunDto> {
    if (MockConfig.isEnabled('PAYROLL')) {
      await MockPayrollApi.executePayrollRun(runId);
      return MockPayrollApi.getPayrollRunById(runId);
    }
    return apiClient.post(`/payroll-runs/${runId}/execute`, {});
  }

  /**
   * 送審薪資批次
   */
  static async submitPayrollRun(runId: string): Promise<PayrollRunDto> {
    // Mock not implemented yet
    return apiClient.put(`/payroll-runs/${runId}/submit`, {});
  }

  /**
   * 核准薪資批次
   */
  static async approvePayrollRun(runId: string): Promise<PayrollRunDto> {
    if (MockConfig.isEnabled('PAYROLL')) {
      await MockPayrollApi.approvePayrollRun(runId);
      return MockPayrollApi.getPayrollRunById(runId);
    }
    return apiClient.put(`/payroll-runs/${runId}/approve`, {});
  }

  /**
   * 退回薪資批次
   */
  static async rejectPayrollRun(runId: string, reason: string): Promise<PayrollRunDto> {
    // Mock not implemented yet
    return apiClient.put(`/payroll-runs/${runId}/reject`, { reason });
  }

  /**
   * 查詢薪資批次詳情
   */
  static async getPayrollRunDetail(runId: string): Promise<{ run: PayrollRunDto }> {
    if (MockConfig.isEnabled('PAYROLL')) {
      const run = await MockPayrollApi.getPayrollRunById(runId);
      return { run };
    }
    const raw: any = await apiClient.get(`/payroll-runs/${runId}`);
    return { run: adaptPayrollRunDto(raw) };
  }

  /**
   * 查詢薪資單列表 (管理員用)
   */
  static async getPayslips(params: GetPayslipListRequest): Promise<GetPayslipListResponse> {
    if (MockConfig.isEnabled('PAYROLL')) {
      const result = await MockPayrollApi.getPayslips(params as any);
      return {
        items: result.payslips,
        total: result.total
      };
    }
    const raw: any = await apiClient.get('/payslips', { params });
    const items = (raw.items ?? []).map(adaptPayslipDto);
    return {
      items,
      total: raw.totalElements ?? items.length,
    };
  }

  // ========== Payroll Item Definition Management ==========

  /**
   * 取得薪資項目定義列表
   */
  static async getPayrollItemDefinitions(): Promise<PayrollItemDefinitionDto[]> {
    if (MockConfig.isEnabled('PAYROLL')) return [] as any;
    const resp: any = await apiClient.get('/payroll-item-definitions');
    // 後端可能回傳陣列或分頁物件
    return Array.isArray(resp) ? resp : (resp?.items ?? resp?.content ?? resp?.data ?? []);
  }

  /**
   * 建立薪資項目定義
   */
  static async createPayrollItemDefinition(data: Partial<PayrollItemDefinitionDto>): Promise<PayrollItemDefinitionDto> {
    if (MockConfig.isEnabled('PAYROLL')) return { ...data, id: 'mock-id' } as any;
    return apiClient.post('/payroll-item-definitions', data);
  }

  /**
   * 更新薪資項目定義
   */
  static async updatePayrollItemDefinition(id: string, data: Partial<PayrollItemDefinitionDto>): Promise<PayrollItemDefinitionDto> {
    if (MockConfig.isEnabled('PAYROLL')) return { ...data, id } as any;
    return apiClient.put(`/payroll-item-definitions/${id}`, data);
  }

  /**
   * 刪除薪資項目定義
   */
  static async deletePayrollItemDefinition(id: string): Promise<void> {
    if (MockConfig.isEnabled('PAYROLL')) return;
    return apiClient.delete(`/payroll-item-definitions/${id}`);
  }

  // ========== Bank Transfer Management ==========

  /**
   * 產生銀行薪轉檔案
   */
  static async generateBankTransferFile(runId: string): Promise<PayrollRunDto> {
    if (MockConfig.isEnabled('PAYROLL')) return { id: runId, status: 'BANK_TRANSFERRED' } as any;
    return apiClient.post(`/payroll-runs/${runId}/bank-transfer`, {});
  }

  /**
   * 取得銀行薪轉檔案下載 URL
   */
  static async getBankTransferDownloadUrl(runId: string): Promise<string> {
    // Mock not implemented yet
    return apiClient.get(`/payroll-runs/${runId}/bank-transfer/download`);
  }
}
