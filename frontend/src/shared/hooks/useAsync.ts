import { useState, useCallback } from 'react';

interface AsyncState<T> {
  loading: boolean;
  error: Error | null;
  data: T | null;
}

/**
 * Async Hook
 * 處理非同步操作的狀態管理
 */
export function useAsync<T>() {
  const [state, setState] = useState<AsyncState<T>>({
    loading: false,
    error: null,
    data: null,
  });

  const execute = useCallback(async (asyncFunction: () => Promise<T>) => {
    setState({ loading: true, error: null, data: null });
    try {
      const data = await asyncFunction();
      setState({ loading: false, error: null, data });
      return data;
    } catch (error) {
      setState({ loading: false, error: error as Error, data: null });
      throw error;
    }
  }, []);

  return { ...state, execute };
}
