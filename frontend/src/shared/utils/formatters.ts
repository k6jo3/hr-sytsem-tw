/**
 * 格式化工具函式
 * 提供常用的資料格式化功能
 */

/**
 * 格式化日期
 * @param date 日期字串或 Date 物件
 * @param format 格式（預設：YYYY-MM-DD）
 * @returns 格式化後的日期字串
 *
 * @example
 * formatDate('2025-01-15T10:30:00') // '2025-01-15'
 * formatDate('2025-01-15', 'YYYY/MM/DD') // '2025/01/15'
 */
export function formatDate(
  date: string | Date | null | undefined,
  format: string = 'YYYY-MM-DD'
): string {
  if (!date) return '-';

  const d = typeof date === 'string' ? new Date(date) : date;

  if (isNaN(d.getTime())) return '-';

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
 * 格式化日期時間
 * @param date 日期字串或 Date 物件
 * @returns 格式化後的日期時間字串（YYYY-MM-DD HH:mm）
 *
 * @example
 * formatDateTime('2025-01-15T10:30:00') // '2025-01-15 10:30'
 */
export function formatDateTime(date: string | Date | null | undefined): string {
  return formatDate(date, 'YYYY-MM-DD HH:mm');
}

/**
 * 格式化金額
 * @param amount 金額數值
 * @param currency 貨幣符號（預設：NT$）
 * @returns 格式化後的金額字串
 *
 * @example
 * formatCurrency(1234567) // 'NT$ 1,234,567'
 * formatCurrency(1234.56, '$') // '$ 1,234.56'
 */
export function formatCurrency(
  amount: number | null | undefined,
  currency: string = 'NT$'
): string {
  if (amount === null || amount === undefined) return '-';

  const formatted = amount.toLocaleString('zh-TW', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  });

  return `${currency} ${formatted}`;
}

/**
 * 格式化百分比
 * @param value 數值（0-1 或 0-100）
 * @param isPercent 是否已經是百分比值（預設：false，即 0.5 表示 50%）
 * @param decimals 小數位數（預設：1）
 * @returns 格式化後的百分比字串
 *
 * @example
 * formatPercent(0.856) // '85.6%'
 * formatPercent(85.6, true) // '85.6%'
 */
export function formatPercent(
  value: number | null | undefined,
  isPercent: boolean = false,
  decimals: number = 1
): string {
  if (value === null || value === undefined) return '-';

  const percent = isPercent ? value : value * 100;
  return `${percent.toFixed(decimals)}%`;
}

/**
 * 格式化電話號碼
 * @param phone 電話號碼
 * @returns 格式化後的電話號碼
 *
 * @example
 * formatPhone('0912345678') // '0912-345-678'
 * formatPhone('0223456789') // '02-2345-6789'
 */
export function formatPhone(phone: string | null | undefined): string {
  if (!phone) return '-';

  const cleaned = phone.replace(/\D/g, '');

  // 手機號碼
  if (cleaned.startsWith('09') && cleaned.length === 10) {
    return `${cleaned.slice(0, 4)}-${cleaned.slice(4, 7)}-${cleaned.slice(7)}`;
  }

  // 市話（台北）
  if (cleaned.startsWith('02') && cleaned.length === 10) {
    return `02-${cleaned.slice(2, 6)}-${cleaned.slice(6)}`;
  }

  // 市話（其他區域）
  if (cleaned.length === 10 && cleaned.startsWith('0')) {
    return `${cleaned.slice(0, 3)}-${cleaned.slice(3, 6)}-${cleaned.slice(6)}`;
  }

  return phone;
}

/**
 * 格式化身分證字號（遮罩）
 * @param idNumber 身分證字號
 * @returns 遮罩後的身分證字號
 *
 * @example
 * formatIdNumber('A123456789') // 'A12****789'
 */
export function formatIdNumber(idNumber: string | null | undefined): string {
  if (!idNumber || idNumber.length < 10) return '-';

  return `${idNumber.slice(0, 3)}****${idNumber.slice(-3)}`;
}

/**
 * 格式化銀行帳號（遮罩）
 * @param accountNumber 銀行帳號
 * @returns 遮罩後的銀行帳號
 *
 * @example
 * formatBankAccount('12345678901234') // '****1234'
 */
export function formatBankAccount(accountNumber: string | null | undefined): string {
  if (!accountNumber) return '-';

  return `****${accountNumber.slice(-4)}`;
}

/**
 * 格式化檔案大小
 * @param bytes 位元組數
 * @returns 格式化後的檔案大小
 *
 * @example
 * formatFileSize(1024) // '1 KB'
 * formatFileSize(1536000) // '1.46 MB'
 */
export function formatFileSize(bytes: number | null | undefined): string {
  if (bytes === null || bytes === undefined) return '-';

  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let size = bytes;
  let unitIndex = 0;

  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex++;
  }

  return `${size.toFixed(unitIndex === 0 ? 0 : 2)} ${units[unitIndex]}`;
}

/**
 * 格式化時數
 * @param hours 小時數
 * @returns 格式化後的時數字串
 *
 * @example
 * formatHours(8) // '8 小時'
 * formatHours(40.5) // '40.5 小時'
 */
export function formatHours(hours: number | null | undefined): string {
  if (hours === null || hours === undefined) return '-';

  return `${hours} 小時`;
}

/**
 * 格式化姓名（姓 + 名）
 * @param firstName 名
 * @param lastName 姓
 * @returns 完整姓名
 *
 * @example
 * formatFullName('小明', '王') // '王小明'
 */
export function formatFullName(
  firstName: string | null | undefined,
  lastName: string | null | undefined
): string {
  if (!firstName && !lastName) return '-';

  return `${lastName ?? ''}${firstName ?? ''}`.trim();
}
