import { renderHook, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { TimesheetApi } from '../api/TimesheetApi';
import type { TimesheetReportSummaryDto } from '../api/TimesheetTypes';
import { useTimesheetReport } from './useTimesheetReport';

// Mock TimesheetApi
vi.mock('../api/TimesheetApi', () => ({
  TimesheetApi: {
    getSummary: vi.fn(),
  },
}));

describe('useTimesheetReport', () => {
  const mockSummary: TimesheetReportSummaryDto = {
    total_hours: 320,
    project_hours: [
      { project_name: 'ERP專案', hours: 160 },
      { project_name: '系統維護', hours: 160 },
    ],
    department_hours: [
      { department_name: '資訊部', hours: 240 },
      { department_name: '研發部', hours: 80 },
    ],
    unreported_employees: [
      { id: 'emp-3', name: '張三' },
      { id: 'emp-4', name: '李四' },
    ],
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      vi.mocked(TimesheetApi.getSummary).mockResolvedValue(mockSummary);

      const { result } = renderHook(() =>
        useTimesheetReport('2024-12-01', '2024-12-31')
      );

      expect(result.current.loading).toBe(true);
      expect(result.current.summary).toBeNull();
      expect(result.current.error).toBeNull();
    });
  });

  describe('取得報表摘要', () => {
    it('應該成功取得報表摘要', async () => {
      vi.mocked(TimesheetApi.getSummary).mockResolvedValue(mockSummary);

      const { result } = renderHook(() =>
        useTimesheetReport('2024-12-01', '2024-12-31')
      );

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.summary).not.toBeNull();
      expect(result.current.summary?.total_hours).toBe(320);
      expect(result.current.summary?.project_hours).toHaveLength(2);
      expect(result.current.summary?.department_hours).toHaveLength(2);
      expect(result.current.summary?.unreported_employees).toHaveLength(2);
      expect(result.current.error).toBeNull();
    });

    it('應該使用正確的日期參數呼叫 API', async () => {
      vi.mocked(TimesheetApi.getSummary).mockResolvedValue(mockSummary);

      renderHook(() => useTimesheetReport('2024-12-01', '2024-12-31'));

      await waitFor(() => {
        expect(TimesheetApi.getSummary).toHaveBeenCalledWith({
          start_date: '2024-12-01',
          end_date: '2024-12-31',
        });
      });
    });

    it('應該正確處理 API 錯誤', async () => {
      const errorMessage = '載入報表失敗';
      vi.mocked(TimesheetApi.getSummary).mockRejectedValue(
        new Error(errorMessage)
      );

      // Mock console.error to avoid test output pollution
      const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

      const { result } = renderHook(() =>
        useTimesheetReport('2024-12-01', '2024-12-31')
      );

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.summary).toBeNull();
      expect(result.current.error).toBe(errorMessage);
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Failed to fetch timesheet summary:',
        expect.any(Error)
      );

      consoleErrorSpy.mockRestore();
    });
  });

  describe('重新整理', () => {
    it('應該能重新取得報表摘要', async () => {
      vi.mocked(TimesheetApi.getSummary).mockResolvedValue(mockSummary);

      const { result } = renderHook(() =>
        useTimesheetReport('2024-12-01', '2024-12-31')
      );

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(TimesheetApi.getSummary).toHaveBeenCalledTimes(1);

      await waitFor(async () => {
        await result.current.refresh();
      });

      await waitFor(() => {
        expect(TimesheetApi.getSummary).toHaveBeenCalledTimes(2);
      });
    });
  });

  describe('日期變更', () => {
    it('日期變更時應該重新取得資料', async () => {
      vi.mocked(TimesheetApi.getSummary).mockResolvedValue(mockSummary);

      const { rerender } = renderHook(
        ({ startDate, endDate }) => useTimesheetReport(startDate, endDate),
        {
          initialProps: {
            startDate: '2024-12-01',
            endDate: '2024-12-31',
          },
        }
      );

      await waitFor(() => {
        expect(TimesheetApi.getSummary).toHaveBeenCalledTimes(1);
      });

      // 變更日期
      rerender({
        startDate: '2025-01-01',
        endDate: '2025-01-31',
      });

      await waitFor(() => {
        expect(TimesheetApi.getSummary).toHaveBeenCalledTimes(2);
        expect(TimesheetApi.getSummary).toHaveBeenLastCalledWith({
          start_date: '2025-01-01',
          end_date: '2025-01-31',
        });
      });
    });
  });

  describe('載入狀態', () => {
    it('取得資料過程中 loading 應該為 true', async () => {
      vi.mocked(TimesheetApi.getSummary).mockImplementation(
        () =>
          new Promise((resolve) =>
            setTimeout(() => resolve(mockSummary), 50)
          )
      );

      const { result } = renderHook(() =>
        useTimesheetReport('2024-12-01', '2024-12-31')
      );

      expect(result.current.loading).toBe(true);

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });
    });
  });

  describe('報表資料結構', () => {
    it('應該正確處理專案工時統計', async () => {
      vi.mocked(TimesheetApi.getSummary).mockResolvedValue(mockSummary);

      const { result } = renderHook(() =>
        useTimesheetReport('2024-12-01', '2024-12-31')
      );

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      const projectHours = result.current.summary?.project_hours;
      expect(projectHours).toBeDefined();
      expect(projectHours![0]!.project_name).toBe('ERP專案');
      expect(projectHours![0]!.hours).toBe(160);
    });

    it('應該正確處理部門工時統計', async () => {
      vi.mocked(TimesheetApi.getSummary).mockResolvedValue(mockSummary);

      const { result } = renderHook(() =>
        useTimesheetReport('2024-12-01', '2024-12-31')
      );

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      const deptHours = result.current.summary?.department_hours;
      expect(deptHours).toBeDefined();
      expect(deptHours![0]!.department_name).toBe('資訊部');
      expect(deptHours![0]!.hours).toBe(240);
    });

    it('應該正確處理未填報員工列表', async () => {
      vi.mocked(TimesheetApi.getSummary).mockResolvedValue(mockSummary);

      const { result } = renderHook(() =>
        useTimesheetReport('2024-12-01', '2024-12-31')
      );

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      const unreported = result.current.summary?.unreported_employees;
      expect(unreported).toBeDefined();
      expect(unreported).toHaveLength(2);
      expect(unreported![0]!.name).toBe('張三');
    });
  });
});
