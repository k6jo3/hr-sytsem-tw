/**
 * 共用類型定義
 * 提供前端與後端 API 對接的標準類型
 */

// ========================================
// API 回應類型
// ========================================

/**
 * 通用 API 回應包裝
 */
export interface StandardApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T | null;
  timestamp: string;
  traceId?: string;
}

/**
 * 分頁回應
 */
export interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

/**
 * 分頁請求參數
 */
export interface PageRequest {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: SortDirection;
}

/**
 * 排序方向
 */
export type SortDirection = 'ASC' | 'DESC';

// ========================================
// 表單與驗證類型
// ========================================

/**
 * 表單欄位驗證錯誤
 */
export interface FieldError {
  field: string;
  message: string;
}

/**
 * 表單驗證結果
 */
export interface ValidationResult {
  isValid: boolean;
  errors: FieldError[];
}

// ========================================
// 通用狀態類型
// ========================================

/**
 * 非同步操作狀態
 */
export type AsyncStatus = 'idle' | 'loading' | 'success' | 'error';

/**
 * 非同步操作狀態物件
 */
export interface AsyncState<T> {
  status: AsyncStatus;
  data: T | null;
  error: string | null;
}

/**
 * 選項項目（用於下拉選單等）
 */
export interface SelectOption<T = string> {
  label: string;
  value: T;
  disabled?: boolean;
}

// ========================================
// 常用業務狀態
// ========================================

/**
 * 啟用狀態
 */
export type ActiveStatus = 'ACTIVE' | 'INACTIVE';

/**
 * 審核狀態
 */
export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

/**
 * 員工狀態
 */
export type EmployeeStatus = 'ACTIVE' | 'RESIGNED' | 'ON_LEAVE' | 'TERMINATED';

// ========================================
// 日期時間類型
// ========================================

/**
 * 日期範圍
 */
export interface DateRange {
  startDate: string;
  endDate: string;
}

/**
 * 年月
 */
export interface YearMonth {
  year: number;
  month: number;
}

// ========================================
// 表格相關類型
// ========================================

/**
 * 表格列定義
 */
export interface TableColumn<T> {
  key: keyof T | string;
  title: string;
  dataIndex?: keyof T | string[];
  width?: number | string;
  align?: 'left' | 'center' | 'right';
  sortable?: boolean;
  render?: (value: unknown, record: T, index: number) => React.ReactNode;
}

/**
 * 表格排序資訊
 */
export interface TableSorter {
  field: string;
  order: SortDirection | null;
}

// ========================================
// 工具類型
// ========================================

/**
 * 將物件的所有屬性設為可選
 */
export type DeepPartial<T> = {
  [P in keyof T]?: T[P] extends object ? DeepPartial<T[P]> : T[P];
};

/**
 * 排除 null 和 undefined
 */
export type NonNullable<T> = T extends null | undefined ? never : T;

/**
 * ID 類型
 */
export type ID = string;
