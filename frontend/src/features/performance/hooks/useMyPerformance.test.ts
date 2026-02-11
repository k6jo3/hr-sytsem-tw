import { act, renderHook, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PerformanceApi } from '../api/PerformanceApi';
import { useMyPerformance } from './useMyPerformance';

// Mock PerformanceApi
vi.mock('../api/PerformanceApi', () => ({
  PerformanceApi: {
    getMyPerformance: vi.fn(),
    saveReview: vi.fn(),
    submitReview: vi.fn(),
  },
}));

// Mock PerformanceViewModelFactory
vi.mock('../factory/PerformanceViewModelFactory', () => ({
  PerformanceViewModelFactory: {
    createMyPerformanceViewModel: vi.fn((dto) => ({
      hasCurrentCycle: !!dto.current_cycle,
      currentCycle: dto.current_cycle ? {
        cycleId: dto.current_cycle.cycle_id,
        cycleName: dto.current_cycle.cycle_name,
      } : null,
      selfReview: dto.self_review,
      canSubmitSelfEval: dto.can_submit_self_eval,
    })),
  },
}));

describe('useMyPerformance', () => {
  const mockPerformance = {
    current_cycle: {
      cycle_id: 'cycle-001',
      cycle_name: '2025年度考核',
      cycle_type: 'ANNUAL',
      start_date: '2025-01-01',
      end_date: '2025-12-31',
      status: 'IN_PROGRESS',
      created_at: '2024-12-01T00:00:00Z',
    },
    self_review: {
      review_id: 'review-001',
      cycle_id: 'cycle-001',
      cycle_name: '2025年度考核',
      employee_id: 'emp-001',
      employee_name: '王小明',
      reviewer_id: 'emp-001',
      reviewer_name: '王小明',
      review_type: 'SELF',
      evaluation_items: [],
      status: 'DRAFT',
      created_at: '2025-12-01T09:00:00Z',
      updated_at: '2025-12-05T10:30:00Z',
    },
    history: [],
    can_submit_self_eval: true,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      vi.mocked(PerformanceApi.getMyPerformance).mockResolvedValue({
        performance: mockPerformance,
      });

      const { result } = renderHook(() => useMyPerformance());

      expect(result.current.loading).toBe(true);
      expect(result.current.performance).toBeNull();
      expect(result.current.error).toBeNull();
      expect(result.current.submitting).toBe(false);
    });
  });

  describe('取得我的績效', () => {
    it('應該成功取得績效資訊', async () => {
      vi.mocked(PerformanceApi.getMyPerformance).mockResolvedValue({
        performance: mockPerformance,
      });

      const { result } = renderHook(() => useMyPerformance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.performance).not.toBeNull();
      expect(result.current.performance?.hasCurrentCycle).toBe(true);
      expect(result.current.error).toBeNull();
    });

    it('應該正確處理 API 錯誤', async () => {
      const errorMessage = '無法取得考核資訊';
      vi.mocked(PerformanceApi.getMyPerformance).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useMyPerformance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.performance).toBeNull();
      expect(result.current.error).toBe(errorMessage);
    });
  });

  describe('儲存評估', () => {
    it('應該成功儲存評估', async () => {
      vi.mocked(PerformanceApi.getMyPerformance).mockResolvedValue({
        performance: mockPerformance,
      });
      vi.mocked(PerformanceApi.saveReview).mockResolvedValue(undefined);

      const { result } = renderHook(() => useMyPerformance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      let response;
      await act(async () => {
        response = await result.current.saveReview([
          { item_id: '1', item_name: '工作品質', weight: 0.3, score: 4, max_score: 5 },
        ]);
      });

      expect(response.success).toBe(true);
      expect(PerformanceApi.saveReview).toHaveBeenCalled();
    });
  });

  describe('提交評估', () => {
    it('應該成功提交評估', async () => {
      vi.mocked(PerformanceApi.getMyPerformance).mockResolvedValue({
        performance: mockPerformance,
      });
      vi.mocked(PerformanceApi.submitReview).mockResolvedValue({
        overall_score: 4.1,
        overall_rating: 'A',
      });

      const { result } = renderHook(() => useMyPerformance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      let response;
      await act(async () => {
        response = await result.current.submitReview('review-001');
      });

      expect(response.success).toBe(true);
      expect(response.score).toBe(4.1);
      expect(PerformanceApi.submitReview).toHaveBeenCalledWith('review-001', {});
    });
  });
});
