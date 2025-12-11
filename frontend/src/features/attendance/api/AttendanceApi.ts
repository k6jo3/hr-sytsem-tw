import { apiClient } from '@shared/api';
import type {
  CheckInRequest,
  CheckInResponse,
  GetTodayAttendanceRequest,
  GetTodayAttendanceResponse,
  GetAttendanceHistoryRequest,
  GetAttendanceHistoryResponse,
} from './AttendanceTypes';

/**
 * Attendance API (考勤 API)
 * Domain Code: HR03
 */
export class AttendanceApi {
  private static readonly BASE_PATH = '/attendance';

  /**
   * 打卡
   */
  static async checkIn(request: CheckInRequest): Promise<CheckInResponse> {
    return apiClient.post(`${this.BASE_PATH}/check-in`, request);
  }

  /**
   * 取得今日考勤記錄
   */
  static async getTodayAttendance(
    params?: GetTodayAttendanceRequest
  ): Promise<GetTodayAttendanceResponse> {
    return apiClient.get(`${this.BASE_PATH}/today`, params);
  }

  /**
   * 取得考勤歷史記錄
   */
  static async getAttendanceHistory(
    params?: GetAttendanceHistoryRequest
  ): Promise<GetAttendanceHistoryResponse> {
    return apiClient.get(`${this.BASE_PATH}/history`, params);
  }
}
