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
    return apiClient.get(`${this.BASE_PATH}/monthly`, { params });
  }

  /**
   * 查詢考勤日報表
   */
  static async getDailyReport(params: { date: string; departmentId?: string }): Promise<GetDailyReportResponse> {
    if (MockConfig.isEnabled('ATTENDANCE')) return MockAttendanceApi.getDailyReport(params.date);
    return apiClient.get(`${this.BASE_PATH}/daily`, { params });
  }
}
