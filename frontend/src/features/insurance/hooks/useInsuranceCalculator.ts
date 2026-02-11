import { useState } from 'react';
import { InsuranceApi } from '../api/InsuranceApi';
import { InsuranceViewModelFactory } from '../factory/InsuranceViewModelFactory';
import type { InsuranceFeesViewModel } from '../model/InsuranceViewModel';

/**
 * useInsuranceCalculator Hook
 * 處理保險費用試算邏輯
 */
export const useInsuranceCalculator = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<InsuranceFeesViewModel | null>(null);
  const [levelNumber, setLevelNumber] = useState<number | null>(null);

  const calculateFees = async (monthlySalary: number) => {
    setLoading(true);
    setError(null);
    try {
      const response = await InsuranceApi.calculateFees({ monthly_salary: monthlySalary });
      const viewModel = InsuranceViewModelFactory.createFeesViewModel(response.fees);
      setResult(viewModel);
      setLevelNumber(response.level_number);
      return viewModel;
    } catch (err: any) {
      setError(err.message || '試算失敗');
      return null;
    } finally {
      setLoading(false);
    }
  };

  const clearResult = () => {
    setResult(null);
    setLevelNumber(null);
    setError(null);
  };

  return {
    calculateFees,
    clearResult,
    result,
    levelNumber,
    loading,
    error
  };
};
