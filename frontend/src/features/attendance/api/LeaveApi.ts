import { apiClient } from '@shared/api';
import type {
    ApplyLeaveRequest,
    ApplyLeaveResponse,
    CreateLeaveTypeRequest,
    GetLeaveApplicationsRequest,
    GetLeaveApplicationsResponse,
    LeaveBalanceListResponse,
    UpdateLeaveTypeRequest
} from './AttendanceTypes';

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
    return apiClient.post(`${this.BASE_PATH}/applications`, request);
  }

  /**
   * 查詢請假申請清單
   */
  static async getLeaveApplications(
    params?: GetLeaveApplicationsRequest
  ): Promise<GetLeaveApplicationsResponse> {
    return apiClient.get(`${this.BASE_PATH}/applications`, { params });
  }

  /**
   * 查詢假期餘額
   */
  static async getLeaveBalances(employeeId: string): Promise<LeaveBalanceListResponse> {
    return apiClient.get(`${this.BASE_PATH}/balances/${employeeId}`);
  }

  /**
   * 查詢假別列表
   */
  static async getLeaveTypes(): Promise<any[]> {
    return apiClient.get(`${this.BASE_PATH}/types`);
  }

  /**
   * 取消請假申請
   */
  static async cancelLeave(applicationId: string): Promise<void> {
    return apiClient.put(`${this.BASE_PATH}/applications/${applicationId}/cancel`, {});
  }

  /**
   * 核准請假申請 (主管用)
   */
  static async approveLeave(applicationId: string, remark?: string): Promise<void> {
    return apiClient.put(`${this.BASE_PATH}/applications/${applicationId}/approve`, { remark });
  }

  /**
   * 駁回請假申請 (主管用)
   */
  static async rejectLeave(applicationId: string, reason: string): Promise<void> {
    return apiClient.put(`${this.BASE_PATH}/applications/${applicationId}/reject`, { reason });
  }

  /**
   * 建立假別 (管理員用)
   */
  static async createLeaveType(request: CreateLeaveTypeRequest): Promise<void> {
    return apiClient.post(`${this.BASE_PATH}/types`, request);
  }

  /**
   * 更新假別 (管理員用)
   */
  static async updateLeaveType(leaveTypeId: string, request: UpdateLeaveTypeRequest): Promise<void> {
    return apiClient.put(`${this.BASE_PATH}/types/${leaveTypeId}`, request);
  }

  /**
   * 停用假別 (管理員用)
   */
  static async deactivateLeaveType(leaveTypeId: string): Promise<void> {
    return apiClient.put(`${this.BASE_PATH}/types/${leaveTypeId}/deactivate`, {});
  }
}
