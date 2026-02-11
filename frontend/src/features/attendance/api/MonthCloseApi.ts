import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
    ExecuteMonthCloseRequest,
    ExecuteMonthCloseResponse,
} from './AttendanceTypes';
import { MockAttendanceApi } from './MockAttendanceApi';

/**
 * Month Close API (月結管理 API)
 * Domain Code: HR03
 */
export class MonthCloseApi {
  private static readonly BASE_PATH = '/attendance/month-close';

  /**
   * 執行考勤月結
   */
  static async executeMonthClose(request: ExecuteMonthCloseRequest): Promise<ExecuteMonthCloseResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.executeMonthClose(request);
    return apiClient.post(this.BASE_PATH, request);
  }
}
