import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright E2E 測試配置
 * - 使用 VITE_MOCK=true 啟動前端，不依賴後端服務
 * - 僅使用 Chromium 瀏覽器
 */
export default defineConfig({
  testDir: './e2e',
  /* 每個測試的超時時間 */
  timeout: 30_000,
  /* 預期斷言的超時時間 */
  expect: {
    timeout: 5_000,
  },
  /* 測試報告 */
  reporter: 'list',
  /* 平行執行設定 */
  fullyParallel: true,
  /* CI 環境下禁止 .only */
  forbidOnly: !!process.env.CI,
  /* 失敗重試次數 */
  retries: process.env.CI ? 2 : 0,
  /* 共用設定 */
  use: {
    baseURL: 'http://localhost:5173',
    /* 僅在失敗時截圖 */
    screenshot: 'only-on-failure',
    /* 追蹤設定：僅在第一次重試時保留 */
    trace: 'on-first-retry',
  },
  /* 瀏覽器配置：僅 Chromium */
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
  /* 自動啟動前端 Dev Server（帶 VITE_MOCK=true） */
  webServer: {
    command: 'npm run dev',
    port: 5173,
    reuseExistingServer: !process.env.CI,
    env: {
      VITE_MOCK: 'true',
    },
    timeout: 30_000,
  },
});
