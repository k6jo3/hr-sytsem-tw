/**
 * Shared Constants
 * 全域常數定義
 */

/**
 * API Response Status
 */
export const API_STATUS = {
  SUCCESS: 'success',
  ERROR: 'error',
  WARNING: 'warning',
} as const;

/**
 * Employee Status
 */
export const EMPLOYEE_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  ON_LEAVE: 'ON_LEAVE',
} as const;

/**
 * Date Formats
 */
export const DATE_FORMATS = {
  DATE: 'YYYY-MM-DD',
  DATETIME: 'YYYY-MM-DD HH:mm:ss',
  TIME: 'HH:mm',
  MONTH: 'YYYY-MM',
} as const;

/**
 * Page Size Options
 */
export const PAGE_SIZE_OPTIONS = [10, 20, 50, 100] as const;

/**
 * Default Page Size
 */
export const DEFAULT_PAGE_SIZE = 20;
