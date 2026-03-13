// @ts-nocheck
/**
 * TimesheetApi Adapter 單元測試
 *
 * 測試範圍：
 * 1. adaptStatus       — 後端 PENDING → 前端 SUBMITTED 映射、未知狀態回退
 * 2. adaptTimesheetSummary — 後端 camelCase → 前端 snake_case 欄位映射
 * 3. adaptTimesheetEntry   — 後端 entry camelCase → 前端 snake_case 欄位映射
 *
 * 三方對齊差異說明（Backend DTO ↔ Contract ↔ Frontend Types）：
 * - GetMyTimesheetResponse.TimesheetSummaryDto 缺少 employeeId / employeeName：
 *   adaptTimesheetSummary 會以空字串填補，前端顯示可能空白。
 * - GetTimesheetDetailResponse.TimesheetEntryDto 使用 entryId（非 id），
 *   adaptTimesheetEntry 已透過 raw.entryId ?? raw.id 處理。
 * - GetTimesheetSummaryResponse 回傳純量（projectHours: BigDecimal），
 *   前端期望 project_hours 陣列；adaptors 回退為空陣列。
 * - BatchApproveTimesheetResponse 使用 failureCount（非 failedCount），
 *   與合約 TSH_CMD_006 期望的 failedCount 不一致。
 * - GetUnreportedEmployeesResponse 缺少合約要求的 week / periodStartDate / periodEndDate 欄位。
 */

import { describe, it, expect } from 'vitest';

// ---------------------------------------------------------------------------
// 因為 adaptStatus / adaptTimesheetSummary / adaptTimesheetEntry 是模組私有函式，
// 本測試透過間接呼叫方式（直接複製純邏輯）驗證語義，確保不需修改生產程式碼即可測試。
// 若未來將這三個函式 export，直接改為 import 即可。
// ---------------------------------------------------------------------------

import { guardEnum } from '../../../shared/utils/adapterGuard';
import type { TimesheetStatus, TimesheetEntryDto, WeeklyTimesheetDto } from './TimesheetTypes';

// --- 從生產程式碼複製的 adapter 純函式（待 export 後改為 import）---

function adaptStatus(backendStatus: string): TimesheetStatus {
  const mapped = backendStatus === 'PENDING' ? 'SUBMITTED' : backendStatus;
  return guardEnum(
    'timesheet.status',
    mapped,
    ['DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'LOCKED'] as const,
    'DRAFT',
  );
}

function adaptTimesheetEntry(raw: any): TimesheetEntryDto {
  return {
    id: raw.entryId ?? raw.id ?? '',
    timesheet_id: raw.timesheetId,
    employee_id: raw.employeeId ?? '',
    employee_name: raw.employeeName ?? '',
    project_id: raw.projectId ?? '',
    project_name: raw.projectName ?? '',
    wbs_code: raw.wbsCode ?? raw.taskCode,
    wbs_name: raw.wbsName ?? raw.taskName,
    work_date: raw.workDate ?? '',
    hours: raw.hours ?? 0,
    description: raw.description,
    status: adaptStatus(raw.status ?? 'DRAFT'),
    created_at: raw.createdAt ?? '',
    updated_at: raw.updatedAt ?? '',
  };
}

function adaptTimesheetSummary(raw: any): WeeklyTimesheetDto {
  return {
    id: raw.timesheetId ?? raw.id ?? '',
    employee_id: raw.employeeId ?? '',
    employee_name: raw.employeeName ?? '',
    week_start_date: raw.periodStartDate ?? '',
    week_end_date: raw.periodEndDate ?? '',
    entries: (raw.entries ?? []).map(adaptTimesheetEntry),
    total_hours: raw.totalHours ?? 0,
    status: adaptStatus(raw.status ?? 'DRAFT'),
    submitted_at: raw.submittedAt,
    rejection_reason: raw.rejectionReason,
  };
}

// ===========================================================================
// 1. adaptStatus
// ===========================================================================

describe('adaptStatus', () => {
  describe('後端 PENDING → 前端 SUBMITTED 映射', () => {
    it('應將後端 PENDING 轉換為前端 SUBMITTED', () => {
      expect(adaptStatus('PENDING')).toBe('SUBMITTED');
    });
  });

  describe('已知狀態直接通過', () => {
    const passthrough: TimesheetStatus[] = ['DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'LOCKED'];
    passthrough.forEach((status) => {
      it(`應保留已知狀態 ${status}`, () => {
        expect(adaptStatus(status)).toBe(status);
      });
    });
  });

  describe('未知狀態處理', () => {
    // 注意：guardEnum 的設計是「警告但回傳原始值」，僅 null/undefined 才使用 fallback。
    // 這是 adapterGuard.ts 的刻意設計，避免靜默 fallback 隱藏後端新增的狀態值。
    // TODO: 若要求嚴格安全回退（未知值一律改為 DRAFT），應修改 guardEnum 或在 adaptStatus 加入
    //       額外的 includes 檢查，將非法值改為 fallback 而非透傳。

    it('未知字串狀態應透傳原始值（guardEnum 警告但不回退）', () => {
      // guardEnum 當值不在 allowedValues 內時：console.warn + return value as T（非 fallback）
      expect(adaptStatus('UNKNOWN_STATUS')).toBe('UNKNOWN_STATUS');
    });

    it('空字串狀態應透傳空字串（guardEnum 警告但不回退）', () => {
      // 空字串不是 null/undefined，故 guardEnum 回傳空字串而非 fallback
      expect(adaptStatus('')).toBe('');
    });

    it('小寫 pending 應透傳原始值（guardEnum 嚴格大小寫比對，不匹配則警告並透傳）', () => {
      expect(adaptStatus('pending')).toBe('pending');
    });

    it('後端未來新增的狀態值應透傳原始值（guardEnum 警告但不回退）', () => {
      expect(adaptStatus('CANCELLED')).toBe('CANCELLED');
    });

    it('null 應回退為 DRAFT（guardEnum null/undefined 判斷使用 fallback）', () => {
      expect(adaptStatus(null as unknown as string)).toBe('DRAFT');
    });

    it('undefined 應回退為 DRAFT（guardEnum null/undefined 判斷使用 fallback）', () => {
      expect(adaptStatus(undefined as unknown as string)).toBe('DRAFT');
    });
  });
});

// ===========================================================================
// 2. adaptTimesheetSummary
// ===========================================================================

describe('adaptTimesheetSummary', () => {
  describe('後端 camelCase 欄位完整映射', () => {
    it('應正確映射後端 GetPendingApprovalsResponse.TimesheetSummaryDto 的所有欄位', () => {
      // 模擬後端 GetPendingApprovalsResponse.TimesheetSummaryDto（含 employeeId/employeeName）
      const backendDto = {
        timesheetId: 'ts-uuid-002',
        employeeId: 'emp-uuid-001',
        employeeName: '王小明',
        periodStartDate: '2025-12-02',
        periodEndDate: '2025-12-08',
        totalHours: 32.0,
        status: 'PENDING',
        submittedAt: '2025-12-08T18:00:00',
      };

      const result = adaptTimesheetSummary(backendDto);

      expect(result.id).toBe('ts-uuid-002');
      expect(result.employee_id).toBe('emp-uuid-001');
      expect(result.employee_name).toBe('王小明');
      expect(result.week_start_date).toBe('2025-12-02');
      expect(result.week_end_date).toBe('2025-12-08');
      expect(result.total_hours).toBe(32.0);
      expect(result.status).toBe('SUBMITTED');        // PENDING → SUBMITTED
      expect(result.submitted_at).toBe('2025-12-08T18:00:00');
      expect(result.rejection_reason).toBeUndefined();
      expect(result.entries).toEqual([]);
    });

    it('應正確映射後端 GetMyTimesheetResponse.TimesheetSummaryDto（缺少 employeeId/employeeName）', () => {
      // GetMyTimesheetResponse.TimesheetSummaryDto 沒有 employeeId / employeeName
      // 這是三方差異：Backend DTO 欄位比 Contract 少
      const backendDtoWithoutEmployee = {
        timesheetId: 'ts-uuid-001',
        periodStartDate: '2025-11-25',
        periodEndDate: '2025-12-01',
        totalHours: 40.0,
        status: 'DRAFT',
        submittedAt: null,
        approvedAt: null,
        // 無 employeeId, employeeName
      };

      const result = adaptTimesheetSummary(backendDtoWithoutEmployee);

      expect(result.id).toBe('ts-uuid-001');
      expect(result.employee_id).toBe('');    // 回退空字串（已知差異）
      expect(result.employee_name).toBe(''); // 回退空字串（已知差異）
      expect(result.status).toBe('DRAFT');
    });

    it('應使用備用欄位 id 當 timesheetId 不存在時', () => {
      const raw = { id: 'fallback-id', status: 'DRAFT' };
      const result = adaptTimesheetSummary(raw);
      expect(result.id).toBe('fallback-id');
    });

    it('應映射 rejectionReason 至 rejection_reason', () => {
      const raw = {
        timesheetId: 'ts-uuid-005',
        status: 'REJECTED',
        rejectionReason: '工時與差勤記錄不符',
      };
      const result = adaptTimesheetSummary(raw);
      expect(result.status).toBe('REJECTED');
      expect(result.rejection_reason).toBe('工時與差勤記錄不符');
    });

    it('應遞迴轉換 entries 陣列', () => {
      const raw = {
        timesheetId: 'ts-uuid-001',
        status: 'DRAFT',
        entries: [
          {
            entryId: 'entry-001',
            timesheetId: 'ts-uuid-001',
            projectId: 'prj-uuid-001',
            projectName: 'Alpha 專案',
            workDate: '2025-11-25',
            hours: 8.0,
            description: '需求分析',
            status: 'DRAFT',
            createdAt: '2025-11-25T09:00:00',
            updatedAt: '2025-11-25T09:00:00',
          },
        ],
      };

      const result = adaptTimesheetSummary(raw);

      expect(result.entries).toHaveLength(1);
      expect(result.entries[0].id).toBe('entry-001');
      expect(result.entries[0].project_name).toBe('Alpha 專案');
    });
  });

  describe('null / undefined 處理', () => {
    it('所有欄位為 null 時應回退為安全預設值', () => {
      const raw = {
        timesheetId: null,
        employeeId: null,
        employeeName: null,
        periodStartDate: null,
        periodEndDate: null,
        totalHours: null,
        status: null,
        submittedAt: null,
        rejectionReason: null,
        entries: null,
      };

      const result = adaptTimesheetSummary(raw);

      expect(result.id).toBe('');
      expect(result.employee_id).toBe('');
      expect(result.employee_name).toBe('');
      expect(result.week_start_date).toBe('');
      expect(result.week_end_date).toBe('');
      expect(result.total_hours).toBe(0);
      expect(result.status).toBe('DRAFT');   // null → DRAFT 回退
      expect(result.submitted_at).toBeNull();
      expect(result.rejection_reason).toBeNull();
      expect(result.entries).toEqual([]);
    });

    it('完全空物件應回傳全部預設值', () => {
      const result = adaptTimesheetSummary({});

      expect(result.id).toBe('');
      expect(result.total_hours).toBe(0);
      expect(result.status).toBe('DRAFT');
      expect(result.entries).toEqual([]);
    });
  });
});

// ===========================================================================
// 3. adaptTimesheetEntry
// ===========================================================================

describe('adaptTimesheetEntry', () => {
  describe('後端 GetTimesheetDetailResponse.TimesheetEntryDto 完整映射', () => {
    it('應正確映射後端 entryId → 前端 id', () => {
      const backendEntry = {
        entryId: 'entry-001',
        timesheetId: 'ts-uuid-001',
        projectId: 'prj-uuid-001',
        workDate: '2025-11-25',
        hours: 8.0,
        description: '需求分析',
        // 後端 GetTimesheetDetailResponse.TimesheetEntryDto 無 projectName/employeeId/status
        // → adapter 以空字串 / DRAFT 回退
      };

      const result = adaptTimesheetEntry(backendEntry);

      expect(result.id).toBe('entry-001');              // entryId → id
      expect(result.timesheet_id).toBe('ts-uuid-001');
      expect(result.project_id).toBe('prj-uuid-001');
      expect(result.work_date).toBe('2025-11-25');
      expect(result.hours).toBe(8.0);
      expect(result.description).toBe('需求分析');
      expect(result.status).toBe('DRAFT');              // 後端無 status，預設 DRAFT
    });

    it('應使用備用欄位 id 當 entryId 不存在時', () => {
      const raw = { id: 'fallback-entry-id', workDate: '2025-11-25', status: 'APPROVED' };
      const result = adaptTimesheetEntry(raw);
      expect(result.id).toBe('fallback-entry-id');
      expect(result.status).toBe('APPROVED');
    });

    it('應映射 wbsCode → wbs_code 且優先於 taskCode', () => {
      const raw = { entryId: 'e1', wbsCode: 'WBS-001', taskCode: 'TASK-001' };
      const result = adaptTimesheetEntry(raw);
      expect(result.wbs_code).toBe('WBS-001');
    });

    it('應在 wbsCode 不存在時回退使用 taskCode', () => {
      const raw = { entryId: 'e1', taskCode: 'TASK-001' };
      const result = adaptTimesheetEntry(raw);
      expect(result.wbs_code).toBe('TASK-001');
    });

    it('應映射 wbsName → wbs_name 且優先於 taskName', () => {
      const raw = { entryId: 'e1', wbsName: 'WBS 需求分析', taskName: 'Task 需求分析' };
      const result = adaptTimesheetEntry(raw);
      expect(result.wbs_name).toBe('WBS 需求分析');
    });

    it('status PENDING 應映射為 SUBMITTED', () => {
      const raw = { entryId: 'e1', status: 'PENDING' };
      const result = adaptTimesheetEntry(raw);
      expect(result.status).toBe('SUBMITTED');
    });
  });

  describe('null / undefined 處理', () => {
    it('所有欄位為 undefined 時應回退為安全預設值', () => {
      const result = adaptTimesheetEntry({});

      expect(result.id).toBe('');
      expect(result.timesheet_id).toBeUndefined();
      expect(result.employee_id).toBe('');
      expect(result.employee_name).toBe('');
      expect(result.project_id).toBe('');
      expect(result.project_name).toBe('');
      expect(result.wbs_code).toBeUndefined();
      expect(result.wbs_name).toBeUndefined();
      expect(result.work_date).toBe('');
      expect(result.hours).toBe(0);
      expect(result.description).toBeUndefined();
      expect(result.status).toBe('DRAFT');
      expect(result.created_at).toBe('');
      expect(result.updated_at).toBe('');
    });

    it('entryId 和 id 都為 null 時 id 應為空字串', () => {
      const raw = { entryId: null, id: null };
      const result = adaptTimesheetEntry(raw);
      expect(result.id).toBe('');
    });

    it('hours 為 null 時應回退為 0', () => {
      const raw = { entryId: 'e1', hours: null };
      const result = adaptTimesheetEntry(raw);
      expect(result.hours).toBe(0);
    });
  });

  describe('合約測試資料驗證（對齊 timesheet_contracts.md 測試資料）', () => {
    it('entry-001 應正確轉換（合約資料 ts-uuid-001）', () => {
      // 對齊合約 §工時明細資料 entry-001
      const contractEntry = {
        entryId: 'entry-001',
        timesheetId: 'ts-uuid-001',
        projectId: 'prj-uuid-001',
        taskCode: 'task-001',
        workDate: '2025-11-25',
        hours: 8.0,
        description: '需求分析',
        status: 'DRAFT',
        createdAt: '2025-11-25T09:00:00',
        updatedAt: '2025-11-25T09:00:00',
      };

      const result = adaptTimesheetEntry(contractEntry);

      expect(result.id).toBe('entry-001');
      expect(result.timesheet_id).toBe('ts-uuid-001');
      expect(result.project_id).toBe('prj-uuid-001');
      expect(result.wbs_code).toBe('task-001');
      expect(result.work_date).toBe('2025-11-25');
      expect(result.hours).toBe(8.0);
      expect(result.description).toBe('需求分析');
      expect(result.status).toBe('DRAFT');
    });

    it('PENDING 狀態的工時表 ts-uuid-002 應映射為 SUBMITTED', () => {
      // 對齊合約 §工時表資料 ts-uuid-002（status: PENDING）
      const pendingTimesheet = {
        timesheetId: 'ts-uuid-002',
        employeeId: 'emp-uuid-001',
        employeeName: '測試員工',
        periodStartDate: '2025-12-02',
        periodEndDate: '2025-12-08',
        totalHours: 32.0,
        status: 'PENDING',
        submittedAt: '2025-12-08T18:00:00',
      };

      const result = adaptTimesheetSummary(pendingTimesheet);

      expect(result.id).toBe('ts-uuid-002');
      expect(result.status).toBe('SUBMITTED');
    });

    it('APPROVED + isLocked 的工時表 ts-uuid-004 應映射為 APPROVED', () => {
      // 對齊合約 §工時表資料 ts-uuid-004（status: APPROVED, is_locked: true）
      const approvedTimesheet = {
        timesheetId: 'ts-uuid-004',
        employeeId: 'emp-uuid-003',
        periodStartDate: '2025-11-25',
        periodEndDate: '2025-12-01',
        totalHours: 38.5,
        status: 'APPROVED',
      };

      const result = adaptTimesheetSummary(approvedTimesheet);

      expect(result.status).toBe('APPROVED');
      expect(result.total_hours).toBe(38.5);
    });
  });
});
