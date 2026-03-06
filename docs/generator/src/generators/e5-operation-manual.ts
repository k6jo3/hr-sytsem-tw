import fs from 'fs/promises';
import path from 'path';
import { PATHS, SERVICES, OUTPUT_PATHS, FRONTEND_DIR, DOC_META, ServiceMeta } from '../config.js';
import { markdownParser } from '../shared/markdown-parser.js';
import { renderFullDocument } from '../shared/html-template.js';
import { pdfGenerator } from '../shared/pdf-generator.js';
import { ScreenshotCapture, RouteCapture } from '../shared/screenshot-capture.js';

/**
 * E5: 系統操作手冊 → 1 PDF
 * 截圖流程：啟動前端 → 多帳號登入 → 逐路由截圖
 * 內容來源：knowledge/06_UI_Design/HR{XX}_*_畫面規格書.md
 */

const FRONTEND_URL = 'http://localhost:5173';
const SCREENSHOT_DIR = path.join(OUTPUT_PATHS.E5, 'screenshots');

/** 依角色分組的路由截圖定義 */
const ROUTE_CAPTURES: RouteCapture[] = [
  // HR01 - 登入與帳號
  { path: '/login', filename: 'HR01-P01-Login', description: '登入頁面', loginAs: 'admin' },
  { path: '/profile', filename: 'HR01-P02-Profile', description: '個人資料', loginAs: 'employee' },
  { path: '/profile/password', filename: 'HR01-P03-PasswordChange', description: '變更密碼', loginAs: 'employee' },
  { path: '/admin/users', filename: 'HR01-P04-UserManagement', description: '使用者管理', loginAs: 'admin' },
  { path: '/admin/roles', filename: 'HR01-P05-RoleManagement', description: '角色管理', loginAs: 'admin' },

  // HR02 - 組織與員工
  { path: '/admin/organization', filename: 'HR02-P01-OrgTree', description: '組織架構', loginAs: 'hr_admin' },
  { path: '/admin/employees', filename: 'HR02-P02-EmployeeList', description: '員工清單', loginAs: 'hr_admin' },

  // HR03 - 考勤
  { path: '/attendance/check-in', filename: 'HR03-P01-CheckIn', description: '打卡', loginAs: 'employee' },
  { path: '/attendance/leave/apply', filename: 'HR03-P02-LeaveApply', description: '請假申請', loginAs: 'employee' },
  { path: '/attendance/my-records', filename: 'HR03-P03-MyAttendance', description: '我的出勤', loginAs: 'employee' },
  { path: '/attendance/overtime', filename: 'HR03-P04-Overtime', description: '加班申請', loginAs: 'employee' },
  { path: '/attendance/leave/balance', filename: 'HR03-P05-LeaveBalance', description: '假別餘額', loginAs: 'employee' },
  { path: '/admin/attendance/approvals', filename: 'HR03-P06-Approvals', description: '假勤審核', loginAs: 'manager' },
  { path: '/admin/attendance/shifts', filename: 'HR03-P07-Shifts', description: '班別管理', loginAs: 'hr_admin' },
  { path: '/admin/attendance/leave-types', filename: 'HR03-P08-LeaveTypes', description: '假別管理', loginAs: 'hr_admin' },
  { path: '/admin/attendance/reports', filename: 'HR03-P09-Reports', description: '出勤報表', loginAs: 'hr_admin' },
  { path: '/admin/attendance/monthly-close', filename: 'HR03-P10-MonthClose', description: '月結作業', loginAs: 'hr_admin' },

  // HR04 - 薪資
  { path: '/admin/payroll/runs', filename: 'HR04-P01-PayrollRuns', description: '薪資批次', loginAs: 'hr_admin' },
  { path: '/profile/payslips', filename: 'HR04-P02-Payslips', description: '我的薪資單', loginAs: 'employee' },
  { path: '/admin/payroll/structures', filename: 'HR04-P03-Structures', description: '薪資結構', loginAs: 'hr_admin' },
  { path: '/admin/payroll/items', filename: 'HR04-P04-PayrollItems', description: '薪資項目', loginAs: 'hr_admin' },
  { path: '/admin/payroll/approval', filename: 'HR04-P05-Approval', description: '薪資審核', loginAs: 'hr_admin' },
  { path: '/admin/payroll/bank-transfer', filename: 'HR04-P06-BankTransfer', description: '銀行轉帳', loginAs: 'admin' },
  { path: '/admin/payroll/employees', filename: 'HR04-P07-History', description: '薪資歷史', loginAs: 'hr_admin' },

  // HR05 - 保險
  { path: '/admin/insurance/enrollments', filename: 'HR05-P01-Enrollments', description: '保險加退保', loginAs: 'hr_admin' },
  { path: '/admin/insurance/calculator', filename: 'HR05-P02-Calculator', description: '保費試算', loginAs: 'hr_admin' },
  { path: '/profile/insurance', filename: 'HR05-P03-MyInsurance', description: '我的保險', loginAs: 'employee' },

  // HR06 - 專案
  { path: '/admin/projects', filename: 'HR06-P01-ProjectList', description: '專案清單', loginAs: 'pm' },
  { path: '/admin/projects/customers', filename: 'HR06-P02-Customers', description: '客戶管理', loginAs: 'pm' },
  { path: '/admin/projects/new', filename: 'HR06-P03-NewProject', description: '新增專案', loginAs: 'pm' },

  // HR07 - 工時
  { path: '/profile/timesheets', filename: 'HR07-P01-Timesheet', description: '我的工時', loginAs: 'employee' },
  { path: '/admin/timesheets/approval', filename: 'HR07-P02-Approval', description: '工時審核', loginAs: 'pm' },
  { path: '/admin/timesheets/reports', filename: 'HR07-P03-Reports', description: '工時報表', loginAs: 'pm' },

  // HR08 - 績效
  { path: '/admin/performance/cycles', filename: 'HR08-P01-Cycles', description: '考核週期', loginAs: 'hr_admin' },
  { path: '/admin/performance/team', filename: 'HR08-P02-Team', description: '團隊績效', loginAs: 'manager' },
  { path: '/admin/performance/reports', filename: 'HR08-P03-Reports', description: '績效報表', loginAs: 'hr_admin' },
  { path: '/profile/performance', filename: 'HR08-P04-MyPerformance', description: '我的績效', loginAs: 'employee' },

  // HR09 - 招募
  { path: '/admin/recruitment', filename: 'HR09-P01-Recruitment', description: '招募管理', loginAs: 'hr_admin' },

  // HR10 - 訓練
  { path: '/admin/training', filename: 'HR10-P01-Training', description: '訓練課程', loginAs: 'employee' },

  // HR11 - 簽核
  { path: '/admin/workflow', filename: 'HR11-P01-WorkflowList', description: '簽核清單', loginAs: 'admin' },
  { path: '/admin/workflow/definitions', filename: 'HR11-P02-Definitions', description: '流程定義', loginAs: 'admin' },
  { path: '/profile/delegation', filename: 'HR11-P03-Delegation', description: '代理人設定', loginAs: 'employee' },

  // HR12 - 通知
  { path: '/profile/notifications', filename: 'HR12-P01-Notifications', description: '通知中心', loginAs: 'employee' },
  { path: '/profile/notification-settings', filename: 'HR12-P02-Settings', description: '通知偏好', loginAs: 'employee' },
  { path: '/admin/notifications/templates', filename: 'HR12-P03-Templates', description: '通知範本', loginAs: 'admin' },
  { path: '/admin/notifications/announcements', filename: 'HR12-P04-Announcements', description: '公告管理', loginAs: 'admin' },

  // HR13 - 文件
  { path: '/profile/documents', filename: 'HR13-P01-MyDocuments', description: '我的文件', loginAs: 'employee' },
  { path: '/admin/documents', filename: 'HR13-P02-Management', description: '文件管理', loginAs: 'admin' },
  { path: '/admin/documents/templates', filename: 'HR13-P03-Templates', description: '文件範本', loginAs: 'admin' },

  // HR14 - 報表
  { path: '/admin/reports', filename: 'HR14-P01-Dashboard', description: '報表儀表板', loginAs: 'admin' },
  { path: '/admin/reports/hr', filename: 'HR14-P02-HRReport', description: 'HR 報表', loginAs: 'hr_admin' },
  { path: '/admin/reports/project', filename: 'HR14-P03-ProjectReport', description: '專案報表', loginAs: 'pm' },
  { path: '/admin/reports/finance', filename: 'HR14-P04-FinanceReport', description: '財務報表', loginAs: 'admin' },
];

/** 讀取 UI 規格書作為操作說明 */
async function loadUiSpec(serviceCode: string): Promise<string> {
  const service = SERVICES.find(s => s.code === serviceCode);
  if (!service) return '';

  // 搜尋對應的 UI 規格書
  const uiDesignDir = PATHS.uiDesign;
  try {
    const files = await fs.readdir(uiDesignDir);
    const specFile = files.find(f => f.startsWith(`HR${serviceCode}`));
    if (specFile) {
      return await fs.readFile(path.join(uiDesignDir, specFile), 'utf-8');
    }
  } catch { /* 目錄或檔案不存在 */ }
  return '';
}

/** 生成操作手冊 HTML 內容 */
async function generateManualContent(screenshotDir: string): Promise<string> {
  let html = '';

  // 系統概述
  html += `<h1>1. 系統概述</h1>
    <p>${DOC_META.company}是一套企業級人力資源暨專案管理系統，涵蓋 14 個功能模組，
    包含員工管理、考勤、薪資、保險、專案、工時、績效、招募、訓練、簽核、通知、文件及報表等完整功能。</p>
    <h2>1.1 系統帳號</h2>
    <table>
      <thead><tr><th>帳號</th><th>角色</th><th>密碼</th><th>說明</th></tr></thead>
      <tbody>
        <tr><td>admin</td><td>ADMIN</td><td>Admin@123</td><td>系統管理員</td></tr>
        <tr><td>hr_admin</td><td>HR</td><td>Admin@123</td><td>人資管理員</td></tr>
        <tr><td>employee</td><td>EMPLOYEE</td><td>Admin@123</td><td>一般員工</td></tr>
        <tr><td>manager</td><td>MANAGER</td><td>Admin@123</td><td>部門主管</td></tr>
        <tr><td>pm</td><td>PM</td><td>Admin@123</td><td>專案經理</td></tr>
      </tbody>
    </table>`;

  // 依模組分組截圖
  const serviceGroups: Record<string, { service: ServiceMeta; captures: RouteCapture[] }> = {};
  for (const capture of ROUTE_CAPTURES) {
    const code = capture.filename.substring(2, 4);
    if (!serviceGroups[code]) {
      const service = SERVICES.find(s => s.code === code);
      if (service) {
        serviceGroups[code] = { service, captures: [] };
      }
    }
    serviceGroups[code]?.captures.push(capture);
  }

  let chapterNum = 2;
  for (const [code, group] of Object.entries(serviceGroups).sort()) {
    const { service, captures } = group;
    html += `<h1>${chapterNum}. ${service.name}（HR${code}）</h1>`;

    // 嘗試讀取 UI 規格書的概述
    const uiSpec = await loadUiSpec(code);
    if (uiSpec) {
      // 擷取第一段描述
      const introMatch = uiSpec.match(/^#[^#].*\n\n([\s\S]*?)(?=\n##|\n#[^#])/);
      if (introMatch) {
        html += `<p>${introMatch[1].trim()}</p>`;
      }
    }

    let pageNum = 1;
    for (const capture of captures) {
      html += `<h2>${chapterNum}.${pageNum} ${capture.description}</h2>`;
      html += `<p><strong>路徑：</strong><code>${capture.path}</code></p>`;
      html += `<p><strong>適用角色：</strong>${capture.loginAs?.toUpperCase() || 'ALL'}</p>`;

      // 嵌入截圖
      const screenshotPath = path.join(screenshotDir, `${capture.filename}.png`);
      try {
        await fs.access(screenshotPath);
        const imgBuffer = await fs.readFile(screenshotPath);
        const base64 = imgBuffer.toString('base64');
        html += `<div class="screenshot-container">
          <img src="data:image/png;base64,${base64}" alt="${capture.description}" />
          <p class="screenshot-caption">圖 ${chapterNum}.${pageNum} — ${capture.description}</p>
        </div>`;
      } catch {
        html += `<div class="diagram-error">截圖尚未生成：${capture.filename}.png</div>`;
      }

      pageNum++;
    }
    chapterNum++;
  }

  return html;
}

/** E5 主入口：生成操作手冊 */
export async function generateE5(): Promise<void> {
  console.log('\n--- E5: 系統操作手冊 ---\n');

  // 步驟一：截圖（如前端運行中）
  let screenshotsAvailable = false;
  try {
    const response = await fetch(FRONTEND_URL, { signal: AbortSignal.timeout(3000) });
    if (response.ok) {
      console.log('[E5] 前端運行中，開始截圖...');
      const capture = new ScreenshotCapture({
        baseUrl: FRONTEND_URL,
        outputDir: SCREENSHOT_DIR,
      });

      try {
        await capture.launch();
        // 先登入 admin
        await capture.login('admin');
        await capture.captureRoutes(ROUTE_CAPTURES);
        screenshotsAvailable = true;
      } finally {
        await capture.close();
      }
    }
  } catch {
    console.log('[E5] 前端未運行，跳過截圖步驟（使用佔位符）');
  }

  // 步驟二：生成 PDF
  console.log('[E5] 生成操作手冊 PDF...');
  const bodyHtml = await generateManualContent(SCREENSHOT_DIR);

  const title = '系統操作手冊';
  const html = await renderFullDocument({
    title,
    subtitle: DOC_META.company,
    bodyHtml,
    showToc: false,
    showCover: true,
    headerText: title,
  });

  const outputPath = path.join(OUTPUT_PATHS.E5, '系統操作手冊.pdf');
  await pdfGenerator.generateFromHtml(html, {
    outputPath,
    headerText: title,
    mermaidWaitMs: 1000,
  });

  console.log(`\nE5 完成: ${screenshotsAvailable ? '含截圖' : '無截圖（佔位符模式）'}`);
}

// 支援直接執行
const isDirectRun = process.argv[1]?.includes('e5-operation-manual');
if (isDirectRun) {
  generateE5().then(() => pdfGenerator.close()).catch(console.error);
}
