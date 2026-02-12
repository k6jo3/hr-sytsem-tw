import { act, renderHook, waitFor } from '@testing-library/react';
import { message } from 'antd';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PerformanceApi } from '../api/PerformanceApi';
import type { PerformanceCycleDto } from '../api/PerformanceTypes';
import { useCycles } from './useCycles';

// Mock PerformanceApi
vi.mock('../api/PerformanceApi', () => ({
  PerformanceApi: {
    getCycles: vi.fn(),
    createCycle: vi.fn(),
    updateCycle: vi.fn(),
    deleteCycle: vi.fn(),
    startCycle: vi.fn(),
    syncLastYearCycle: vi.fn(),
  },
}));

// Mock antd message
vi.mock('antd', () => ({
  message: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

// Mock PerformanceViewModelFactory
vi.mock('../factory/PerformanceViewModelFactory', () => ({
  PerformanceViewModelFactory: {
    createCycleViewModel: vi.fn((dto) => ({
      cycleId: dto.cycle_id,
      cycleName: dto.cycle_name,
      cycleType: dto.cycle_type,
      cycleTypeLabel: dto.cycle_type === 'ANNUAL' ? '年度考核' : '試用期考核',
      status: dto.status,
      statusLabel: dto.status === 'IN_PROGRESS' ? '進行中' : '草稿',
      isInProgress: dto.status === 'IN_PROGRESS',
      isDraft: dto.status === 'DRAFT',
    })),
  },
}));

describe('useCycles', () => {
  const mockCycles: PerformanceCycleDto[] = [
    {
      cycle_id: 'cycle-001',
      cycle_name: '2025年度考核',
      cycle_type: 'ANNUAL' as const,
      start_date: '2025-01-01',
      end_date: '2025-12-31',
      status: 'IN_PROGRESS' as const,
      created_at: '2024-12-01T00:00:00Z',
    },
    {
      cycle_id: 'cycle-002',
      cycle_name: '2025 Q1考核',
      cycle_type: 'QUARTERLY' as const,
      start_date: '2025-01-01',
      end_date: '2025-03-31',
      status: 'DRAFT' as const,
      created_at: '2024-12-15T00:00:00Z',
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      const { result } = renderHook(() => useCycles());

      expect(result.current.cycles).toEqual([]);
      expect(result.current.loading).toBe(false);
      expect(result.current.total).toBe(0);
    });
  });

  describe('取得考核週期列表', () => {
    it('應該成功取得考核週期列表', async () => {
      vi.mocked(PerformanceApi.getCycles).mockResolvedValue({
        cycles: mockCycles,
        total: 2,
      });

      const { result } = renderHook(() => useCycles());

      await act(async () => {
        await result.current.fetchCycles();
      });

      expect(result.current.cycles).toHaveLength(2);
      expect(result.current.total).toBe(2);
      expect(PerformanceApi.getCycles).toHaveBeenCalled();
    });

    it('應該支援狀態篩選參數', async () => {
      vi.mocked(PerformanceApi.getCycles).mockResolvedValue({
        cycles: mockCycles,
        total: 2,
      });

      const { result } = renderHook(() => useCycles());

      await act(async () => {
        await result.current.fetchCycles({ status: 'IN_PROGRESS' });
      });

      expect(PerformanceApi.getCycles).toHaveBeenCalledWith({ status: 'IN_PROGRESS' });
    });

    it('應該支援類型篩選參數', async () => {
      vi.mocked(PerformanceApi.getCycles).mockResolvedValue({
        cycles: mockCycles,
        total: 2,
      });

      const { result } = renderHook(() => useCycles());

      await act(async () => {
        await result.current.fetchCycles({ cycle_type: 'ANNUAL' });
      });

      expect(PerformanceApi.getCycles).toHaveBeenCalledWith({ cycle_type: 'ANNUAL' });
    });

    it('應該正確處理 API 錯誤', async () => {
      vi.mocked(PerformanceApi.getCycles).mockRejectedValue(new Error('API Error'));

      const { result } = renderHook(() => useCycles());

      await act(async () => {
        await result.current.fetchCycles();
      });

      expect(message.error).toHaveBeenCalledWith('無法取得考核週期列表');
    });
  });

  describe('建立考核週期', () => {
    it('應該成功建立考核週期', async () => {
      vi.mocked(PerformanceApi.createCycle).mockResolvedValue({
        cycle_id: 'cycle-001',
        message: '建立成功',
      });
      vi.mocked(PerformanceApi.getCycles).mockResolvedValue({
        cycles: mockCycles,
        total: 2,
      });

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.createCycle({
          cycle_name: '2025年度考核',
          cycle_type: 'ANNUAL',
          start_date: '2025-01-01',
          end_date: '2025-12-31',
        });
      });

      expect(success).toBe(true);
      expect(PerformanceApi.createCycle).toHaveBeenCalled();
      expect(message.success).toHaveBeenCalledWith('考核週期建立成功');
      expect(PerformanceApi.getCycles).toHaveBeenCalled();
    });

    it('應該正確處理建立失敗', async () => {
      vi.mocked(PerformanceApi.createCycle).mockRejectedValue(new Error('Create failed'));

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.createCycle({
          cycle_name: '2025年度考核',
          cycle_type: 'ANNUAL',
          start_date: '2025-01-01',
          end_date: '2025-12-31',
        });
      });

      expect(success).toBe(false);
      expect(message.error).toHaveBeenCalledWith('建立失敗');
    });
  });

  describe('更新考核週期', () => {
    it('應該成功更新考核週期', async () => {
      vi.mocked(PerformanceApi.updateCycle).mockResolvedValue(undefined);
      vi.mocked(PerformanceApi.getCycles).mockResolvedValue({
        cycles: mockCycles,
        total: 2,
      });

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.updateCycle('cycle-001', {
          cycle_name: '2025年度考核(修訂)',
        });
      });

      expect(success).toBe(true);
      expect(PerformanceApi.updateCycle).toHaveBeenCalledWith('cycle-001', {
        cycle_name: '2025年度考核(修訂)',
      });
      expect(message.success).toHaveBeenCalledWith('考核週期更新成功');
    });

    it('應該正確處理更新失敗', async () => {
      vi.mocked(PerformanceApi.updateCycle).mockRejectedValue(new Error('Update failed'));

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.updateCycle('cycle-001', {
          cycle_name: '2025年度考核(修訂)',
        });
      });

      expect(success).toBe(false);
      expect(message.error).toHaveBeenCalledWith('更新失敗');
    });
  });

  describe('刪除考核週期', () => {
    it('應該成功刪除考核週期', async () => {
      vi.mocked(PerformanceApi.deleteCycle).mockResolvedValue(undefined);
      vi.mocked(PerformanceApi.getCycles).mockResolvedValue({
        cycles: mockCycles.slice(1),
        total: 1,
      });

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.deleteCycle('cycle-001');
      });

      expect(success).toBe(true);
      expect(PerformanceApi.deleteCycle).toHaveBeenCalledWith('cycle-001');
      expect(message.success).toHaveBeenCalledWith('考核週期已刪除');
    });

    it('應該正確處理刪除失敗', async () => {
      vi.mocked(PerformanceApi.deleteCycle).mockRejectedValue(new Error('Delete failed'));

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.deleteCycle('cycle-001');
      });

      expect(success).toBe(false);
      expect(message.error).toHaveBeenCalledWith('刪除失敗');
    });
  });

  describe('啟動考核週期', () => {
    it('應該成功啟動考核週期', async () => {
      vi.mocked(PerformanceApi.startCycle).mockResolvedValue({ message: '啟動成功' });
      vi.mocked(PerformanceApi.getCycles).mockResolvedValue({
        cycles: mockCycles,
        total: 2,
      });

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.startCycle('cycle-001');
      });

      expect(success).toBe(true);
      expect(PerformanceApi.startCycle).toHaveBeenCalledWith('cycle-001');
      expect(message.success).toHaveBeenCalledWith('考核週期已啟動');
    });

    it('應該正確處理啟動失敗', async () => {
      vi.mocked(PerformanceApi.startCycle).mockRejectedValue(new Error('Start failed'));

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.startCycle('cycle-001');
      });

      expect(success).toBe(false);
      expect(message.error).toHaveBeenCalledWith('啟動失敗');
    });
  });

  describe('同步去年週期', () => {
    it('應該成功同步去年週期', async () => {
      vi.mocked(PerformanceApi.syncLastYearCycle).mockResolvedValue(undefined);

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.syncLastYearCycle('cycle-001', 'cycle-2024');
      });

      expect(success).toBe(true);
      expect(PerformanceApi.syncLastYearCycle).toHaveBeenCalledWith('cycle-001', 'cycle-2024');
      expect(message.success).toHaveBeenCalledWith('同步成功');
    });

    it('應該正確處理同步失敗', async () => {
      vi.mocked(PerformanceApi.syncLastYearCycle).mockRejectedValue(new Error('Sync failed'));

      const { result } = renderHook(() => useCycles());

      let success;
      await act(async () => {
        success = await result.current.syncLastYearCycle('cycle-001', 'cycle-2024');
      });

      expect(success).toBe(false);
      expect(message.error).toHaveBeenCalledWith('同步失敗');
    });
  });

  describe('載入狀態', () => {
    it('操作過程中 loading 應該為 true', async () => {
      vi.mocked(PerformanceApi.getCycles).mockImplementation(
        () =>
          new Promise((resolve) =>
            setTimeout(
              () =>
                resolve({
                  cycles: mockCycles,
                  total: 2,
                }),
              50
            )
          )
      );

      const { result } = renderHook(() => useCycles());

      act(() => {
        result.current.fetchCycles();
      });

      expect(result.current.loading).toBe(true);

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });
    });
  });
});
