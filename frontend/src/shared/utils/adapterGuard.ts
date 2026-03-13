/**
 * Adapter 防護工具
 * 用於在 Adapter 函式中檢查後端回傳值，避免靜默 fallback 隱藏問題。
 */

/**
 * 檢查欄位值是否在允許的列舉中，若不在則發出警告並回傳原始值
 * @param fieldName - 欄位名稱（用於警告訊息）
 * @param value - 後端回傳的實際值
 * @param allowedValues - 前端支援的列舉值
 * @param fallback - 當值為 null/undefined 時的預設值
 */
export function guardEnum<T extends string>(
  fieldName: string,
  value: string | null | undefined,
  allowedValues: readonly T[],
  fallback: T
): T {
  if (value === null || value === undefined) {
    return fallback;
  }
  if (!allowedValues.includes(value as T)) {
    console.warn(
      `[Adapter] 欄位 "${fieldName}" 收到未知值: "${value}"，允許值: [${allowedValues.join(', ')}]。使用原始值。`
    );
    return value as T;
  }
  return value as T;
}

/**
 * 檢查必要欄位是否存在，若缺失則發出警告
 * @param context - 上下文名稱（如 "adaptUserItem"）
 * @param fieldName - 欄位名稱
 * @param value - 欄位值
 * @param fallback - 預設值
 */
export function guardRequired<T>(
  context: string,
  fieldName: string,
  value: T | null | undefined,
  fallback: T
): T {
  if (value === null || value === undefined) {
    console.warn(
      `[Adapter] ${context}: 必要欄位 "${fieldName}" 缺失，使用預設值: ${JSON.stringify(fallback)}`
    );
    return fallback;
  }
  return value;
}
