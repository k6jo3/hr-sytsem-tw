import { useState, useEffect } from 'react';

/**
 * Local Storage Hook
 * 同步 React state 與 localStorage
 * @param key - localStorage 的 key
 * @param initialValue - 初始值
 */
export function useLocalStorage<T>(key: string, initialValue: T): [T, (value: T) => void] {
  // 從 localStorage 讀取初始值
  const [storedValue, setStoredValue] = useState<T>(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(`Error reading localStorage key "${key}":`, error);
      return initialValue;
    }
  });

  // 更新 localStorage 和 state
  const setValue = (value: T) => {
    try {
      setStoredValue(value);
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
      console.error(`Error setting localStorage key "${key}":`, error);
    }
  };

  return [storedValue, setValue];
}
