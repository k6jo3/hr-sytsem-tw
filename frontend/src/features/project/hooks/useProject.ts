import { useState } from 'react';
import { ProjectApi } from '../api';

/**
 * Project Hook (專案管理 Hook)
 * 處理專案管理相關的業務邏輯
 */
export const useProject = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // TODO: Add hook methods

  return {
    loading,
    error,
  };
};
