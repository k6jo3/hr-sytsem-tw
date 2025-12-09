import { useState } from 'react';
import { PerformanceApi } from '../api';

/**
 * Performance Hook (績效管理 Hook)
 * 處理績效管理相關的業務邏輯
 */
export const usePerformance = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // TODO: Add hook methods

  return {
    loading,
    error,
  };
};
