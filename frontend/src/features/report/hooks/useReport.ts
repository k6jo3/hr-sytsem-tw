/**
 * Report Hooks (報表分析 Hooks)
 * Domain Code: HR14
 */

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { message } from 'antd';
import { ReportApi } from '../api';
import { ReportViewModelFactory } from '../factory/ReportViewModelFactory';
import type {
  ReportType,
  ReportStatus,
  ReportPeriod,
  GenerateReportRequest,
} from '../api/ReportTypes';

// ========== Query Keys ==========

const REPORT_KEYS = {
  all: ['reports'] as const,
  dashboard: (params?: { period?: ReportPeriod }) => [...REPORT_KEYS.all, 'dashboard', params] as const,
  definitions: (params?: { reportType?: ReportType }) =>
    [...REPORT_KEYS.all, 'definitions', params] as const,
  reports: (params?: { reportType?: ReportType; status?: ReportStatus }) =>
    [...REPORT_KEYS.all, 'list', params] as const,
  scheduled: () => [...REPORT_KEYS.all, 'scheduled'] as const,
};

// ========== Dashboard Hooks ==========

/**
 * 取得儀表板資料
 */
export const useDashboard = (params?: {
  period?: ReportPeriod;
  startDate?: string;
  endDate?: string;
}) => {
  return useQuery({
    queryKey: REPORT_KEYS.dashboard(params),
    queryFn: async () => {
      const response = await ReportApi.getDashboard({
        period: params?.period,
        start_date: params?.startDate,
        end_date: params?.endDate,
      });
      return ReportViewModelFactory.createDashboardFromDTO(response);
    },
  });
};

// ========== Report Definition Hooks ==========

/**
 * 取得報表定義列表
 */
export const useReportDefinitions = (params?: {
  reportType?: ReportType;
  keyword?: string;
  page?: number;
  pageSize?: number;
}) => {
  return useQuery({
    queryKey: REPORT_KEYS.definitions(params),
    queryFn: async () => {
      const response = await ReportApi.getReportDefinitions();
      return {
        definitions: ReportViewModelFactory.createDefinitionListFromDTOs(response.definitions),
        pagination: response.pagination,
      };
    },
  });
};

// ========== Report Hooks ==========

/**
 * 取得報表列表
 */
export const useReports = (params?: {
  reportType?: ReportType;
  status?: ReportStatus;
  page?: number;
  pageSize?: number;
}) => {
  return useQuery({
    queryKey: REPORT_KEYS.reports(params),
    queryFn: async () => {
      const response = await ReportApi.getReports();
      return {
        reports: ReportViewModelFactory.createReportListFromDTOs(response.reports),
        pagination: response.pagination,
      };
    },
  });
};

/**
 * 產生報表
 */
export const useGenerateReport = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: GenerateReportRequest) => ReportApi.generateReport(request),
    onSuccess: () => {
      message.success('報表產生中，請稍候');
      queryClient.invalidateQueries({ queryKey: REPORT_KEYS.reports() });
    },
    onError: () => {
      message.error('報表產生失敗');
    },
  });
};

/**
 * 下載報表
 */
export const useDownloadReport = () => {
  return useMutation({
    mutationFn: async (reportId: string) => {
      const response = await ReportApi.downloadReport(reportId);
      window.open(response.download_url, '_blank');
      return response;
    },
    onError: () => {
      message.error('取得下載連結失敗');
    },
  });
};

/**
 * 刪除報表
 */
export const useDeleteReport = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (reportId: string) => ReportApi.deleteReport(reportId),
    onSuccess: () => {
      message.success('報表刪除成功');
      queryClient.invalidateQueries({ queryKey: REPORT_KEYS.reports() });
    },
    onError: () => {
      message.error('報表刪除失敗');
    },
  });
};

// ========== Scheduled Report Hooks ==========

/**
 * 取得排程報表列表
 */
export const useScheduledReports = () => {
  return useQuery({
    queryKey: REPORT_KEYS.scheduled(),
    queryFn: async () => {
      const response = await ReportApi.getScheduledReports();
      return ReportViewModelFactory.createScheduledReportListFromDTOs(response.scheduled_reports);
    },
  });
};

/**
 * 刪除排程報表
 */
export const useDeleteScheduledReport = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (scheduleId: string) => ReportApi.deleteScheduledReport(scheduleId),
    onSuccess: () => {
      message.success('排程報表刪除成功');
      queryClient.invalidateQueries({ queryKey: REPORT_KEYS.scheduled() });
    },
    onError: () => {
      message.error('排程報表刪除失敗');
    },
  });
};

/**
 * 切換排程報表狀態
 */
export const useToggleScheduledReport = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ scheduleId, isActive }: { scheduleId: string; isActive: boolean }) =>
      ReportApi.toggleScheduledReport(scheduleId, isActive),
    onSuccess: (_, { isActive }) => {
      message.success(isActive ? '排程報表已啟用' : '排程報表已停用');
      queryClient.invalidateQueries({ queryKey: REPORT_KEYS.scheduled() });
    },
    onError: () => {
      message.error('操作失敗');
    },
  });
};

// Re-export for backward compatibility
export const useReport = () => {
  const dashboardQuery = useDashboard();
  const reportsQuery = useReports();
  const generateMutation = useGenerateReport();

  return {
    dashboard: dashboardQuery.data,
    reports: reportsQuery.data?.reports ?? [],
    loading: dashboardQuery.isLoading || reportsQuery.isLoading,
    error: dashboardQuery.error || reportsQuery.error,
    generateReport: generateMutation.mutate,
    isGenerating: generateMutation.isPending,
  };
};
