/**
 * Report API (報表分析 API)
 * Domain Code: HR14
 */

import { apiClient } from '@shared/api';
import type {
  GetDashboardRequest,
  GetDashboardResponse,
  GetReportDefinitionsRequest,
  GetReportDefinitionsResponse,
  GetReportsRequest,
  GetReportsResponse,
  GenerateReportRequest,
  GenerateReportResponse,
  GetScheduledReportsResponse,
  CreateScheduledReportRequest,
} from './ReportTypes';

const BASE_URL = '/reports';

export const ReportApi = {
  // ========== Dashboard ==========

  /**
   * 取得儀表板資料
   */
  getDashboard: async (params?: GetDashboardRequest): Promise<GetDashboardResponse> => {
    const response = await apiClient.get<GetDashboardResponse>(`${BASE_URL}/dashboard`, { params });
    return response.data;
  },

  // ========== Report Definitions ==========

  /**
   * 取得報表定義列表
   */
  getReportDefinitions: async (
    params?: GetReportDefinitionsRequest
  ): Promise<GetReportDefinitionsResponse> => {
    const response = await apiClient.get<GetReportDefinitionsResponse>(`${BASE_URL}/definitions`, {
      params,
    });
    return response.data;
  },

  // ========== Reports ==========

  /**
   * 取得報表列表
   */
  getReports: async (params?: GetReportsRequest): Promise<GetReportsResponse> => {
    const response = await apiClient.get<GetReportsResponse>(BASE_URL, { params });
    return response.data;
  },

  /**
   * 產生報表
   */
  generateReport: async (request: GenerateReportRequest): Promise<GenerateReportResponse> => {
    const response = await apiClient.post<GenerateReportResponse>(`${BASE_URL}/generate`, request);
    return response.data;
  },

  /**
   * 下載報表
   */
  downloadReport: async (reportId: string): Promise<{ download_url: string }> => {
    const response = await apiClient.get<{ download_url: string }>(
      `${BASE_URL}/${reportId}/download`
    );
    return response.data;
  },

  /**
   * 刪除報表
   */
  deleteReport: async (reportId: string): Promise<{ message: string }> => {
    const response = await apiClient.delete<{ message: string }>(`${BASE_URL}/${reportId}`);
    return response.data;
  },

  // ========== Scheduled Reports ==========

  /**
   * 取得排程報表列表
   */
  getScheduledReports: async (): Promise<GetScheduledReportsResponse> => {
    const response = await apiClient.get<GetScheduledReportsResponse>(`${BASE_URL}/scheduled`);
    return response.data;
  },

  /**
   * 建立排程報表
   */
  createScheduledReport: async (
    request: CreateScheduledReportRequest
  ): Promise<{ message: string }> => {
    const response = await apiClient.post<{ message: string }>(`${BASE_URL}/scheduled`, request);
    return response.data;
  },

  /**
   * 刪除排程報表
   */
  deleteScheduledReport: async (scheduleId: string): Promise<{ message: string }> => {
    const response = await apiClient.delete<{ message: string }>(
      `${BASE_URL}/scheduled/${scheduleId}`
    );
    return response.data;
  },

  /**
   * 切換排程報表狀態
   */
  toggleScheduledReport: async (
    scheduleId: string,
    isActive: boolean
  ): Promise<{ message: string }> => {
    const response = await apiClient.patch<{ message: string }>(
      `${BASE_URL}/scheduled/${scheduleId}`,
      { is_active: isActive }
    );
    return response.data;
  },
};
