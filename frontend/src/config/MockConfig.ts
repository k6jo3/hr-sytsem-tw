/**
 * Mock 配置
 * 控制各模組使用 Mock API 或真實後端 API。
 *
 * 控制方式（優先順序由高到低）：
 * 1. 環境變數 VITE_MOCK=true → 全部模組啟用 Mock
 * 2. 環境變數 VITE_MOCK=AUTH,PAYROLL → 僅指定模組啟用 Mock
 * 3. 環境變數未設定 → 全部使用真實 API
 *
 * 使用方式：
 *   開發時全部 mock:  VITE_MOCK=true npm run dev
 *   僅 mock 部分模組: VITE_MOCK=AUTH,ORGANIZATION npm run dev
 *   使用真實 API:     npm run dev （不設定環境變數）
 */

/** 從環境變數解析 mock 設定 */
function parseMockEnv(): { allEnabled: boolean; enabledModules: Set<string> } {
  const envValue = import.meta.env.VITE_MOCK as string | undefined;

  if (!envValue) {
    return { allEnabled: false, enabledModules: new Set() };
  }

  // VITE_MOCK=true → 全部啟用
  if (envValue === 'true' || envValue === '1') {
    return { allEnabled: true, enabledModules: new Set() };
  }

  // VITE_MOCK=AUTH,PAYROLL → 僅指定模組
  const modules = envValue.split(',').map(m => m.trim().toUpperCase()).filter(Boolean);
  return { allEnabled: false, enabledModules: new Set(modules) };
}

const { allEnabled, enabledModules } = parseMockEnv();

export const MockConfig = {
  /**
   * 檢查指定模組是否使用 Mock API
   */
  isEnabled(moduleName: string): boolean {
    if (allEnabled) return true;
    return enabledModules.has(moduleName.toUpperCase());
  },
};
