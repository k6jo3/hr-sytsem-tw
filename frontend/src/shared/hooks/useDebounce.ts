import { useEffect, useState } from 'react';

/**
 * Debounce Hook
 * 防抖 Hook，延遲更新值
 * @param value - 要防抖的值
 * @param delay - 延遲時間（毫秒）
 */
export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}
