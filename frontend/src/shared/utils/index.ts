// 匯出格式化工具
export * from './formatters';
export * from './adapterGuard';

/**
 * 數字格式化 (加入千分位)
 * @param value - 數字
 */
export function formatNumber(value: number): string {
  return new Intl.NumberFormat('zh-TW').format(value);
}

/**
 * 防抖函式
 * @param fn - 要執行的函式
 * @param delay - 延遲時間 (ms)
 */
export function debounce<T extends (...args: unknown[]) => void>(
  fn: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timeoutId: ReturnType<typeof setTimeout>;
  return (...args: Parameters<T>) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => fn(...args), delay);
  };
}

/**
 * 判斷值是否為空
 */
export function isEmpty(value: unknown): boolean {
  if (value === null || value === undefined) return true;
  if (typeof value === 'string') return value.trim() === '';
  if (Array.isArray(value)) return value.length === 0;
  if (typeof value === 'object') return Object.keys(value).length === 0;
  return false;
}

/**
 * 深拷貝物件
 */
export function deepClone<T>(obj: T): T {
  return JSON.parse(JSON.stringify(obj));
}
