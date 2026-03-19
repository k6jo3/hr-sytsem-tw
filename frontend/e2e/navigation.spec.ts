import { expect, test } from '@playwright/test';
import { login, TEST_ACCOUNTS } from './fixtures/auth';

test.describe('導航測試', () => {
  test.beforeEach(async ({ page }) => {
    // 每個測試前先以 admin 帳號登入
    await login(page, TEST_ACCOUNTS.admin.username, TEST_ACCOUNTS.admin.password);
  });

  test('登入後能看到側邊欄', async ({ page }) => {
    // 側邊欄應該包含主要選單項目
    const sider = page.locator('.ant-layout-sider');
    await expect(sider).toBeVisible();

    // 驗證關鍵選單項目存在
    await expect(page.locator('text=首頁儀表板')).toBeVisible();
  });

  test('點擊員工管理能切換頁面', async ({ page }) => {
    // 展開「組織與員工」子選單
    await page.click('text=組織與員工');
    // 點擊「員工基本資料」
    await page.click('text=員工基本資料');

    // 驗證頁面已切換
    await expect(page).toHaveURL(/\/admin\/employees/);
  });

  test('點擊考勤相關選單能切換頁面', async ({ page }) => {
    // 展開「考勤管理」子選單
    await page.click('text=考勤管理');
    // 點擊「每日打卡」
    await page.click('text=每日打卡');

    await expect(page).toHaveURL(/\/attendance\/check-in/);
  });

  test('點擊專案管理能切換頁面', async ({ page }) => {
    // 展開「專案管理」子選單
    await page.click('text=專案管理');
    // 點擊「專案總覽」
    await page.click('text=專案總覽');

    await expect(page).toHaveURL(/\/admin\/projects/);
  });

  test('點擊首頁儀表板能回到 Dashboard', async ({ page }) => {
    // 先切換到其他頁面
    await page.click('text=組織與員工');
    await page.click('text=員工基本資料');
    await expect(page).toHaveURL(/\/admin\/employees/);

    // 點擊首頁
    await page.click('text=首頁儀表板');
    await expect(page).toHaveURL(/\/dashboard/);
  });
});
