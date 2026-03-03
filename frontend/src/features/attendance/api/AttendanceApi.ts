import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
    ApproveCorrectionResponse,
    AttendanceRecordDto,
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
 * 將後端每日出勤紀錄轉為前端 AttendanceRecordDto 格式
 * 後端格式：一筆 = 一天（含 checkInTime + checkOutTime）
 * 前端格式：一筆 = 一次打卡（checkType + checkTime）
 */
function adaptBackendRecords(backendItems: any[]): AttendanceRecordDto[] {
  const records: AttendanceRecordDto[] = [];
  for (const item of backendItems) {
    if (item.checkInTime) {
      records.push({
        id: `${item.recordId}-in`,
        employeeId: item.employeeId,
        employeeName: item.employeeName ?? '',
        checkType: 'CHECK_IN',
        checkTime: item.checkInTime,
        status: item.lateMinutes > 0 ? 'LATE' : 'NORMAL',
        createdAt: item.checkInTime,
      });
    }
    if (item.checkOutTime) {
      records.push({
        id: `${item.recordId}-out`,
        employeeId: item.employeeId,
        employeeName: item.employeeName ?? '',
        checkType: 'CHECK_OUT',
        checkTime: item.checkOutTime,
        status: item.earlyLeaveMinutes > 0 ? 'EARLY_LEAVE' : 'NORMAL',
        createdAt: item.checkOutTime,
      });
    }
  }
  return records;
}

/**
 * 將後端分頁回應轉為前端 GetAttendanceHistoryResponse 格式
 */
function adaptAttendanceHistoryResponse(raw: any): GetAttendanceHistoryResponse {
  return {
    records: adaptBackendRecords(raw.items ?? []),
    total: raw.totalElements ?? 0,
    page: raw.page ?? 1,
    pageSize: raw.size ?? 20,
  };
}

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
    const raw = await apiClient.get(`${this.BASE_PATH}/records`, { params });
    return adaptAttendanceHistoryResponse(raw);
  }

  /**
   * 提交補卡申請
   */
  static async createCorrection(request: CreateCorrectionRequest): Promise<CreateCorrectionResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return { success: true, message: '補卡申請已提交 (Mock)' } as any;
    return apiClient.post(`${this.BASE_PATH}/corrections`, request);
  }

  /**
   * 查詢補卡申請列表 (主管用)
   */
  static async getCorrectionApplications(params?: GetCorrectionListRequest): Promise<GetCorrectionListResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return { items: [], total: 0 } as any;
    return apiClient.get(`${this.BASE_PATH}/corrections`, { params });
  }

  /**
   * 審核補卡申請 (主管用)
   */
  static async approveCorrection(correctionId: string, comment?: string): Promise<ApproveCorrectionResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return { success: true, message: '補卡已核准 (Mock)' } as any;
    return apiClient.put(`${this.BASE_PATH}/corrections/${correctionId}/approve`, { comment });
  }
}
