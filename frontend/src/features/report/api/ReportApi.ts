/**
 * Report API (報表分析 API)
 * Domain Code: HR14
 */

import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { MockReportingApi } from '../../../shared/api/SupportModuleMockApis';
import type {
    CreateScheduledReportRequest,
    DepartmentDistributionDto,
    GenerateReportRequest,
    GenerateReportResponse,
    GetDashboardRequest,
    GetDashboardResponse,
    GetReportDefinitionsResponse,
    GetReportsResponse,
    GetScheduledReportsResponse,
} from './ReportTypes';

const BASE_URL = '/reporting';

// ========== Response Adapter ==========

/** 從 employee roster 資料建構部門分佈 */
function buildDeptDistribution(employees: any[]): DepartmentDistributionDto[] {
  const deptMap = new Map<string, { id: string; name: string; count: number }>();
  employees.forEach((e: any) => {
    const deptName = e.departmentName ?? e.department_name ?? '未知部門';
    const deptId = e.departmentId ?? e.department_id ?? deptName;
    const existing = deptMap.get(deptName) ?? { id: deptId, name: deptName, count: 0 };
    existing.count++;
    deptMap.set(deptName, existing);
  });
  const total = employees.length || 1;
  return Array.from(deptMap.values()).map((dept) => ({
    department_id: dept.id,
    department_name: dept.name,
    employee_count: dept.count,
    percentage: Math.round((dept.count / total) * 100),
  }));
}

/** 建構空白的 Dashboard 回應 */
function emptyDashboard(): GetDashboardResponse {
  return {
    kpis: {
      total_employees: 0, active_employees: 0, new_hires_this_month: 0,
      turnover_rate: 0, average_attendance_rate: 0, pending_leave_requests: 0,
      overtime_hours_this_month: 0, training_completion_rate: 0,
    },
    headcount_trend: [],
    department_distribution: [],
    attendance_stats: [],
    salary_distribution: [],
  };
}

export const ReportApi = {
  // ========== Dashboard ==========

  /**
   * 取得儀表板資料
   * 後端無統一 KPI endpoint，從多個報表 API 組合資料
   */
  getDashboard: async (_params?: GetDashboardRequest): Promise<GetDashboardResponse> => {
    if (MockConfig.isEnabled('REPORT')) {
      const res = await MockReportingApi.getDashboardData();
      return res as any;
    }
    try {
      const [rosterRaw, attendanceRaw, _payrollRaw] = await Promise.all([
        apiClient.get<any>(`${BASE_URL}/hr/employee-roster`).catch(() => ({ content: [] })),
        apiClient.get<any>(`${BASE_URL}/hr/attendance-statistics`).catch(() => ({ content: [] })),
        apiClient.get<any>(`${BASE_URL}/finance/payroll-summary`).catch(() => ({ content: [] })),
      ]);

      const employees = rosterRaw.content ?? [];
      const attStats = attendanceRaw.content ?? [];

      const avgAttRate = attStats.length > 0
        ? attStats.reduce((sum: number, a: any) => sum + (a.attendanceRate ?? 0), 0) / attStats.length
        : 0;
      const totalOT = attStats.reduce((sum: number, a: any) => sum + (a.overtimeHours ?? 0), 0);

      return {
        kpis: {
          total_employees: employees.length,
          active_employees: employees.filter((e: any) => e.status === 'ACTIVE' || !e.resignationDate).length,
          new_hires_this_month: 0,
          turnover_rate: 0,
          average_attendance_rate: Math.round(avgAttRate * 100) / 100,
          pending_leave_requests: 0,
          overtime_hours_this_month: totalOT,
          training_completion_rate: 0,
        },
        headcount_trend: [],
        department_distribution: buildDeptDistribution(employees),
        attendance_stats: attStats.map((a: any) => ({
          date: a.statDate ?? a.stat_date ?? a.employeeName ?? a.employee_name ?? '',
          present_count: a.actualDays ?? a.actual_days ?? 0,
          absent_count: a.absentCount ?? a.absent_count ?? 0,
          late_count: a.lateCount ?? a.late_count ?? 0,
          leave_count: 0,
          attendance_rate: a.attendanceRate ?? a.attendance_rate ?? 0,
        })),
        salary_distribution: [],
      };
    } catch {
      return emptyDashboard();
    }
  },

  // ========== Report Definitions ==========

  /**
   * 取得報表定義列表
   * 注意：後端無對應 endpoint，回傳空資料
   */
  getReportDefinitions: async (): Promise<GetReportDefinitionsResponse> => {
    if (MockConfig.isEnabled('REPORT')) return { definitions: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    return { definitions: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
  },

  // ========== Reports ==========

  /**
   * 取得報表列表
   * 注意：後端無對應 endpoint，回傳空資料
   */
  getReports: async (): Promise<GetReportsResponse> => {
    if (MockConfig.isEnabled('REPORT')) return { reports: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    return { reports: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
  },

  /**
   * 產生報表
   */
  generateReport: async (request: GenerateReportRequest): Promise<GenerateReportResponse> => {
    if (MockConfig.isEnabled('REPORT')) return { reportId: 'mock-report-id', message: '報表產生中 (Mock)' } as any;
    const response = await apiClient.post<any>(`${BASE_URL}/reports/generate/hr`, request);
    return { report: response, message: response.message ?? '報表產生中' };
  },

  /**
   * 下載報表
   */
  downloadReport: async (reportId: string): Promise<{ download_url: string }> => {
    if (MockConfig.isEnabled('REPORT')) return { download_url: '#' };
    const response = await apiClient.get<any>(`${BASE_URL}/export/${reportId}/download`);
    return { download_url: response.downloadUrl ?? response.download_url ?? '#' };
  },

  /**
   * 刪除報表
   */
  deleteReport: async (reportId: string): Promise<{ message: string }> => {
    if (MockConfig.isEnabled('REPORT')) return { message: '報表已刪除 (Mock)' };
    const response = await apiClient.delete<{ message: string }>(`${BASE_URL}/reports/${reportId}`);
    return response;
  },

  // ========== Scheduled Reports ==========

  /**
   * 取得排程報表列表
   * 注意：後端無對應 endpoint，回傳空資料
   */
  getScheduledReports: async (): Promise<GetScheduledReportsResponse> => {
    if (MockConfig.isEnabled('REPORT')) return { scheduled_reports: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    return { scheduled_reports: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
  },

  /**
   * 建立排程報表
   */
  createScheduledReport: async (
    _request: CreateScheduledReportRequest
  ): Promise<{ message: string }> => {
    if (MockConfig.isEnabled('REPORT')) return { message: '排程已建立 (Mock)' };
    return { message: '後端尚未實作排程報表 API' };
  },

  /**
   * 刪除排程報表
   */
  deleteScheduledReport: async (_scheduleId: string): Promise<{ message: string }> => {
    if (MockConfig.isEnabled('REPORT')) return { message: '排程已刪除 (Mock)' };
    return { message: '後端尚未實作排程報表 API' };
  },

  /**
   * 切換排程報表狀態
   */
  toggleScheduledReport: async (
    _scheduleId: string,
    _isActive: boolean
  ): Promise<{ message: string }> => {
    if (MockConfig.isEnabled('REPORT')) return { message: '狀態已切換 (Mock)' };
    return { message: '後端尚未實作排程報表 API' };
  },
};
