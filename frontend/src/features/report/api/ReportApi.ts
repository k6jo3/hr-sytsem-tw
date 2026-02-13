/**
 * Report API (報表分析 API)
 * Domain Code: HR14
 */

import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { MockReportingApi } from '../../../shared/api/SupportModuleMockApis';
import type {
    CreateScheduledReportRequest,
    GenerateReportRequest,
    GenerateReportResponse,
    GetDashboardRequest,
    GetDashboardResponse,
    GetReportDefinitionsRequest,
    GetReportDefinitionsResponse,
    GetReportsRequest,
    GetReportsResponse,
    GetScheduledReportsResponse,
} from './ReportTypes';

const BASE_URL = '/reports';

export const ReportApi = {
  // ========== Dashboard ==========

  /**
   * 取得儀表板資料
   */
  getDashboard: async (params?: GetDashboardRequest): Promise<GetDashboardResponse> => {
    if (MockConfig.isEnabled('REPORTING')) {
      const res = await MockReportingApi.getDashboardData();
      return res as any; // Adjust Mapping if needed
    }
    const response = await apiClient.get<GetDashboardResponse>(`${BASE_URL}/dashboard`, { params });
    return response as any;
  },

  // ========== Report Definitions ==========

  /**
   * 取得報表定義列表
   */
  getReportDefinitions: async (
    params?: GetReportDefinitionsRequest
  ): Promise<GetReportDefinitionsResponse> => {
    if (MockConfig.isEnabled('REPORTING')) return { definitions: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    const response = await apiClient.get<GetReportDefinitionsResponse>(`${BASE_URL}/definitions`, {
      params,
    });
    return response as any;
  },

  // ========== Reports ==========

  /**
   * 取得報表列表
   */
  getReports: async (params?: GetReportsRequest): Promise<GetReportsResponse> => {
    if (MockConfig.isEnabled('REPORTING')) return { reports: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    const response = await apiClient.get<GetReportsResponse>(BASE_URL, { params });
    return response as any;
  },

  /**
   * 產生報表
   */
  generateReport: async (request: GenerateReportRequest): Promise<GenerateReportResponse> => {
    if (MockConfig.isEnabled('REPORTING')) return { reportId: 'mock-report-id', message: '報表產生中 (Mock)' } as any;
    const response = await apiClient.post<GenerateReportResponse>(`${BASE_URL}/generate`, request);
    return response as any;
  },

  /**
   * 下載報表
   */
  downloadReport: async (reportId: string): Promise<{ download_url: string }> => {
    if (MockConfig.isEnabled('REPORTING')) return { download_url: '#' };
    const response = await apiClient.get<{ download_url: string }>(
      `${BASE_URL}/${reportId}/download`
    );
    return response as any;
  },

  /**
   * 刪除報表
   */
  deleteReport: async (reportId: string): Promise<{ message: string }> => {
    if (MockConfig.isEnabled('REPORTING')) return { message: '報表已刪除 (Mock)' };
    const response = await apiClient.delete<{ message: string }>(`${BASE_URL}/${reportId}`);
    return response as any;
  },

  // ========== Scheduled Reports ==========

  /**
   * 取得排程報表列表
   */
  getScheduledReports: async (): Promise<GetScheduledReportsResponse> => {
    if (MockConfig.isEnabled('REPORTING')) return { scheduled_reports: [], pagination: { page: 1, page_size: 10, total: 0, total_pages: 0 } };
    const response = await apiClient.get<GetScheduledReportsResponse>(`${BASE_URL}/scheduled`);
    return response as any;
  },

  /**
   * 建立排程報表
   */
  createScheduledReport: async (
    request: CreateScheduledReportRequest
  ): Promise<{ message: string }> => {
    if (MockConfig.isEnabled('REPORTING')) return { message: '排程已建立 (Mock)' };
    const response = await apiClient.post<{ message: string }>(`${BASE_URL}/scheduled`, request);
    return response as any;
  },

  /**
   * 刪除排程報表
   */
  deleteScheduledReport: async (scheduleId: string): Promise<{ message: string }> => {
    if (MockConfig.isEnabled('REPORTING')) return { message: '排程已刪除 (Mock)' };
    const response = await apiClient.delete<{ message: string }>(
      `${BASE_URL}/scheduled/${scheduleId}`
    );
    return response as any;
  },

  /**
   * 切換排程報表狀態
   */
  toggleScheduledReport: async (
    scheduleId: string,
    isActive: boolean
  ): Promise<{ message: string }> => {
    if (MockConfig.isEnabled('REPORTING')) return { message: '狀態已切換 (Mock)' };
    const response = await apiClient.patch<{ message: string }>(
      `${BASE_URL}/scheduled/${scheduleId}`,
      { is_active: isActive }
    );
    return response as any;
  },
};
