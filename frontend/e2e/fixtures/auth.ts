import { type Page } from '@playwright/test';

/**
 * E2E 測試共用登入工具
 * Mock 模式下，使用 MockAuthApi 中定義的帳號進行登入
 */

/** Mock 測試帳號（對應 MockAuthApi.ts 中的 mock 資料） */
export const TEST_ACCOUNTS = {
  /** 管理員帳號（ADMIN 角色） */
  admin: { username: 'admin', password: 'password123' },
  /** 一般使用者帳號（USER 角色） */
  demo: { username: 'demo', password: 'password123' },
  /** HR 管理員帳號（HR 角色） */
  hr: { username: 'hr_admin', password: 'password123' },
  /** 主管帳號（MANAGER 角色） */
  manager: { username: 'manager', password: 'password123' },
  /** 不存在的帳號（用於測試登入失敗） */
  invalid: { username: 'nonexistent', password: 'wrongpassword' },
};

/**
 * 執行登入操作
 * @param page Playwright Page 物件
 * @param username 帳號
 * @param password 密碼
 */
export async function login(
  page: Page,
  username: string = TEST_ACCOUNTS.admin.username,
  password: string = TEST_ACCOUNTS.admin.password
): Promise<void> {
  // 前往登入頁面
  await page.goto('/login');
  // 等待登入表單出現
  await page.waitForSelector('input[aria-label="帳號"]', { timeout: 10_000 });
  // 填入帳號密碼
  await page.fill('input[aria-label="帳號"]', username);
  await page.fill('input[aria-label="密碼"]', password);
  // 點擊登入按鈕
  await page.click('button[aria-label="登入"]');
  // 等待跳轉至 Dashboard
  await page.waitForURL('**/dashboard', { timeout: 10_000 });
}

/**
 * 執行登出操作
 * @param page Playwright Page 物件
 */
export async function logout(page: Page): Promise<void> {
  // 點擊右上角使用者頭像/下拉
  const avatarButton = page.locator('.ant-dropdown-trigger').first();
  await avatarButton.click();
  // 點擊「登出」選項
  await page.click('text=登出');
  // 等待回到登入頁面
  await page.waitForURL('**/login', { timeout: 10_000 });
}
