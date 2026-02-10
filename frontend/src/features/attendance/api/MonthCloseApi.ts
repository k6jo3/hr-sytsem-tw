import { apiClient } from '@shared/api';
import type {
    ExecuteMonthCloseRequest,
    ExecuteMonthCloseResponse,
} from './AttendanceTypes';

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
    return apiClient.post(this.BASE_PATH, request);
  }
}
