import { apiClient } from '@shared/api';
import type { AttendanceDto, CheckInRequest, LeaveRequestDto } from './AttendanceTypes';

/**
 * Attendance API (考勤管理 API)
 * Domain Code: HR03
 */
export class AttendanceApi {
  private static readonly BASE_PATH = '/attendance';

  /**
   * 打卡
   */
  static async checkIn(request: CheckInRequest): Promise<AttendanceDto> {
    return apiClient.post(`${this.BASE_PATH}/check-in`, request);
  }

  /**
   * 取得考勤記錄列表
   */
  static async getAttendanceList(params?: { startDate?: string; endDate?: string }): Promise<AttendanceDto[]> {
    return apiClient.get(`${this.BASE_PATH}`, { params });
  }

  /**
   * 取得請假列表
   */
  static async getLeaveRequests(): Promise<LeaveRequestDto[]> {
    return apiClient.get(`${this.BASE_PATH}/leaves`);
  }
}
