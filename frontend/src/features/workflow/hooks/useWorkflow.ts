import { useState } from 'react';
import { WorkflowApi } from '../api';

/**
 * Workflow Hook (簽核流程 Hook)
 * 處理簽核流程相關的業務邏輯
 */
export const useWorkflow = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  // TODO: Add hook methods

  return {
    loading,
    error,
  };
};
