#!/usr/bin/env tsx
/**
 * 獨立截圖腳本
 * 用途：接受 --url 參數，自動截圖前端所有路由
 *
 * 使用方式：
 *   npx tsx src/screenshot-runner.ts --url http://localhost:5173
 *   npx tsx src/screenshot-runner.ts --url http://localhost:5173 --output ./output/E5_系統操作手冊/screenshots
 */

import path from 'path';
import { ScreenshotCapture, RouteCapture } from './shared/screenshot-capture.js';
import { OUTPUT_PATHS } from './config.js';

/** 命令列參數解析 */
function parseArgs(): { url: string; output: string } {
  const args = process.argv.slice(2);
  let url = 'http://localhost:5173';
  let output = path.join(OUTPUT_PATHS.E5, 'screenshots');

  for (let i = 0; i < args.length; i++) {
    if (args[i] === '--url' && args[i + 1]) {
      url = args[i + 1];
      i++;
    } else if (args[i] === '--output' && args[i + 1]) {
      output = args[i + 1];
      i++;
    }
  }

  return { url, output };
}

/** 完整路由截圖定義 */
const ROUTE_CAPTURES: RouteCapture[] = [
  // HR01 - 登入與帳號
  { path: '/login', filename: 'HR01-P01-Login', description: '登入頁面', loginAs: 'admin' },
  { path: '/profile', filename: 'HR01-P02-Profile', description: '個人資料', loginAs: 'employee' },
  { path: '/profile/password', filename: 'HR01-P03-PasswordChange', description: '變更密碼', loginAs: 'employee' },
  { path: '/admin/users', filename: 'HR01-P04-UserManagement', description: '使用者管理', loginAs: 'admin' },
  { path: '/admin/roles', filename: 'HR01-P05-RoleManagement', description: '角色管理', loginAs: 'admin' },
  { path: '/admin/system', filename: 'HR01-P06-SystemManagement', description: '系統管理', loginAs: 'admin' },

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

/** 主程式 */
async function main(): Promise<void> {
  const { url, output } = parseArgs();
  console.log(`[Screenshot Runner] URL: ${url}`);
  console.log(`[Screenshot Runner] Output: ${output}`);
  console.log(`[Screenshot Runner] Routes: ${ROUTE_CAPTURES.length}`);

  // 檢查前端是否運行
  try {
    const response = await fetch(url, { signal: AbortSignal.timeout(5000) });
    if (!response.ok) {
      console.error('[Screenshot Runner] 前端回應異常，中止截圖');
      process.exit(1);
    }
  } catch {
    console.error('[Screenshot Runner] 無法連線前端，請確認已啟動 dev server');
    process.exit(1);
  }

  const capture = new ScreenshotCapture({ baseUrl: url, outputDir: output });

  try {
    await capture.launch();
    await capture.login('admin');
    const results = await capture.captureRoutes(ROUTE_CAPTURES);
    console.log(`\n[Screenshot Runner] 完成! 共截取 ${results.size} 張截圖`);
  } finally {
    await capture.close();
  }
}

main().catch(err => {
  console.error('[Screenshot Runner] 執行失敗:', err);
  process.exit(1);
});
