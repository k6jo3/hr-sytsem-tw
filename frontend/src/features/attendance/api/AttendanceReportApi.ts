import { apiClient } from '@shared/api';
import type {
    GetDailyReportResponse,
    GetMonthlyReportResponse,
} from './AttendanceTypes';

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
    return apiClient.get(`${this.BASE_PATH}/monthly`, { params });
  }

  /**
   * 查詢考勤日報表
   */
  static async getDailyReport(params: { date: string; departmentId?: string }): Promise<GetDailyReportResponse> {
    return apiClient.get(`${this.BASE_PATH}/daily`, { params });
  }
}
