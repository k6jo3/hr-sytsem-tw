import { useState, useCallback, useEffect } from 'react';
import { InsuranceApi } from '../api/InsuranceApi';
import { InsuranceViewModelFactory } from '../factory/InsuranceViewModelFactory';
import type { MyInsuranceInfoViewModel } from '../model/InsuranceViewModel';

/**
 * 我的保險資訊 Hook (ESS)
 */
export const useMyInsurance = () => {
  const [insuranceInfo, setInsuranceInfo] = useState<MyInsuranceInfoViewModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchMyInsurance = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await InsuranceApi.getMyInsurance();
      const viewModel = InsuranceViewModelFactory.createMyInsuranceInfoViewModel(
        response.insurance_info
      );
      setInsuranceInfo(viewModel);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得保險資訊');
      setInsuranceInfo(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchMyInsurance();
  }, [fetchMyInsurance]);

  return {
    insuranceInfo,
    loading,
    error,
    refresh: fetchMyInsurance,
  };
};
