import { apiClient } from '@shared/api';
import type {
    BatchApprovalRequest,
    GetPendingApprovalsRequest,
    GetPendingApprovalsResponse,
    GetWeeklyTimesheetRequest,
    GetWeeklyTimesheetResponse,
    SaveTimesheetEntryRequest,
    SubmitTimesheetResponse,
    TimesheetApprovalRequest,
    TimesheetReportSummaryDto
} from './TimesheetTypes';

/**
 * Timesheet API (工時管理 API)
 * Domain Code: HR07
 */
export class TimesheetApi {
  private static readonly BASE_PATH = '/timesheets';

  /**
   * GET /api/v1/timesheets/weekly - 取得週工時記錄
   */
  static async getWeeklyTimesheet(params: GetWeeklyTimesheetRequest): Promise<GetWeeklyTimesheetResponse> {
    return apiClient.get<GetWeeklyTimesheetResponse>(`${this.BASE_PATH}/weekly`, { params });
  }

  /**
   * POST /api/v1/timesheets/entries - 儲存工時明細
   */
  static async saveEntry(request: SaveTimesheetEntryRequest): Promise<void> {
    return apiClient.post(`${this.BASE_PATH}/entries`, request);
  }

  /**
   * DELETE /api/v1/timesheets/entries/{id} - 刪除工時明細
   */
  static async deleteEntry(entryId: string): Promise<void> {
    return apiClient.delete(`${this.BASE_PATH}/entries/${entryId}`);
  }

  /**
   * PUT /api/v1/timesheets/{id}/submit - 提交週工時審核
   */
  static async submitTimesheet(timesheetId: string): Promise<SubmitTimesheetResponse> {
    return apiClient.put<SubmitTimesheetResponse>(`${this.BASE_PATH}/${timesheetId}/submit`);
  }

  // ========== Approval APIs ==========

  /**
   * GET /api/v1/timesheets/pending-approval - 取得待審核工時列表
   */
  static async getPendingApprovals(params: GetPendingApprovalsRequest): Promise<GetPendingApprovalsResponse> {
    return apiClient.get<GetPendingApprovalsResponse>(`${this.BASE_PATH}/pending-approval`, { params });
  }

  /**
   * PUT /api/v1/timesheets/{id}/approve - 核准工時
   */
  static async approve(timesheetId: string): Promise<void> {
    return apiClient.put(`${this.BASE_PATH}/${timesheetId}/approve`);
  }

  /**
   * PUT /api/v1/timesheets/{id}/reject - 駁回工時
   */
  static async reject(timesheetId: string, request: TimesheetApprovalRequest): Promise<void> {
    return apiClient.put(`${this.BASE_PATH}/${timesheetId}/reject`, request);
  }

  /**
   * PUT /api/v1/timesheets/batch-approve - 批次核准
   */
  static async batchApprove(request: BatchApprovalRequest): Promise<void> {
    return apiClient.put(`${this.BASE_PATH}/batch-approve`, request);
  }

  // ========== Report APIs ==========

  /**
   * GET /api/v1/timesheets/summary - 取得工時統計摘要
   */
  static async getSummary(params: { start_date: string; end_date: string }): Promise<TimesheetReportSummaryDto> {
    return apiClient.get<TimesheetReportSummaryDto>(`${this.BASE_PATH}/summary`, { params });
  }
}
