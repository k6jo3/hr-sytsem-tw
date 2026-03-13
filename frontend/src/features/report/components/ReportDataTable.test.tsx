/**
 * ReportDataTable 元件測試
 * Domain Code: HR14
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { ReportDataTable } from './ReportDataTable';

// Mock hooks
vi.mock('../hooks', () => ({
  useReports: vi.fn(),
  useDownloadReport: vi.fn(),
  useDeleteReport: vi.fn(),
}));

import { useReports, useDownloadReport, useDeleteReport } from '../hooks';

const mockReports = [
  {
    reportId: 'rpt-1',
    definitionId: 'def-1',
    reportName: '2026年2月薪資總表',
    reportType: 'PAYROLL_SUMMARY',
    reportTypeLabel: '薪資報表',
    format: 'PDF',
    formatLabel: 'PDF',
    formatIcon: 'FilePdfOutlined',
    parameters: {},
    status: 'COMPLETED',
    statusLabel: '已完成',
    statusColor: 'green',
    generatedBy: 'user-1',
    generatedByName: '王大明',
    generatedAt: '2026-02-28T10:00:00Z',
    generatedAtDisplay: '2026-02-28 10:00',
    canDownload: true,
    isProcessing: false,
  },
  {
    reportId: 'rpt-2',
    definitionId: 'def-2',
    reportName: '出勤統計報表',
    reportType: 'ATTENDANCE_SUMMARY',
    reportTypeLabel: '出勤報表',
    format: 'EXCEL',
    formatLabel: 'EXCEL',
    formatIcon: 'FileExcelOutlined',
    parameters: {},
    status: 'GENERATING',
    statusLabel: '產生中',
    statusColor: 'blue',
    generatedBy: 'user-1',
    generatedByName: '王大明',
    canDownload: false,
    isProcessing: true,
  },
];

describe('ReportDataTable', () => {
  const mockDownload = vi.fn();
  const mockDelete = vi.fn();
  const mockRefetch = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useDownloadReport as ReturnType<typeof vi.fn>).mockReturnValue({
      mutate: mockDownload,
    });
    (useDeleteReport as ReturnType<typeof vi.fn>).mockReturnValue({
      mutate: mockDelete,
      isPending: false,
    });
  });

  it('應渲染報表歷史列表', () => {
    (useReports as ReturnType<typeof vi.fn>).mockReturnValue({
      data: {
        reports: mockReports,
        pagination: { total: 2, page: 1, page_size: 10, total_pages: 1 },
      },
      isLoading: false,
      refetch: mockRefetch,
    });

    render(<ReportDataTable />);

    expect(screen.getByText('2026年2月薪資總表')).toBeInTheDocument();
    expect(screen.getByText('出勤統計報表')).toBeInTheDocument();
    expect(screen.getByText('已完成')).toBeInTheDocument();
    expect(screen.getByText('產生中')).toBeInTheDocument();
  });

  it('已完成的報表應顯示下載按鈕', () => {
    (useReports as ReturnType<typeof vi.fn>).mockReturnValue({
      data: {
        reports: mockReports,
        pagination: { total: 2, page: 1, page_size: 10, total_pages: 1 },
      },
      isLoading: false,
      refetch: mockRefetch,
    });

    render(<ReportDataTable />);

    // 只有一個下載按鈕（第一筆 canDownload: true）
    const downloadButtons = screen.getAllByText('下載');
    expect(downloadButtons.length).toBe(1);
  });

  it('點擊下載應觸發 downloadReport', () => {
    (useReports as ReturnType<typeof vi.fn>).mockReturnValue({
      data: {
        reports: mockReports,
        pagination: { total: 2, page: 1, page_size: 10, total_pages: 1 },
      },
      isLoading: false,
      refetch: mockRefetch,
    });

    render(<ReportDataTable />);

    fireEvent.click(screen.getByText('下載'));
    expect(mockDownload).toHaveBeenCalledWith('rpt-1');
  });

  it('載入中應顯示 loading 狀態', () => {
    (useReports as ReturnType<typeof vi.fn>).mockReturnValue({
      data: undefined,
      isLoading: true,
      refetch: mockRefetch,
    });

    const { container } = render(<ReportDataTable />);

    expect(container.querySelector('.ant-spin')).toBeInTheDocument();
  });

  it('應渲染重新整理按鈕', () => {
    (useReports as ReturnType<typeof vi.fn>).mockReturnValue({
      data: {
        reports: [],
        pagination: { total: 0, page: 1, page_size: 10, total_pages: 0 },
      },
      isLoading: false,
      refetch: mockRefetch,
    });

    render(<ReportDataTable />);

    fireEvent.click(screen.getByText('重新整理'));
    expect(mockRefetch).toHaveBeenCalled();
  });

  it('應顯示分頁資訊', () => {
    (useReports as ReturnType<typeof vi.fn>).mockReturnValue({
      data: {
        reports: mockReports,
        pagination: { total: 2, page: 1, page_size: 10, total_pages: 1 },
      },
      isLoading: false,
      refetch: mockRefetch,
    });

    render(<ReportDataTable />);

    expect(screen.getByText('共 2 筆')).toBeInTheDocument();
  });
});
