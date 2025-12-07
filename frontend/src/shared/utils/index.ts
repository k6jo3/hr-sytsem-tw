/**
 * 日期格式化
 * @param date - 日期物件或字串
 * @param format - 格式 (預設: YYYY-MM-DD)
 */
export function formatDate(date: Date | string, format = 'YYYY-MM-DD'): string {
  const d = typeof date === 'string' ? new Date(date) : date;
  
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');
  
  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 數字格式化 (加入千分位)
 * @param value - 數字
 */
export function formatNumber(value: number): string {
  return new Intl.NumberFormat('zh-TW').format(value);
}

/**
 * 金額格式化
 * @param value - 金額數字
 * @param currency - 貨幣符號 (預設: NT$)
 */
export function formatCurrency(value: number, currency = 'NT$'): string {
  return `${currency} ${formatNumber(value)}`;
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
