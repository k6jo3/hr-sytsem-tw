import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
    ApplyLeaveRequest,
    ApplyLeaveResponse,
    CreateLeaveTypeRequest,
    GetLeaveApplicationsRequest,
    GetLeaveApplicationsResponse,
    LeaveBalanceListResponse,
    UpdateLeaveTypeRequest
} from './AttendanceTypes';
import { MockAttendanceApi } from './MockAttendanceApi';

/**
 * Leave API (請假 API)
 * Domain Code: HR03
 */
export class LeaveApi {
  private static readonly BASE_PATH = '/leave';

  /**
   * 提交請假申請
   */
  static async applyLeave(request: ApplyLeaveRequest): Promise<ApplyLeaveResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.applyLeave(request);
    // 後端期望 leaveTypeId，前端表單可能用 leaveTypeCode 存放 leaveTypeId 值
    const payload = {
      employeeId: request.employeeId,
      leaveTypeId: request.leaveTypeCode || (request as any).leaveTypeId,
      startDate: request.startDate,
      endDate: request.endDate,
      reason: request.reason,
      proofAttachmentUrl: request.proofAttachmentUrl,
    };
    return apiClient.post(`${this.BASE_PATH}/applications`, payload);
  }

  /**
   * 查詢請假申請清單
   */
  static async getLeaveApplications(
    params?: GetLeaveApplicationsRequest
  ): Promise<GetLeaveApplicationsResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getLeaveApplications(params);
    return apiClient.get(`${this.BASE_PATH}/applications`, { params });
  }

  /**
   * 查詢假期餘額
   */
  static async getLeaveBalances(employeeId: string): Promise<LeaveBalanceListResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getLeaveBalance(employeeId);
    return apiClient.get(`${this.BASE_PATH}/balances/${employeeId}`);
  }

  /**
   * 查詢假別列表
   */
  static async getLeaveTypes(): Promise<any[]> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getLeaveTypes();
    const resp: any = await apiClient.get(`${this.BASE_PATH}/types`);
    // 後端可能回傳陣列或分頁物件（取 items / content / data）
    return Array.isArray(resp)
      ? resp
      : (resp?.items ?? resp?.content ?? resp?.data ?? []);
  }

  /**
   * 取消請假申請
   */
  static async cancelLeave(applicationId: string): Promise<void> {
    // Mock not implemented yet
    return apiClient.put(`${this.BASE_PATH}/applications/${applicationId}/cancel`, {});
  }

  /**
   * 核准請假申請 (主管用)
   */
  static async approveLeave(applicationId: string, remark?: string): Promise<void> {
    // Mock not implemented yet
    return apiClient.put(`${this.BASE_PATH}/applications/${applicationId}/approve`, { remark });
  }

  /**
   * 駁回請假申請 (主管用)
   */
  static async rejectLeave(applicationId: string, reason: string): Promise<void> {
    // Mock not implemented yet
    return apiClient.put(`${this.BASE_PATH}/applications/${applicationId}/reject`, { reason });
  }

  /**
   * 建立假別 (管理員用)
   * 自動補上 organizationId 與 boolean 欄位預設值
   */
  static async createLeaveType(request: CreateLeaveTypeRequest): Promise<void> {
    const payload = {
      ...request,
      organizationId: (request as any).organizationId || await this.getDefaultOrganizationId(),
      isPaid: request.isPaid ?? false,
      allowCarryOver: request.allowCarryOver ?? false,
      isActive: (request as any).isActive ?? true,
    };
    return apiClient.post(`${this.BASE_PATH}/types`, payload);
  }

  /**
   * 取得預設組織 ID（從組織列表取第一個）
   */
  private static async getDefaultOrganizationId(): Promise<string> {
    try {
      const resp: any = await apiClient.get('/organizations');
      const items = resp.items ?? resp.content ?? [];
      if (items.length > 0) {
        return items[0].organizationId;
      }
    } catch {
      // 忽略錯誤
    }
    return '';
  }

  /**
   * 更新假別 (管理員用)
   */
  static async updateLeaveType(leaveTypeId: string, request: UpdateLeaveTypeRequest): Promise<void> {
    // Mock not implemented yet
    return apiClient.put(`${this.BASE_PATH}/types/${leaveTypeId}`, request);
  }

  /**
   * 停用假別 (管理員用)
   */
  static async deactivateLeaveType(leaveTypeId: string): Promise<void> {
    // Mock not implemented yet
    return apiClient.put(`${this.BASE_PATH}/types/${leaveTypeId}/deactivate`, {});
  }
}
