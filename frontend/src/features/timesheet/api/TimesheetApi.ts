import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { guardEnum } from '../../../shared/utils/adapterGuard';
import { MockTimesheetApi } from './MockTimesheetApi';
import type {
    BatchApprovalRequest,
    GetPendingApprovalsRequest,
    GetPendingApprovalsResponse,
    GetWeeklyTimesheetRequest,
    GetWeeklyTimesheetResponse,
    SaveTimesheetEntryRequest,
    SubmitTimesheetResponse,
    TimesheetApprovalRequest,
    TimesheetEntryDto,
    TimesheetReportSummaryDto,
    TimesheetStatus,
    WeeklyTimesheetDto,
} from './TimesheetTypes';

// ========== Response Adapters ==========
// 後端 camelCase → 前端 snake_case
// 後端 status PENDING → 前端 SUBMITTED

/**
 * 後端 status → 前端 status 映射
 * 後端用 PENDING，前端用 SUBMITTED
 */
function adaptStatus(backendStatus: string): TimesheetStatus {
  // 後端 PENDING → 前端 SUBMITTED 的特殊映射
  const mapped = backendStatus === 'PENDING' ? 'SUBMITTED' : backendStatus;
  return guardEnum('timesheet.status', mapped, ['DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED', 'LOCKED'] as const, 'DRAFT');
}

/**
 * 後端 TimesheetSummaryDto → 前端 WeeklyTimesheetDto
 */
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

/**
 * 後端 TimesheetEntryDto → 前端 TimesheetEntryDto
 */
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

/**
 * Timesheet API (工時管理 API)
 * Domain Code: HR07
 */
export class TimesheetApi {
  private static readonly BASE_PATH = '/timesheets';

  /**
   * 取得週工時記錄
   * 前端 GET /timesheets/weekly → 後端 GET /timesheets/my
   */
  static async getWeeklyTimesheet(params: GetWeeklyTimesheetRequest): Promise<GetWeeklyTimesheetResponse> {
    if (MockConfig.isEnabled('TIMESHEET')) return MockTimesheetApi.getWeeklyTimesheet(params);
    // 轉換參數: week_start_date → periodStartDate
    const backendParams: any = {};
    if (params.week_start_date) backendParams.periodStartDate = params.week_start_date;
    if (params.employee_id) backendParams.employeeId = params.employee_id;

    const raw: any = await apiClient.get(`${this.BASE_PATH}/my`, { params: backendParams });
    const items = raw.items ?? [];
    // 後端回傳 list，前端期望 single timesheet
    // 取第一筆匹配的工時表
    const firstItem = items[0];
    if (!firstItem) {
      // 無資料時返回空的 weekly timesheet
      return {
        timesheet: {
          id: '',
          employee_id: '',
          employee_name: '',
          week_start_date: params.week_start_date ?? '',
          week_end_date: '',
          entries: [],
          total_hours: 0,
          status: 'DRAFT',
          submitted_at: undefined,
          rejection_reason: undefined,
        },
      };
    }
    return { timesheet: adaptTimesheetSummary(firstItem) };
  }

  /**
   * POST /api/v1/timesheets/entry - 儲存工時明細
   * 前端 /entries → 後端 /entry
   */
  static async saveEntry(request: SaveTimesheetEntryRequest): Promise<void> {
    if (MockConfig.isEnabled('TIMESHEET')) {
      await MockTimesheetApi.saveTimesheetEntry(request);
      return;
    }
    // 轉換請求: snake_case → camelCase
    const backendRequest: any = {
      timesheetId: request.timesheet_id,
      projectId: request.project_id,
      wbsCode: request.wbs_code,
      workDate: request.work_date,
      hours: request.hours,
      description: request.description,
    };
    return apiClient.post(`${this.BASE_PATH}/entry`, backendRequest);
  }

  /**
   * DELETE /api/v1/timesheets/{id}/entries/{entryId} - 刪除工時明細
   */
  static async deleteEntry(timesheetId: string, entryId: string): Promise<void> {
    if (MockConfig.isEnabled('TIMESHEET')) return MockTimesheetApi.deleteTimesheetEntry(entryId);
    return apiClient.delete(`${this.BASE_PATH}/${timesheetId}/entries/${entryId}`);
  }

  /**
   * 提交週工時審核
   * 前端 PUT /timesheets/{id}/submit → 後端 POST /timesheets/submit
   */
  static async submitTimesheet(timesheetId: string): Promise<SubmitTimesheetResponse> {
    if (MockConfig.isEnabled('TIMESHEET')) return MockTimesheetApi.submitTimesheet(timesheetId);
    const raw: any = await apiClient.post(`${this.BASE_PATH}/submit`, { timesheetId });
    return {
      timesheet_id: raw.timesheetId ?? timesheetId,
      message: raw.message ?? '提交成功',
    };
  }

  // ========== Approval APIs ==========

  /**
   * 取得待審核工時列表
   * 前端 GET /timesheets/pending-approval → 後端 GET /timesheets/approvals
   */
  static async getPendingApprovals(params: GetPendingApprovalsRequest): Promise<GetPendingApprovalsResponse> {
    if (MockConfig.isEnabled('TIMESHEET')) return MockTimesheetApi.getPendingApprovals(params);
    // 轉換參數
    const backendParams: any = {};
    if (params.project_id) backendParams.projectId = params.project_id;
    if (params.employee_id) backendParams.employeeId = params.employee_id;
    if (params.page != null && params.page > 0) backendParams.page = params.page - 1;
    if (params.page_size) backendParams.size = params.page_size;

    const raw: any = await apiClient.get(`${this.BASE_PATH}/approvals`, { params: backendParams });
    const items = raw.items ?? [];
    return {
      timesheets: items.map(adaptTimesheetSummary),
      total: raw.total ?? items.length,
    };
  }

  /**
   * 核准工時
   * 前端 PUT → 後端 POST
   */
  static async approve(timesheetId: string): Promise<void> {
    if (MockConfig.isEnabled('TIMESHEET')) return MockTimesheetApi.approveTimesheet(timesheetId);
    return apiClient.post(`${this.BASE_PATH}/${timesheetId}/approve`);
  }

  /**
   * 駁回工時
   * 前端 PUT → 後端 POST
   */
  static async reject(timesheetId: string, request: TimesheetApprovalRequest): Promise<void> {
    if (MockConfig.isEnabled('TIMESHEET')) return MockTimesheetApi.rejectTimesheet(timesheetId, request);
    return apiClient.post(`${this.BASE_PATH}/${timesheetId}/reject`, {
      rejectionReason: request.rejection_reason,
    });
  }

  /**
   * PUT /api/v1/timesheets/batch-approve - 批次核准
   */
  static async batchApprove(request: BatchApprovalRequest): Promise<void> {
    if (MockConfig.isEnabled('TIMESHEET')) return;
    return apiClient.put(`${this.BASE_PATH}/batch-approve`, {
      timesheetIds: request.timesheet_ids,
    });
  }

  // ========== Report APIs ==========

  /**
   * GET /api/v1/timesheets/summary - 取得工時統計摘要
   */
  static async getSummary(params: { start_date: string; end_date: string }): Promise<TimesheetReportSummaryDto> {
    if (MockConfig.isEnabled('TIMESHEET')) return MockTimesheetApi.getTimesheetReport(params);
    const backendParams = {
      startDate: params.start_date,
      endDate: params.end_date,
    };
    const raw: any = await apiClient.get(`${this.BASE_PATH}/summary`, { params: backendParams });
    // 後端回傳 projectHours 為數字（總計），非陣列明細
    const projectsArr = Array.isArray(raw.projects) ? raw.projects : [];
    const deptArr = Array.isArray(raw.departmentHours) ? raw.departmentHours : [];
    const empArr = Array.isArray(raw.employees ?? raw.unreportedEmployees) ? (raw.employees ?? raw.unreportedEmployees) : [];
    return {
      total_hours: raw.totalHours ?? 0,
      project_hours: projectsArr.map((p: any) => ({
        project_name: p.projectName ?? p.project_name ?? '',
        hours: p.totalHours ?? p.hours ?? 0,
      })),
      department_hours: deptArr.map((d: any) => ({
        department_name: d.departmentName ?? d.department_name ?? '',
        hours: d.totalHours ?? d.hours ?? 0,
      })),
      unreported_employees: empArr.map((e: any) => ({
        id: e.employeeId ?? e.id ?? '',
        name: e.employeeName ?? e.name ?? '',
      })),
    };
  }
}
