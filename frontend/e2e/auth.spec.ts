import { expect, test } from '@playwright/test';
import { login, logout, TEST_ACCOUNTS } from './fixtures/auth';

test.describe('登入流程', () => {
  test('能看到登入頁面', async ({ page }) => {
    await page.goto('/login');

    // 驗證登入頁面的關鍵元素
    await expect(page.locator('text=歡迎回來')).toBeVisible();
    await expect(page.locator('input[aria-label="帳號"]')).toBeVisible();
    await expect(page.locator('input[aria-label="密碼"]')).toBeVisible();
    await expect(page.locator('button[aria-label="登入"]')).toBeVisible();
  });

  test('輸入帳密後能登入成功跳到 Dashboard', async ({ page }) => {
    await login(page, TEST_ACCOUNTS.admin.username, TEST_ACCOUNTS.admin.password);

    // 驗證已跳轉到 Dashboard
    await expect(page).toHaveURL(/\/dashboard/);
    // Dashboard 頁面應該顯示歡迎訊息（含使用者姓名）
    await expect(page.locator('text=歡迎使用人力資源暨專案管理系統')).toBeVisible();
  });

  test('錯誤密碼顯示錯誤訊息', async ({ page }) => {
    await page.goto('/login');
    await page.waitForSelector('input[aria-label="帳號"]');

    // 填入不存在的帳號
    await page.fill('input[aria-label="帳號"]', TEST_ACCOUNTS.invalid.username);
    await page.fill('input[aria-label="密碼"]', TEST_ACCOUNTS.invalid.password);
    await page.click('button[aria-label="登入"]');

    // 應該顯示錯誤訊息（Ant Design message 或 Alert，取第一個匹配的即可）
    await expect(page.locator('.ant-message-error, .ant-alert-error').first()).toBeVisible({ timeout: 5_000 });
  });

  test('登出後回到登入頁', async ({ page }) => {
    // 先登入
    await login(page, TEST_ACCOUNTS.admin.username, TEST_ACCOUNTS.admin.password);
    await expect(page).toHaveURL(/\/dashboard/);

    // 執行登出
    await logout(page);

    // 驗證回到登入頁
    await expect(page).toHaveURL(/\/login/);
    await expect(page.locator('text=歡迎回來')).toBeVisible();
  });
});
