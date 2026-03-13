import { act, renderHook, waitFor } from '@testing-library/react';
import { message } from 'antd';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { TimesheetApi } from '../api/TimesheetApi';
import { useTimesheetApproval } from './useTimesheetApproval';

// Mock TimesheetApi
vi.mock('../api/TimesheetApi', () => ({
  TimesheetApi: {
    getPendingApprovals: vi.fn(),
    approve: vi.fn(),
    reject: vi.fn(),
    batchApprove: vi.fn(),
  },
}));

// Mock antd message
vi.mock('antd', () => ({
  message: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

// Mock TimesheetViewModelFactory
vi.mock('../factory/TimesheetViewModelFactory', () => ({
  TimesheetViewModelFactory: {
    createWeeklySummary: vi.fn((dto) => ({
      id: dto.id,
      weekStartDate: dto.week_start_date,
      weekEndDate: dto.week_end_date,
      totalHours: dto.total_hours,
      statusLabel: '已提交',
      canSubmit: false,
      canEdit: false,
      entries: dto.entries || [],
    })),
  },
}));

describe('useTimesheetApproval', () => {
  const mockTimesheets = [
    {
      id: 'ts-1',
      employee_id: 'emp-1',
      employee_name: '王小明',
      week_start_date: '2024-12-02',
      week_end_date: '2024-12-08',
      entries: [],
      total_hours: 40,
      status: 'SUBMITTED' as const,
      submitted_at: '2024-12-08T18:00:00Z',
    },
    {
      id: 'ts-2',
      employee_id: 'emp-2',
      employee_name: '李小華',
      week_start_date: '2024-12-02',
      week_end_date: '2024-12-08',
      entries: [],
      total_hours: 38,
      status: 'SUBMITTED' as const,
      submitted_at: '2024-12-08T18:00:00Z',
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      const { result } = renderHook(() => useTimesheetApproval());

      expect(result.current.pendingTimesheets).toEqual([]);
      expect(result.current.total).toBe(0);
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBeNull();
    });
  });

  describe('取得待審核列表', () => {
    it('應該成功取得待審核工時列表', async () => {
      vi.mocked(TimesheetApi.getPendingApprovals).mockResolvedValue({
        timesheets: mockTimesheets,
        total: 2,
      });

      const { result } = renderHook(() => useTimesheetApproval());

      await act(async () => {
        await result.current.fetchPendingApprovals();
      });

      expect(result.current.pendingTimesheets).toHaveLength(2);
      expect(result.current.total).toBe(2);
      expect(result.current.error).toBeNull();
      expect(TimesheetApi.getPendingApprovals).toHaveBeenCalled();
    });

    it('應該支援專案篩選參數', async () => {
      vi.mocked(TimesheetApi.getPendingApprovals).mockResolvedValue({
        timesheets: mockTimesheets,
        total: 2,
      });

      const { result } = renderHook(() => useTimesheetApproval());

      await act(async () => {
        await result.current.fetchPendingApprovals('proj-1', 1, 10);
      });

      expect(TimesheetApi.getPendingApprovals).toHaveBeenCalledWith({
        project_id: 'proj-1',
        page: 1,
        page_size: 10,
      });
    });

    it('應該正確處理 API 錯誤', async () => {
      const errorMessage = '無法取得待審核列表';
      vi.mocked(TimesheetApi.getPendingApprovals).mockRejectedValue(
        new Error(errorMessage)
      );

      const { result } = renderHook(() => useTimesheetApproval());

      await act(async () => {
        await result.current.fetchPendingApprovals();
      });

      expect(result.current.error).toBe(errorMessage);
      expect(result.current.pendingTimesheets).toEqual([]);
    });
  });

  describe('核准工時', () => {
    it('應該成功核准工時', async () => {
      vi.mocked(TimesheetApi.approve).mockResolvedValue(undefined);
      vi.mocked(TimesheetApi.getPendingApprovals).mockResolvedValue({
        timesheets: mockTimesheets.slice(1),
        total: 1,
      });

      const { result } = renderHook(() => useTimesheetApproval());

      await act(async () => {
        await result.current.handleApprove('ts-1');
      });

      expect(TimesheetApi.approve).toHaveBeenCalledWith('ts-1');
      expect(message.success).toHaveBeenCalledWith('工時已核准');
      expect(TimesheetApi.getPendingApprovals).toHaveBeenCalled();
    });

    it('應該正確處理核准失敗', async () => {
      const errorMessage = '核准失敗';
      vi.mocked(TimesheetApi.approve).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useTimesheetApproval());

      await act(async () => {
        await result.current.handleApprove('ts-1');
      });

      expect(message.error).toHaveBeenCalledWith(errorMessage);
    });
  });

  describe('駁回工時', () => {
    it('應該成功駁回工時', async () => {
      vi.mocked(TimesheetApi.reject).mockResolvedValue(undefined);
      vi.mocked(TimesheetApi.getPendingApprovals).mockResolvedValue({
        timesheets: mockTimesheets.slice(1),
        total: 1,
      });

      const { result } = renderHook(() => useTimesheetApproval());

      await act(async () => {
        await result.current.handleReject('ts-1', '工時記錄不完整');
      });

      expect(TimesheetApi.reject).toHaveBeenCalledWith('ts-1', {
        rejection_reason: '工時記錄不完整',
      });
      expect(message.success).toHaveBeenCalledWith('工時已駁回');
      expect(TimesheetApi.getPendingApprovals).toHaveBeenCalled();
    });

    it('應該正確處理駁回失敗', async () => {
      const errorMessage = '駁回失敗';
      vi.mocked(TimesheetApi.reject).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useTimesheetApproval());

      await act(async () => {
        await result.current.handleReject('ts-1', '工時記錄不完整');
      });

      expect(message.error).toHaveBeenCalledWith(errorMessage);
    });
  });

  describe('批次核准', () => {
    it('應該成功批次核准工時', async () => {
      vi.mocked(TimesheetApi.batchApprove).mockResolvedValue(undefined);
      vi.mocked(TimesheetApi.getPendingApprovals).mockResolvedValue({
        timesheets: [],
        total: 0,
      });

      const { result } = renderHook(() => useTimesheetApproval());

      await act(async () => {
        await result.current.handleBatchApprove(['ts-1', 'ts-2']);
      });

      expect(TimesheetApi.batchApprove).toHaveBeenCalledWith({
        timesheet_ids: ['ts-1', 'ts-2'],
      });
      expect(message.success).toHaveBeenCalledWith('已核准 2 筆工時記錄');
      expect(TimesheetApi.getPendingApprovals).toHaveBeenCalled();
    });

    it('應該正確處理批次核准失敗', async () => {
      const errorMessage = '批次核准失敗';
      vi.mocked(TimesheetApi.batchApprove).mockRejectedValue(
        new Error(errorMessage)
      );

      const { result } = renderHook(() => useTimesheetApproval());

      await act(async () => {
        await result.current.handleBatchApprove(['ts-1', 'ts-2']);
      });

      expect(message.error).toHaveBeenCalledWith(errorMessage);
    });
  });

  describe('載入狀態', () => {
    it('操作過程中 loading 應該為 true', async () => {
      vi.mocked(TimesheetApi.getPendingApprovals).mockImplementation(
        () =>
          new Promise((resolve) =>
            setTimeout(
              () =>
                resolve({
                  timesheets: mockTimesheets,
                  total: 2,
                }),
              50
            )
          )
      );

      const { result } = renderHook(() => useTimesheetApproval());

      act(() => {
        result.current.fetchPendingApprovals();
      });

      expect(result.current.loading).toBe(true);

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });
    });
  });
});
