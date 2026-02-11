import { useCallback, useState } from 'react';
import { InsuranceApi } from '../api/InsuranceApi';
import { InsuranceViewModelFactory } from '../factory/InsuranceViewModelFactory';
import type { MyInsuranceInfoViewModel } from '../model/InsuranceViewModel';

/**
 * useMyInsurance Hook
 * 用戶端 (ESS) 查詢個人保險資訊
 */
export const useMyInsurance = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [insuranceInfo, setInsuranceInfo] = useState<MyInsuranceInfoViewModel | null>(null);

  const fetchMyInsurance = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await InsuranceApi.getMyInsurance();
      const vm = InsuranceViewModelFactory.createMyInsuranceInfoViewModel(response.insurance_info);
      setInsuranceInfo(vm);
    } catch (err: any) {
      setError(err.message || '載入個人保險資訊失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    insuranceInfo,
    loading,
    error,
    refresh: fetchMyInsurance
  };
};
