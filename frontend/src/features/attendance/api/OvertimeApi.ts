import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
    ApplyOvertimeRequest,
    ApplyOvertimeResponse,
    GetOvertimeApplicationsRequest,
    GetOvertimeApplicationsResponse,
    OvertimeApplicationDto,
} from './AttendanceTypes';
import { adaptOvertimeType } from './AttendanceApi';
import { MockAttendanceApi } from './MockAttendanceApi';

/**
 * 將後端加班申請項目的 overtimeType 標準化
 */
function adaptOvertimeItem(raw: any): OvertimeApplicationDto {
  return {
    ...raw,
    overtimeType: adaptOvertimeType(raw.overtimeType),
  };
}

/**
 * 將後端加班申請清單的 overtimeType 全部標準化
 */
function adaptOvertimeListResponse(raw: any): GetOvertimeApplicationsResponse {
  return {
    ...raw,
    items: (raw.items ?? []).map(adaptOvertimeItem),
  };
}

/**
 * Overtime API (加班 API)
 * Domain Code: HR03
 */
export class OvertimeApi {
  private static readonly BASE_PATH = '/overtime';

  /**
   * 提交加班申請
   */
  static async applyOvertime(request: ApplyOvertimeRequest): Promise<ApplyOvertimeResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.applyOvertime(request);
    return apiClient.post(`${this.BASE_PATH}/applications`, request);
  }

  /**
   * 查詢加班申請清單
   * 透過 adapter 標準化後端 overtimeType 枚舉值
   */
  static async getOvertimeApplications(
    params?: GetOvertimeApplicationsRequest
  ): Promise<GetOvertimeApplicationsResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getOvertimeApplications(params);
    const raw = await apiClient.get(`${this.BASE_PATH}/applications`, { params });
    return adaptOvertimeListResponse(raw);
  }

  /**
   * 核准加班申請 (主管用)
   */
  static async approveOvertime(overtimeId: string, remark?: string): Promise<void> {
    // Mock not implemented yet
    return apiClient.put(`${this.BASE_PATH}/applications/${overtimeId}/approve`, { remark });
  }

  /**
   * 駁回加班申請 (主管用)
   */
  static async rejectOvertime(overtimeId: string, reason: string): Promise<void> {
    // Mock not implemented yet
    return apiClient.put(`${this.BASE_PATH}/applications/${overtimeId}/reject`, { reason });
  }
}
