import { useCallback, useState } from 'react';

/**
 * useApi Hook 回傳介面
 */
interface UseApiResult<T> {
  data: T | null;
  isLoading: boolean;
  error: string | null;
  execute: (...args: unknown[]) => Promise<T | null>;
  reset: () => void;
}

/**
 * 通用 API 呼叫 Hook
 * 封裝載入狀態、錯誤處理
 *
 * @param apiFunc - API 函式
 *
 * @example
 * const { data, isLoading, execute } = useApi(AuthApi.getCurrentUser);
 * useEffect(() => { execute(); }, []);
 */
export function useApi<T>(
  apiFunc: (...args: unknown[]) => Promise<T>
): UseApiResult<T> {
  const [data, setData] = useState<T | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const execute = useCallback(
    async (...args: unknown[]): Promise<T | null> => {
      try {
        setIsLoading(true);
        setError(null);
        const result = await apiFunc(...args);
        setData(result);
        return result;
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '發生未知錯誤';
        setError(errorMessage);
        return null;
      } finally {
        setIsLoading(false);
      }
    },
    [apiFunc]
  );

  const reset = useCallback(() => {
    setData(null);
    setError(null);
    setIsLoading(false);
  }, []);

  return { data, isLoading, error, execute, reset };
}

export default useApi;
