import { useState, useCallback, useEffect } from 'react';
import { PerformanceApi } from '../api/PerformanceApi';
import { PerformanceViewModelFactory } from '../factory/PerformanceViewModelFactory';
import type { MyPerformanceViewModel } from '../model/PerformanceViewModel';
import type { SaveReviewRequest, EvaluationItemDto } from '../api/PerformanceTypes';

/**
 * 我的績效考核 Hook (ESS)
 */
export const useMyPerformance = () => {
  const [performance, setPerformance] = useState<MyPerformanceViewModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchMyPerformance = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await PerformanceApi.getMyPerformance();
      const viewModel = PerformanceViewModelFactory.createMyPerformanceViewModel(
        response.performance
      );
      setPerformance(viewModel);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得考核資訊');
      setPerformance(null);
    } finally {
      setLoading(false);
    }
  }, []);

  const saveReview = useCallback(
    async (evaluationItems: EvaluationItemDto[], comments?: string) => {
      if (!performance?.currentCycle) {
        throw new Error('目前無進行中的考核週期');
      }

      setSubmitting(true);
      try {
        const request: SaveReviewRequest = {
          cycle_id: performance.currentCycle.cycleId,
          review_type: 'SELF',
          evaluation_items: evaluationItems,
          comments,
        };

        await PerformanceApi.saveReview(request);
        await fetchMyPerformance(); // Refresh data
        return { success: true, message: '儲存成功' };
      } catch (err) {
        throw new Error(err instanceof Error ? err.message : '儲存失敗');
      } finally {
        setSubmitting(false);
      }
    },
    [performance, fetchMyPerformance]
  );

  const submitReview = useCallback(async (reviewId: string) => {
    setSubmitting(true);
    try {
      const response = await PerformanceApi.submitReview(reviewId, {});
      await fetchMyPerformance(); // Refresh data
      return {
        success: true,
        message: '提交成功',
        score: response.overall_score,
        rating: response.overall_rating,
      };
    } catch (err) {
      throw new Error(err instanceof Error ? err.message : '提交失敗');
    } finally {
      setSubmitting(false);
    }
  }, [fetchMyPerformance]);

  useEffect(() => {
    fetchMyPerformance();
  }, [fetchMyPerformance]);

  return {
    performance,
    loading,
    error,
    submitting,
    refresh: fetchMyPerformance,
    saveReview,
    submitReview,
  };
};
