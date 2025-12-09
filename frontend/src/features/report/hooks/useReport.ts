import { useState } from 'react';
import { ReportApi } from '../api';

/**
 * Report Hook (報表分析 Hook)
 * 處理報表分析相關的業務邏輯
 */
export const useReport = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // TODO: Add hook methods

  return {
    loading,
    error,
  };
};
