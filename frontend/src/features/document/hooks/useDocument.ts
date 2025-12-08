import { useState } from 'react';
import { DocumentApi } from '../api';

/**
 * Document Hook (文件管理 Hook)
 * 處理文件管理相關的業務邏輯
 */
export const useDocument = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // TODO: Add hook methods

  return {
    loading,
    error,
  };
};
