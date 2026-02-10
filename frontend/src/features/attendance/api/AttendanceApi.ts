import { apiClient } from '@shared/api';
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
    return apiClient.post(`${this.BASE_PATH}/check-in`, request);
  }

  /**
   * 下班打卡
   */
  static async checkOut(request: CheckOutRequest): Promise<CheckOutResponse> {
    return apiClient.post(`${this.BASE_PATH}/check-out`, request);
  }

  /**
   * 取得今日考勤記錄
   */
  static async getTodayAttendance(
    params?: GetTodayAttendanceRequest
  ): Promise<GetTodayAttendanceResponse> {
    return apiClient.get(`${this.BASE_PATH}/today`, { params });
  }

  /**
   * 取得考勤歷史記錄
   */
  static async getAttendanceHistory(
    params?: GetAttendanceHistoryRequest
  ): Promise<GetAttendanceHistoryResponse> {
    return apiClient.get(`${this.BASE_PATH}/records`, { params });
  }

  /**
   * 提交補卡申請
   */
  static async createCorrection(request: CreateCorrectionRequest): Promise<CreateCorrectionResponse> {
    return apiClient.post(`${this.BASE_PATH}/corrections`, request);
  }

  /**
   * 查詢補卡申請列表 (主管用)
   */
  static async getCorrectionApplications(params?: GetCorrectionListRequest): Promise<GetCorrectionListResponse> {
    return apiClient.get(`${this.BASE_PATH}/corrections`, { params });
  }

  /**
   * 審核補卡申請 (主管用)
   */
  static async approveCorrection(correctionId: string, comment?: string): Promise<ApproveCorrectionResponse> {
    return apiClient.put(`${this.BASE_PATH}/corrections/${correctionId}/approve`, { comment });
  }
}
