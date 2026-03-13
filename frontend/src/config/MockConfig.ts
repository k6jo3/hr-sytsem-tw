/**
 * Mock 配置
 * 控制各模組使用 Mock API 或真實後端 API。
 *
 * 漸進式切換：將模組設為 false 即切換為真實 API。
 * 當後端服務尚未建置時保持 true 使用 Mock 資料。
 */
export const MockConfig = {
  // 各模組 Mock 開關（false = 使用真實後端 API）
  modules: {
    AUTH: false,
    ORGANIZATION: false,
    ATTENDANCE: false,
    PAYROLL: false,
    INSURANCE: false,
    PROJECT: false,
    TIMESHEET: false,
    PERFORMANCE: false,
    RECRUITMENT: false,
    TRAINING: false,
    WORKFLOW: false,
    NOTIFICATION: false,
    DOCUMENT: false,
    REPORT: false,
  } as Record<string, boolean>,

  /**
   * 檢查指定模組是否使用 Mock API
   * 預設為 false（使用真實 API），避免未定義的模組名稱靜默啟用 Mock
   */
  isEnabled(moduleName: string): boolean {
    const value = this.modules[moduleName];
    if (value === undefined) {
      console.warn(`[MockConfig] 未知模組名稱: "${moduleName}"，預設使用真實 API`);
    }
    return value ?? false;
  }
};
