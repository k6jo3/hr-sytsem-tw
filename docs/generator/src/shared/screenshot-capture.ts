import puppeteer, { Browser, Page } from 'puppeteer';
import fs from 'fs/promises';
import path from 'path';

/** 截圖設定 */
export interface ScreenshotConfig {
  /** 前端 URL */
  baseUrl: string;
  /** 截圖輸出目錄 */
  outputDir: string;
  /** 視窗寬度 */
  width?: number;
  /** 視窗高度 */
  height?: number;
}

/** 路由截圖定義 */
export interface RouteCapture {
  /** 路由路徑 */
  path: string;
  /** 截圖檔名（不含副檔名） */
  filename: string;
  /** 說明 */
  description: string;
  /** 登入帳號（預設 admin） */
  loginAs?: string;
  /** 截圖前等待時間（毫秒） */
  waitMs?: number;
  /** 是否全頁截圖 */
  fullPage?: boolean;
}

/**
 * 前端截圖工具
 * 使用 puppeteer 自動登入並截圖前端頁面
 */
export class ScreenshotCapture {
  private browser: Browser | null = null;
  private page: Page | null = null;
  private config: ScreenshotConfig;
  private currentUser: string | null = null;

  constructor(config: ScreenshotConfig) {
    this.config = {
      width: 1440,
      height: 900,
      ...config,
    };
  }

  /** 啟動瀏覽器 */
  async launch(): Promise<void> {
    this.browser = await puppeteer.launch({
      headless: true,
      args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-gpu'],
    });
    this.page = await this.browser.newPage();
    await this.page.setViewport({
      width: this.config.width!,
      height: this.config.height!,
    });
  }

  /** 關閉瀏覽器 */
  async close(): Promise<void> {
    if (this.browser) {
      await this.browser.close();
      this.browser = null;
      this.page = null;
    }
  }

  /** 登入前端系統 */
  async login(username: string, password: string = 'Admin@123'): Promise<void> {
    if (!this.page) throw new Error('瀏覽器未啟動');
    if (this.currentUser === username) return;

    // 前往登入頁
    await this.page.goto(`${this.config.baseUrl}/login`, {
      waitUntil: 'networkidle2',
      timeout: 30000,
    });

    // 等待登入表單
    await this.page.waitForSelector('input[type="text"], input[id="username"]', { timeout: 10000 });

    // 清除並填入帳號密碼
    const usernameInput = await this.page.$('input[type="text"], input[id="username"]');
    const passwordInput = await this.page.$('input[type="password"], input[id="password"]');

    if (usernameInput && passwordInput) {
      await usernameInput.click({ clickCount: 3 });
      await usernameInput.type(username);
      await passwordInput.click({ clickCount: 3 });
      await passwordInput.type(password);

      // 點擊登入按鈕
      const submitBtn = await this.page.$('button[type="submit"]');
      if (submitBtn) {
        await submitBtn.click();
      }

      // 等待頁面跳轉
      await this.page.waitForNavigation({ waitUntil: 'networkidle2', timeout: 15000 }).catch(() => {});
      await new Promise(r => setTimeout(r, 2000));
    }

    this.currentUser = username;
  }

  /** 截圖單一路由 */
  async captureRoute(route: RouteCapture): Promise<string> {
    if (!this.page) throw new Error('瀏覽器未啟動');

    // 登入（如需切換帳號）
    if (route.loginAs && route.loginAs !== this.currentUser) {
      await this.login(route.loginAs);
    }

    // 導航到目標頁面
    const url = `${this.config.baseUrl}${route.path}`;
    await this.page.goto(url, {
      waitUntil: 'networkidle2',
      timeout: 30000,
    }).catch(() => {
      console.warn(`[Screenshot] 無法載入: ${url}`);
    });

    // 等待頁面穩定
    await new Promise(r => setTimeout(r, route.waitMs || 2000));

    // 確保輸出目錄存在
    await fs.mkdir(this.config.outputDir, { recursive: true });

    // 截圖
    const outputPath = path.join(this.config.outputDir, `${route.filename}.png`);
    await this.page.screenshot({
      path: outputPath,
      fullPage: route.fullPage || false,
    });

    console.log(`[Screenshot] ${route.filename}: ${route.description}`);
    return outputPath;
  }

  /** 批次截圖多個路由 */
  async captureRoutes(routes: RouteCapture[]): Promise<Map<string, string>> {
    const results = new Map<string, string>();

    for (const route of routes) {
      try {
        const outputPath = await this.captureRoute(route);
        results.set(route.filename, outputPath);
      } catch (err) {
        console.error(`[Screenshot] 截圖失敗 ${route.filename}: ${err}`);
      }
    }

    return results;
  }
}
