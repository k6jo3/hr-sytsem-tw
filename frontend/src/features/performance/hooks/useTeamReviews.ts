import { message } from 'antd';
import { useCallback, useState } from 'react';
import { PerformanceApi } from '../api/PerformanceApi';
import type { GetTeamReviewsRequest } from '../api/PerformanceTypes';
import { PerformanceViewModelFactory } from '../factory/PerformanceViewModelFactory';
import type { TeamReviewItemViewModel } from '../model/PerformanceViewModel';

export const useTeamReviews = () => {
  const [reviews, setReviews] = useState<TeamReviewItemViewModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);

  const fetchReviews = useCallback(async (params: GetTeamReviewsRequest) => {
    setLoading(true);
    try {
      const response = await PerformanceApi.getTeamReviews(params);
      const viewModels = response.reviews.map(dto => 
        PerformanceViewModelFactory.createTeamReviewItemViewModel(dto)
      );
      setReviews(viewModels);
      setTotal(response.total);
    } catch (error) {
      message.error('無法取得團隊考核列表');
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    reviews,
    loading,
    total,
    fetchReviews
  };
};
