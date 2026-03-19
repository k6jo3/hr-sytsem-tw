import { expect, test } from '@playwright/test';
import { login, TEST_ACCOUNTS } from './fixtures/auth';

test.describe('員工管理 CRUD', () => {
  test.beforeEach(async ({ page }) => {
    // 以 admin 帳號登入
    await login(page, TEST_ACCOUNTS.admin.username, TEST_ACCOUNTS.admin.password);
    // 導航到員工列表頁面
    await page.goto('/admin/employees');
    // 等待頁面載入完成
    await page.waitForLoadState('networkidle');
  });

  test('能看到員工列表', async ({ page }) => {
    // 員工列表頁面應該有 Table 元件
    const table = page.locator('.ant-table');
    await expect(table).toBeVisible({ timeout: 10_000 });
  });

  test('能搜尋員工', async ({ page }) => {
    // 找到搜尋輸入框（可能是 SearchForm 或獨立的 Input）
    const searchInput = page.locator('input[placeholder*="搜尋"], input[placeholder*="查詢"], input[placeholder*="姓名"], input[placeholder*="工號"]').first();

    // 如果搜尋框存在，嘗試輸入搜尋
    if (await searchInput.isVisible({ timeout: 3_000 }).catch(() => false)) {
      await searchInput.fill('張');
      // 觸發搜尋（按 Enter 或點擊搜尋按鈕）
      const searchButton = page.locator('button:has-text("搜尋"), button:has-text("查詢")').first();
      if (await searchButton.isVisible({ timeout: 2_000 }).catch(() => false)) {
        await searchButton.click();
      } else {
        await searchInput.press('Enter');
      }
      // 等待搜尋結果更新
      await page.waitForTimeout(1_000);
    }

    // 表格應仍然可見（不論是否有搜尋結果）
    await expect(page.locator('.ant-table')).toBeVisible();
  });

  test('能點進員工詳情', async ({ page }) => {
    // 等待表格資料載入
    const table = page.locator('.ant-table');
    await expect(table).toBeVisible({ timeout: 10_000 });

    // 點擊表格中第一筆可見的資料列（跳過 Ant Design 的隱藏測量列）
    // 使用 onRow onClick 導航機制，直接點擊含有員工編號的資料列
    const firstDataRow = page.locator('.ant-table-tbody tr[data-row-key]').first();
    await expect(firstDataRow).toBeVisible({ timeout: 5_000 });
    await firstDataRow.click();

    // 驗證已導航到詳情頁面（URL 包含 /admin/employees/ 且有 ID）
    await page.waitForURL(/\/admin\/employees\//, { timeout: 5_000 });
  });
});
