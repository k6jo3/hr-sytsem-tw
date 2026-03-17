/**
 * API 錯誤資訊萃取工具
 * 從 catch 區塊的 unknown 錯誤中提取結構化資訊
 */
export interface ApiErrorInfo {
  message: string;
  status?: number;
  errorCode?: string;
}

/**
 * 從未知錯誤物件中萃取 API 錯誤資訊
 *
 * @param err - catch 區塊捕獲的錯誤（unknown 型別）
 * @param defaultMessage - 當無法取得錯誤訊息時的預設訊息
 * @returns 結構化的錯誤資訊
 *
 * @example
 * catch (err) {
 *   const { message, status, errorCode } = extractApiError(err, '操作失敗');
 *   setError(message);
 *   if (status === 403) { ... }
 * }
 */
export function extractApiError(err: unknown, defaultMessage: string): ApiErrorInfo {
  if (err && typeof err === 'object') {
    const error = err as Record<string, unknown>;
    return {
      message: (typeof error.message === 'string' && error.message) || defaultMessage,
      status: typeof error.status === 'number' ? error.status : undefined,
      errorCode: typeof error.errorCode === 'string' ? error.errorCode : undefined,
    };
  }
  return { message: defaultMessage };
}
