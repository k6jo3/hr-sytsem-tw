import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
    GetDailyReportResponse,
    GetMonthlyReportResponse,
} from './AttendanceTypes';
import { MockAttendanceApi } from './MockAttendanceApi';

/**
 * Attendance Report API (考勤報表 API)
 * Domain Code: HR03
 */
export class AttendanceReportApi {
  private static readonly BASE_PATH = '/attendance/reports';

  /**
   * 查詢考勤月報表
   */
  static async getMonthlyReport(params: { year: number; month: number; departmentId?: string }): Promise<GetMonthlyReportResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getMonthlyReport(params.year, params.month);
    // organizationId 為後端必填參數，使用預設組織 ID
    const queryParams = {
      ...params,
      organizationId: '00000000-0000-0000-0000-000000000001',
    };
    return apiClient.get(`${this.BASE_PATH}/monthly`, { params: queryParams });
  }

  /**
   * 查詢考勤日報表
   */
  static async getDailyReport(params: { date: string; departmentId?: string }): Promise<GetDailyReportResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getDailyReport(params.date);
    return apiClient.get(`${this.BASE_PATH}/daily`, { params });
  }
}
