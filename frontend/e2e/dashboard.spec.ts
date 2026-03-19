import { expect, test } from '@playwright/test';
import { login, TEST_ACCOUNTS } from './fixtures/auth';

test.describe('Dashboard 儀表板', () => {
  test.beforeEach(async ({ page }) => {
    await login(page, TEST_ACCOUNTS.admin.username, TEST_ACCOUNTS.admin.password);
  });

  test('登入後 Dashboard 顯示統計卡片', async ({ page }) => {
    await expect(page).toHaveURL(/\/dashboard/);

    // 驗證統計卡片存在（Ant Design Statistic 元件）
    // Dashboard 有四個統計項目：待辦事項、本月出勤、未讀通知、特休餘額
    // 使用 .ant-statistic-title 精確定位統計卡片中的標題，避免與 Card 標題重複
    await expect(page.locator('.ant-statistic-title:has-text("待辦事項")')).toBeVisible();
    await expect(page.locator('.ant-statistic-title:has-text("本月出勤")')).toBeVisible();
    await expect(page.locator('.ant-statistic-title:has-text("未讀通知")')).toBeVisible();
    await expect(page.locator('.ant-statistic-title:has-text("特休餘額")')).toBeVisible();
  });

  test('顯示公告區塊', async ({ page }) => {
    // 驗證系統公告區塊存在
    await expect(page.locator('text=系統公告')).toBeVisible();

    // Mock 模式下有 3 筆公告
    await expect(page.locator('text=2026 年第一季考核開始')).toBeVisible();
    await expect(page.locator('text=系統維護通知')).toBeVisible();
  });

  test('顯示待辦事項', async ({ page }) => {
    // 驗證待辦事項區塊存在
    await expect(page.locator('text=待辦事項').first()).toBeVisible();

    // Mock 模式下有 2 筆待辦：簽核待處理和未讀通知
    await expect(page.locator('text=2 筆簽核待處理')).toBeVisible();
    await expect(page.locator('text=3 則未讀通知')).toBeVisible();
  });

  test('顯示快捷操作區塊', async ({ page }) => {
    // 驗證快捷操作區塊
    await expect(page.locator('text=快捷操作')).toBeVisible();

    // admin 角色應該能看到所有快捷操作
    await expect(page.locator('text=每日打卡')).toBeVisible();
    await expect(page.locator('text=請假申請')).toBeVisible();
  });
});
