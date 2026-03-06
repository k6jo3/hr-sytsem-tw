/**
 * ReportCatalog 元件測試
 * Domain Code: HR14
 */

import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ReportCatalog } from './ReportCatalog';

// Mock hooks
vi.mock('../hooks', () => ({
  useReportDefinitions: vi.fn(),
  useGenerateReport: vi.fn(),
}));

import { useReportDefinitions, useGenerateReport } from '../hooks';

const mockDefinitions = [
  {
    definitionId: 'def-1',
    reportCode: 'RPT001',
    reportName: '員工總表',
    reportType: 'EMPLOYEE_SUMMARY',
    reportTypeLabel: '人事報表',
    reportTypeIcon: 'FileTextOutlined',
    description: '全體員工基本資料報表',
    parameters: [],
    availableFormats: ['PDF', 'EXCEL'],
    availableFormatsDisplay: 'PDF, EXCEL',
    isScheduled: false,
  },
  {
    definitionId: 'def-2',
    reportCode: 'RPT002',
    reportName: '出勤統計',
    reportType: 'ATTENDANCE_SUMMARY',
    reportTypeLabel: '出勤報表',
    reportTypeIcon: 'ClockCircleOutlined',
    description: '月度出勤統計報表',
    parameters: [],
    availableFormats: ['PDF'],
    availableFormatsDisplay: 'PDF',
    isScheduled: true,
  },
];

describe('ReportCatalog', () => {
  const mockGenerate = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useGenerateReport as ReturnType<typeof vi.fn>).mockReturnValue({
      mutate: mockGenerate,
      isPending: false,
    });
  });

  it('應渲染報表定義卡片', () => {
    (useReportDefinitions as ReturnType<typeof vi.fn>).mockReturnValue({
      data: { definitions: mockDefinitions, pagination: { total: 2 } },
      isLoading: false,
    });

    render(<ReportCatalog />);

    expect(screen.getByText('員工總表')).toBeInTheDocument();
    expect(screen.getByText('出勤統計')).toBeInTheDocument();
    expect(screen.getByText('全體員工基本資料報表')).toBeInTheDocument();
    expect(screen.getByText('月度出勤統計報表')).toBeInTheDocument();
    expect(screen.getByText('格式: PDF, EXCEL')).toBeInTheDocument();
  });

  it('載入中應顯示 Spin', () => {
    (useReportDefinitions as ReturnType<typeof vi.fn>).mockReturnValue({
      data: undefined,
      isLoading: true,
    });

    const { container } = render(<ReportCatalog />);

    expect(container.querySelector('.ant-spin')).toBeInTheDocument();
  });

  it('無報表定義時應顯示 Empty', () => {
    (useReportDefinitions as ReturnType<typeof vi.fn>).mockReturnValue({
      data: { definitions: [], pagination: { total: 0 } },
      isLoading: false,
    });

    render(<ReportCatalog />);

    expect(screen.getByText('暫無可用報表')).toBeInTheDocument();
  });

  it('點擊卡片應觸發產生報表', () => {
    (useReportDefinitions as ReturnType<typeof vi.fn>).mockReturnValue({
      data: { definitions: mockDefinitions, pagination: { total: 2 } },
      isLoading: false,
    });

    render(<ReportCatalog />);

    fireEvent.click(screen.getByText('員工總表'));

    expect(mockGenerate).toHaveBeenCalledWith({
      report_definition_id: 'def-1',
      format: 'PDF',
      parameters: {},
    });
  });
});
