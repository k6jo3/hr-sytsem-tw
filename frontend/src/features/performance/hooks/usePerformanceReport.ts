import { message } from 'antd';
import { useCallback, useState } from 'react';
import { PerformanceApi } from '../api/PerformanceApi';
import type { GetDistributionRequest, PerformanceDistributionDto } from '../api/PerformanceTypes';

export const usePerformanceReport = () => {
  const [distribution, setDistribution] = useState<PerformanceDistributionDto[]>([]);
  const [stats, setStats] = useState<{ total: number; average: number }>({ total: 0, average: 0 });
  const [loading, setLoading] = useState(false);

  const fetchDistribution = useCallback(async (params: GetDistributionRequest) => {
    setLoading(true);
    try {
      const response = await PerformanceApi.getDistribution(params);
      setDistribution(response.distribution);
      setStats({
        total: response.total_employees,
        average: response.average_score,
      });
    } catch (error) {
      message.error('無法取得報表資料');
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    distribution,
    stats,
    loading,
    fetchDistribution
  };
};
