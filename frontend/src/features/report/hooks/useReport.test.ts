import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import React from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useDashboard, useReportDefinitions, useReports } from './useReport';
import { ReportApi } from '../api';

vi.mock('../api', () => ({
  ReportApi: {
    getDashboard: vi.fn(),
    getReportDefinitions: vi.fn(),
    getReports: vi.fn(),
    generateReport: vi.fn(),
    downloadReport: vi.fn(),
    deleteReport: vi.fn(),
    getScheduledReports: vi.fn(),
    deleteScheduledReport: vi.fn(),
    toggleScheduledReport: vi.fn(),
  },
}));

vi.mock('antd', async () => {
  const actual = await vi.importActual('antd');
  return { ...actual, message: { success: vi.fn(), error: vi.fn() } };
});

const mockDashboardResponse = {
  kpis: {
    total_employees: 150,
    active_employees: 140,
    new_hires_this_month: 5,
    turnover_rate: 8.5,
    average_attendance_rate: 95.2,
    pending_leave_requests: 12,
    overtime_hours_this_month: 320,
    training_completion_rate: 78.3,
  },
  headcount_trend: [
    { month: '2026-01', headcount: 140, new_hires: 5, terminations: 2 },
  ],
  department_distribution: [
    { department_id: 'dept-001', department_name: '研發部', employee_count: 45, percentage: 30.0 },
  ],
  attendance_stats: [
    { date: '2026-03-05', present_count: 135, absent_count: 5, late_count: 3, leave_count: 7, attendance_rate: 95.0 },
  ],
  salary_distribution: [
    { range: '30K-40K', count: 25, percentage: 16.7 },
  ],
};

const mockDefinitionsResponse = {
  definitions: [
    {
      id: 'def-001',
      report_code: 'RPT_EMP',
      report_name: '員工統計報表',
      report_type: 'EMPLOYEE_SUMMARY' as const,
      description: '員工統計',
      parameters: [],
      available_formats: ['PDF' as const, 'EXCEL' as const],
      is_scheduled: false,
      created_at: '2026-01-01T00:00:00Z',
      updated_at: '2026-03-01T00:00:00Z',
    },
  ],
  pagination: { page: 1, page_size: 10, total: 1, total_pages: 1 },
};

const mockReportsResponse = {
  reports: [
    {
      id: 'rpt-001',
      report_definition_id: 'def-001',
      report_name: '員工統計報表',
      report_type: 'EMPLOYEE_SUMMARY' as const,
      format: 'PDF' as const,
      parameters: {},
      status: 'COMPLETED' as const,
      download_url: '/api/v1/reports/rpt-001/download',
      generated_by: 'user-001',
      generated_by_name: '王大明',
      generated_at: '2026-03-05T10:00:00Z',
      created_at: '2026-03-05T09:59:00Z',
      updated_at: '2026-03-05T10:00:00Z',
    },
  ],
  pagination: { page: 1, page_size: 10, total: 1, total_pages: 1 },
};

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });
  return ({ children }: { children: React.ReactNode }) =>
    React.createElement(QueryClientProvider, { client: queryClient }, children);
}

describe('useDashboard', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確載入儀表板資料', async () => {
    vi.mocked(ReportApi.getDashboard).mockResolvedValue(mockDashboardResponse);

    const { result } = renderHook(() => useDashboard({ period: 'MONTHLY' }), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data?.kpis.totalEmployees).toBe(150);
    expect(result.current.data?.kpis.turnoverRateDisplay).toBe('8.5%');
    expect(result.current.data?.headcountTrend).toHaveLength(1);
    expect(result.current.data?.departmentDistribution).toHaveLength(1);
  });

  it('應正確處理錯誤', async () => {
    vi.mocked(ReportApi.getDashboard).mockRejectedValue(new Error('載入失敗'));

    const { result } = renderHook(() => useDashboard(), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isError).toBe(true);
    });
  });
});

describe('useReportDefinitions', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確載入報表定義', async () => {
    vi.mocked(ReportApi.getReportDefinitions).mockResolvedValue(mockDefinitionsResponse);

    const { result } = renderHook(() => useReportDefinitions(), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data?.definitions).toHaveLength(1);
    expect(result.current.data?.definitions[0].reportTypeLabel).toBe('員工統計報表');
  });
});

describe('useReports', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確載入報表列表', async () => {
    vi.mocked(ReportApi.getReports).mockResolvedValue(mockReportsResponse);

    const { result } = renderHook(() => useReports(), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data?.reports).toHaveLength(1);
    expect(result.current.data?.reports[0].statusLabel).toBe('已完成');
    expect(result.current.data?.reports[0].canDownload).toBe(true);
  });
});
