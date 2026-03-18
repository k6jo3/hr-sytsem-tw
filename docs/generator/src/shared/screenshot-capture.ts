import puppeteer, { Browser, Page } from 'puppeteer';
import fs from 'fs/promises';
import path from 'path';
import { getMockResponse } from './screenshot-mock-data.js';

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

/** 模擬使用者資料（匹配 MockAuthApi + UserProfile 格式） */
interface MockUserProfile {
  id: string;
  username: string;
  email: string;
  fullName: string;
  employeeId?: string;
  roles: string[];
  displayRoles: string;
  isAdmin: boolean;
  statusLabel: string;
  statusColor: string;
  displayStatus: string;
}

const MOCK_USERS: Record<string, MockUserProfile> = {
  admin: {
    id: 'u001', username: 'admin', email: 'admin@company.com',
    fullName: 'System Admin', employeeId: '00000000-0000-0000-0000-000000000001',
    roles: ['ADMIN'], displayRoles: '系統管理員', isAdmin: true,
    statusLabel: '啟用', statusColor: 'success', displayStatus: '啟用',
  },
  hr_admin: {
    id: 'u003', username: 'hr_admin', email: 'hr_admin@company.com',
    fullName: '李小美', employeeId: '00000000-0000-0000-0000-000000000002',
    roles: ['HR'], displayRoles: 'HR管理員', isAdmin: false,
    statusLabel: '啟用', statusColor: 'success', displayStatus: '啟用',
  },
  employee: {
    id: 'u004', username: 'employee', email: 'employee@company.com',
    fullName: '陳志強', employeeId: '00000000-0000-0000-0000-000000000003',
    roles: ['EMPLOYEE'], displayRoles: '一般員工', isAdmin: false,
    statusLabel: '啟用', statusColor: 'success', displayStatus: '啟用',
  },
  manager: {
    id: 'u005', username: 'manager', email: 'manager@company.com',
    fullName: '陳志強', employeeId: '00000000-0000-0000-0000-000000000003',
    roles: ['MANAGER'], displayRoles: '部門主管', isAdmin: false,
    statusLabel: '啟用', statusColor: 'success', displayStatus: '啟用',
  },
  pm: {
    id: 'u006', username: 'pm', email: 'pm@company.com',
    fullName: '林雅婷', employeeId: '00000000-0000-0000-0000-000000000004',
    roles: ['PM'], displayRoles: '專案經理', isAdmin: false,
    statusLabel: '啟用', statusColor: 'success', displayStatus: '啟用',
  },
};

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

    // 攔截後端 API 請求，回傳空成功回應
    // 避免因後端未啟動而觸發 401 → 清除 token → 重導登入頁
    // 只攔截 XHR/Fetch 類型的 /api/ 請求，不攔截 JS/CSS 等靜態資源
    await this.page.setRequestInterception(true);
    this.page.on('request', (request) => {
      const url = request.url();
      const resourceType = request.resourceType();
      // 只攔截 XHR/Fetch 類型且路徑包含 /api/ 的請求
      // 使用 getMockResponse 回傳對應模組的假資料，讓截圖有實際內容
      if ((resourceType === 'xhr' || resourceType === 'fetch') && url.includes('/api/')) {
        const mockData = getMockResponse(url);
        request.respond({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(mockData),
        });
      } else {
        request.continue();
      }
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

  /**
   * 登入前端系統（直接注入 localStorage 認證狀態）
   * 繞過 Ant Design 表單互動問題，直接設定 Redux authSlice 所需的 localStorage 資料
   *
   * 重要：必須先注入 localStorage 再 reload 頁面，因為 Redux authSlice 的
   * initialState 在模組載入時從 localStorage 讀取。若先載入頁面再注入，
   * Redux store 已初始化為未認證狀態，後續導航不會重新初始化 store。
   */
  async login(username: string, _password: string = 'Admin@123'): Promise<void> {
    if (!this.page) throw new Error('瀏覽器未啟動');
    if (this.currentUser === username) return;

    const mockUser = MOCK_USERS[username];
    if (!mockUser) {
      console.warn(`[Screenshot] 未知帳號: ${username}，跳過登入`);
      return;
    }

    // 先導航到前端頁面（確保在正確 origin 才能存取 localStorage）
    const currentUrl = this.page.url();
    if (!currentUrl.startsWith(this.config.baseUrl)) {
      await this.page.goto(`${this.config.baseUrl}/login`, {
        waitUntil: 'networkidle2',
        timeout: 30000,
      });
    }

    // 注入 localStorage 認證狀態
    await this.page.evaluate((profile: MockUserProfile) => {
      localStorage.clear();
      sessionStorage.clear();
      localStorage.setItem('accessToken', 'mock-screenshot-token-' + profile.username);
      localStorage.setItem('user', JSON.stringify(profile));
    }, mockUser);

    // 重新載入頁面，讓 Redux authSlice initialState 從 localStorage 讀取新的認證狀態
    await this.page.reload({ waitUntil: 'networkidle2', timeout: 30000 });

    // 等待 React 渲染完成
    await new Promise(r => setTimeout(r, 1500));

    this.currentUser = username;
    console.log(`[Screenshot] 已注入登入狀態: ${username} (${mockUser.fullName})`);
  }

  /** 截圖單一路由 */
  async captureRoute(route: RouteCapture): Promise<string> {
    if (!this.page) throw new Error('瀏覽器未啟動');

    // 登入（如需切換帳號）
    if (route.loginAs && route.loginAs !== this.currentUser) {
      await this.login(route.loginAs);
    }

    // 登入頁特殊處理：清除認證讓 React 顯示登入表單
    if (route.path === '/login') {
      await this.page.evaluate(() => {
        localStorage.clear();
        sessionStorage.clear();
      });
      this.currentUser = null;
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
