import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useTimesheet } from './useTimesheet';
import * as TimesheetApi from '../api/TimesheetApi';
import type { WeeklyTimesheetDto } from '../api/TimesheetTypes';

// Mock TimesheetApi
vi.mock('../api/TimesheetApi', () => ({
  getWeeklyTimesheet: vi.fn(),
  submitTimesheet: vi.fn(),
}));

describe('useTimesheet', () => {
  const mockWeeklyData: WeeklyTimesheetDto = {
    week_start_date: '2024-12-02',
    week_end_date: '2024-12-08',
    entries: [
      {
        id: '1',
        employee_id: 'emp-1',
        employee_name: '王小明',
        project_id: 'proj-1',
        project_name: 'HR系統',
        work_date: '2024-12-08',
        hours: 8,
        status: 'DRAFT',
        created_at: '2024-12-08T18:00:00Z',
        updated_at: '2024-12-08T18:00:00Z',
      },
    ],
    total_hours: 40,
    status: 'DRAFT',
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      vi.mocked(TimesheetApi.getWeeklyTimesheet).mockResolvedValue({
        timesheet: mockWeeklyData,
      });

      const { result } = renderHook(() => useTimesheet('2024-12-02'));

      expect(result.current.loading).toBe(true);
      expect(result.current.summary).toBeNull();
      expect(result.current.error).toBeNull();
    });
  });

  describe('取得週工時', () => {
    it('應該成功取得週工時資料', async () => {
      vi.mocked(TimesheetApi.getWeeklyTimesheet).mockResolvedValue({
        timesheet: mockWeeklyData,
      });

      const { result } = renderHook(() => useTimesheet('2024-12-02'));

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.summary).not.toBeNull();
      expect(result.current.summary?.totalHours).toBe(40);
      expect(result.current.summary?.entries).toHaveLength(1);
      expect(result.current.error).toBeNull();
    });

    it('應該正確處理API錯誤', async () => {
      const errorMessage = '無法取得工時記錄';
      vi.mocked(TimesheetApi.getWeeklyTimesheet).mockRejectedValue(
        new Error(errorMessage)
      );

      const { result } = renderHook(() => useTimesheet('2024-12-02'));

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.summary).toBeNull();
      expect(result.current.error).toBe(errorMessage);
    });
  });

  describe('提交工時', () => {
    it('應該成功提交工時', async () => {
      vi.mocked(TimesheetApi.getWeeklyTimesheet).mockResolvedValue({
        timesheet: mockWeeklyData,
      });

      vi.mocked(TimesheetApi.submitTimesheet).mockResolvedValue({
        timesheet_id: 'ts-1',
        message: '提交成功',
      });

      const { result } = renderHook(() => useTimesheet('2024-12-02'));

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      await act(async () => {
        await result.current.handleSubmit();
      });

      expect(TimesheetApi.submitTimesheet).toHaveBeenCalled();
    });
  });
});
