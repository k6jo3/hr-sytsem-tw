import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { guardEnum } from '../../../shared/utils/adapterGuard';
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
    GetTodayAttendanceResponse,
    OvertimeType,
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
 * 後端 OvertimeType 枚舉映射
 * 後端可能回傳舊值 WEEKDAY/WEEKEND，需映射為新規格 WORKDAY/REST_DAY
 */
const OVERTIME_TYPE_MAP: Record<string, OvertimeType> = {
  WEEKDAY: 'WORKDAY',
  WEEKEND: 'REST_DAY',
};

/**
 * 標準化後端 overtimeType 值
 */
export function adaptOvertimeType(raw: string | null | undefined): OvertimeType {
  if (!raw) return 'WORKDAY';
  const mapped = OVERTIME_TYPE_MAP[raw];
  if (mapped) return mapped;
  return guardEnum(
    'overtime.overtimeType',
    raw,
    ['WORKDAY', 'REST_DAY', 'HOLIDAY'] as const,
    'WORKDAY'
  );
}

/**
 * 將後端 CreateCorrectionResponse 轉為前端格式
 * 後端欄位：correctionId, status, workflowInstanceId, createdAt
 * 前端需要：success, correctionId, status, workflowInstanceId, createdAt, message
 */
function adaptCreateCorrectionResponse(raw: any): CreateCorrectionResponse {
  return {
    success: !!raw.correctionId,
    correctionId: raw.correctionId ?? '',
    status: raw.status ?? 'PENDING',
    workflowInstanceId: raw.workflowInstanceId,
    createdAt: raw.createdAt,
    message: raw.correctionId ? '補卡申請已提交' : '補卡申請失敗',
  };
}

/**
 * 將後端 ApproveCorrectionResponse 轉為前端格式
 * 後端欄位：correctionId, status, approvedBy, approvedAt
 * 前端需要：success, correctionId, status, approvedBy, approvedAt, message
 */
function adaptApproveCorrectionResponse(raw: any): ApproveCorrectionResponse {
  const isApproved = raw.status === 'APPROVED';
  return {
    success: isApproved,
    correctionId: raw.correctionId ?? '',
    status: raw.status ?? '',
    approvedBy: raw.approvedBy,
    approvedAt: raw.approvedAt,
    message: isApproved ? '補卡已核准' : `補卡審核結果: ${raw.status ?? '未知'}`,
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
   * 透過 adapter 將後端 correctionId/status 結構轉為前端 success/message 結構
   */
  static async createCorrection(request: CreateCorrectionRequest): Promise<CreateCorrectionResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return { success: true, correctionId: 'MOCK-001', status: 'PENDING', message: '補卡申請已提交 (Mock)' };
    const raw = await apiClient.post(`${this.BASE_PATH}/corrections`, request);
    return adaptCreateCorrectionResponse(raw);
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
   * 透過 adapter 將後端 correctionId/status/approvedBy 結構轉為前端格式
   */
  static async approveCorrection(correctionId: string, comment?: string): Promise<ApproveCorrectionResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return { success: true, correctionId, status: 'APPROVED', approvedBy: 'MOCK-MGR', message: '補卡已核准 (Mock)' };
    const raw = await apiClient.put(`${this.BASE_PATH}/corrections/${correctionId}/approve`, { comment });
    return adaptApproveCorrectionResponse(raw);
  }
}
