import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
    ApproveCorrectionResponse,
    CheckInRequest,
    CheckInResponse,
    CheckOutRequest,
    CheckOutResponse,
    CreateCorrectionRequest,
    CreateCorrectionResponse,
    GetAttendanceHistoryRequest,
    GetAttendanceHistoryResponse,
    GetCorrectionListRequest,
    GetCorrectionListResponse,
    GetTodayAttendanceRequest,
    GetTodayAttendanceResponse
} from './AttendanceTypes';
import { MockAttendanceApi } from './MockAttendanceApi';

/**
 * Attendance API (考勤 API)
 * Domain Code: HR03
 */
export class AttendanceApi {
  private static readonly BASE_PATH = '/attendance';

  /**
   * 上班打卡
   */
  static async checkIn(request: CheckInRequest): Promise<CheckInResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.checkIn(request);
    return apiClient.post(`${this.BASE_PATH}/check-in`, request);
  }

  /**
   * 下班打卡
   */
  static async checkOut(request: CheckOutRequest): Promise<CheckOutResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.checkOut(request);
    return apiClient.post(`${this.BASE_PATH}/check-out`, request);
  }

  /**
   * 取得今日考勤記錄
   */
  static async getTodayAttendance(
    params?: GetTodayAttendanceRequest
  ): Promise<GetTodayAttendanceResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getTodayAttendance(params);
    return apiClient.get(`${this.BASE_PATH}/today`, { params });
  }

  /**
   * 取得考勤歷史記錄
   */
  static async getAttendanceHistory(
    params?: GetAttendanceHistoryRequest
  ): Promise<GetAttendanceHistoryResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getAttendanceHistory(params);
    return apiClient.get(`${this.BASE_PATH}/records`, { params });
  }

  /**
   * 提交補卡申請
   */
  static async createCorrection(request: CreateCorrectionRequest): Promise<CreateCorrectionResponse> {
    // Mock not implemented for corrections yet
    return apiClient.post(`${this.BASE_PATH}/corrections`, request);
  }

  /**
   * 查詢補卡申請列表 (主管用)
   */
  static async getCorrectionApplications(params?: GetCorrectionListRequest): Promise<GetCorrectionListResponse> {
    // Mock not implemented for corrections yet
    return apiClient.get(`${this.BASE_PATH}/corrections`, { params });
  }

  /**
   * 審核補卡申請 (主管用)
   */
  static async approveCorrection(correctionId: string, comment?: string): Promise<ApproveCorrectionResponse> {
    // Mock not implemented for corrections yet
    return apiClient.put(`${this.BASE_PATH}/corrections/${correctionId}/approve`, { comment });
  }
}
