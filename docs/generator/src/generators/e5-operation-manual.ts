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

// ===== 使用者導向文字清理 =====

/** 未實作功能關鍵字（過濾操作列） */
const EXCLUDED_KEYWORDS = ['SSO', 'OAuth2', 'oauth2', 'Google', 'Azure'];

/** 技術術語 → 白話文對照表 */
type Replacement = string | ((...args: string[]) => string);
const TERM_REPLACEMENTS: [RegExp, Replacement][] = [
  // API 路徑
  [/`(GET|POST|PUT|DELETE|PATCH)\s+\/api\/[^`]*`/g, ''],
  [/(GET|POST|PUT|DELETE|PATCH)\s+\/api\/\S+/g, ''],
  // 事件 ID
  [/E-[A-Z]+-\d+/g, ''],
  // 程式元件
  [/Modal/g, '彈出視窗'],
  [/message\.success\([^)]*\)/g, '顯示成功提示'],
  [/message\.error\(([^)]*)\)/g, '顯示錯誤提示：$1'],
  [/message\.success/g, '顯示成功提示'],
  [/message\.error/g, '顯示錯誤提示'],
  [/autofocus/g, '游標自動定位'],
  [/loading\s*(狀態|spinner)?/gi, '載入中'],
  [/navigate\(-1\)\s*返回?/g, '返回上一頁'],
  [/Divider/g, ''],
  [/Form\s*\+?\s*Input/g, ''],
  [/`[^`]*`/g, (match: string) => {
    const inner = match.slice(1, -1);
    if (/[\u4e00-\u9fff]/.test(inner)) return inner;
    if (/^[a-zA-Z_./]/.test(inner)) return '';
    return inner;
  }],
  [/\s{2,}/g, ' '],
  [/^[\s,、，]+|[\s,、，]+$/g, ''],
];

/** 將技術描述轉為使用者導向白話文 */
function cleanForUser(text: string): string {
  if (!text) return '';
  let cleaned = text;
  for (const [pattern, replacement] of TERM_REPLACEMENTS) {
    cleaned = cleaned.replace(pattern, replacement as string);
  }
  return cleaned.trim();
}

/** 清除 purpose 中的 SSO 相關描述 */
function cleanPurpose(text: string): string {
  if (!text) return '';
  return text
    .replace(/[，,]?\s*(?:支援|包含|整合)?\s*(?:Google|Azure)?\s*SSO\s*(?:登入|認證|整合)?/g, '')
    .replace(/與\s*SSO/g, '')
    .replace(/SSO\s*與/g, '')
    .replace(/[與和及]\s*$/g, '')
    .replace(/\s{2,}/g, ' ')
    .trim();
}

/** 檢查文字是否包含排除關鍵字 */
function containsExcluded(text: string): boolean {
  return EXCLUDED_KEYWORDS.some(kw => text.includes(kw));
}

// ===== 畫面規格書解析（保留供未來擴充） =====

/** 頁面操作資訊（從畫面規格書擷取） */
interface PageInfo {
  pageCode: string;
  pageName: string;
  purpose: string;
  roles: string;
  entry: string;
  pageType: string;
  operations: { trigger: string; action: string; result: string }[];
  states: { state: string; display: string }[];
}

/** 讀取 UI 規格書原始文字 */
async function loadUiSpecRaw(serviceCode: string): Promise<string> {
  try {
    const files = await fs.readdir(PATHS.uiDesign);
    const specFile = files.find(f => f.startsWith(`HR${serviceCode}`) && f.endsWith('畫面規格書.md'));
    if (specFile) {
      return await fs.readFile(path.join(PATHS.uiDesign, specFile), 'utf-8');
    }
  } catch { /* 目錄或檔案不存在 */ }
  return '';
}

/** 從表格中擷取指定項目的值 */
function extractTableValue(section: string, key: string): string {
  const regex = new RegExp(`\\|\\s*\\*\\*${key}\\*\\*\\s*\\|\\s*(.+?)\\s*\\|`, 'i');
  const match = section.match(regex);
  return match ? match[1].trim() : '';
}

/** 解析操作事件規格表 */
function parseOperations(section: string): PageInfo['operations'] {
  const ops: PageInfo['operations'] = [];
  const eventBlock = section.match(/操作事件規格[\s\S]*?\n(\|[\s\S]*?)(?=\n###|\n---|\n## |\s*$)/);
  if (!eventBlock) return ops;

  const rows = eventBlock[1].split('\n').filter(r => r.startsWith('|') && !r.includes('---') && !r.includes('事件 ID'));
  for (const row of rows) {
    if (containsExcluded(row)) continue;
    const cells = row.split('|').map(c => c.trim()).filter(Boolean);
    if (cells.length >= 4) {
      const trigger = cleanForUser(cells[1] || '');
      const action = cleanForUser(cells[2] || '');
      const result = cleanForUser(cells[4] || cells[3] || '');
      if (!trigger && !action && !result) continue;
      ops.push({ trigger, action, result });
    }
  }
  return ops;
}

/** 解析狀態與互動表 */
function parseStates(section: string): PageInfo['states'] {
  const states: PageInfo['states'] = [];
  const stateBlock = section.match(/狀態與互動[\s\S]*?\n(\|[\s\S]*?)(?=\n###|\n---|\n## |\s*$)/);
  if (!stateBlock) return states;

  const rows = stateBlock[1].split('\n').filter(r => r.startsWith('|') && !r.includes('---') && !r.includes('狀態'));
  for (const row of rows) {
    if (containsExcluded(row)) continue;
    const cells = row.split('|').map(c => c.trim()).filter(Boolean);
    if (cells.length >= 2) {
      states.push({
        state: cleanForUser(cells[0]),
        display: cleanForUser(cells[1]),
      });
    }
  }
  return states;
}

/** 解析所有 UI 規格書（保留供未來擴充） */
async function parseAllUiSpecs(): Promise<Map<string, PageInfo>> {
  const routeMap = new Map<string, PageInfo>();

  for (const service of SERVICES) {
    const spec = await loadUiSpecRaw(service.code);
    if (!spec) continue;

    const routeToCode = new Map<string, string>();
    const pageListMatch = spec.match(/## 2\.\s*頁面清單[\s\S]*?\n(\|[\s\S]*?)(?=\n---|\n## )/);
    if (pageListMatch) {
      const rows = pageListMatch[1].split('\n').filter(r => r.startsWith('|') && !r.includes('---') && !r.includes('代碼'));
      for (const row of rows) {
        const cells = row.split('|').map(c => c.trim()).filter(Boolean);
        if (cells.length >= 3) {
          const code = cells[0].replace(/\s/g, '');
          const route = cells[2].replace(/`/g, '').trim();
          if (route.startsWith('/') && code.includes('-P')) {
            routeToCode.set(route, code);
          }
        }
      }
    }

    const pageSections = spec.split(/(?=\n## \d+\.\s+HR\d{2}-[PM]\d{2})/);
    for (const section of pageSections) {
      const headerMatch = section.match(/## \d+\.\s+(HR\d{2}-P\d{2})\s+(.+)/);
      if (!headerMatch) continue;

      const pageCode = headerMatch[1];
      const pageName = headerMatch[2].trim();
      if (pageName.includes('~') || pageName.includes('簡要')) continue;

      const info: PageInfo = {
        pageCode,
        pageName,
        purpose: extractTableValue(section, '用途'),
        roles: extractTableValue(section, '使用角色'),
        entry: extractTableValue(section, '進入方式'),
        pageType: extractTableValue(section, '頁面類型'),
        operations: parseOperations(section),
        states: parseStates(section),
      };

      for (const [route, code] of routeToCode) {
        if (code === pageCode) {
          routeMap.set(route, info);
          break;
        }
      }
    }

    const briefSections = spec.split(/(?=\n### HR\d{2}-P\d{2}\s)/);
    for (const section of briefSections) {
      const headerMatch = section.match(/### (HR\d{2}-P\d{2})\s+(.+)/);
      if (!headerMatch) continue;

      const pageCode = headerMatch[1];
      if (routeMap.has(pageCode)) continue;

      const pageName = headerMatch[2].trim();
      const purposeMatch = section.match(/\|\s*主要功能\s*\|\s*(.+?)\s*\|/);
      const typeMatch = section.match(/\|\s*頁面類型\s*\|\s*(.+?)\s*\|/);
      const flowMatch = section.match(/\|\s*操作流程\s*\|\s*(.+?)\s*\|/);
      const ruleMatch = section.match(/\|\s*特殊規則\s*\|\s*(.+?)\s*\|/);

      const info: PageInfo = {
        pageCode,
        pageName,
        purpose: purposeMatch?.[1]?.trim() || '',
        roles: '',
        entry: '',
        pageType: typeMatch?.[1]?.trim() || '',
        operations: flowMatch ? [{
          trigger: '操作流程',
          action: flowMatch[1].trim(),
          result: ruleMatch?.[1]?.trim() || '',
        }] : [],
        states: [],
      };

      for (const [route, code] of routeToCode) {
        if (code === pageCode) {
          routeMap.set(route, info);
          break;
        }
      }
    }
  }

  return routeMap;
}

/** 角色代碼轉中文 */
const ROLE_LABELS: Record<string, string> = {
  admin: '系統管理員',
  hr_admin: '人資管理員',
  employee: '一般員工',
  manager: '部門主管',
  pm: '專案經理',
};

// ===== 角色導向流程定義 =====

/** 流程步驟 */
interface FlowStep {
  description: string;
  screenshotKeys: string[];
  tip?: string;
}

/** 操作流程 */
interface OperationFlow {
  title: string;
  summary: string;
  steps: FlowStep[];
}

/** 角色章節 */
interface RoleChapter {
  title: string;
  role: string;
  description: string;
  flows: OperationFlow[];
}

/** 全部角色章節定義（第 2～6 章） */
const ROLE_CHAPTERS: RoleChapter[] = [
  // ========== 第 2 章：一般員工操作 ==========
  {
    title: '一般員工操作',
    role: '一般員工（EMPLOYEE）',
    description: '本章說明一般員工日常使用的功能，包括打卡、請假、查詢薪資單等。使用「employee / Admin@123」帳號登入。',
    flows: [
      {
        title: '每日考勤打卡',
        summary: '每天上下班時，透過系統完成打卡紀錄。',
        steps: [
          { description: '登入系統後，從左側選單點選「考勤管理」→「打卡」', screenshotKeys: ['HR03-P01-CheckIn'] },
          { description: '確認目前時間無誤，點擊「上班打卡」或「下班打卡」按鈕', screenshotKeys: [] },
          { description: '畫面顯示打卡成功訊息，打卡紀錄會即時更新在頁面下方', screenshotKeys: [], tip: '若遲到或早退，系統會自動標記異常，請留意出勤狀態。' },
        ],
      },
      {
        title: '請假申請',
        summary: '當需要請假時，填寫假單並送出審核。',
        steps: [
          { description: '從左側選單點選「考勤管理」→「請假申請」', screenshotKeys: ['HR03-P02-LeaveApply'] },
          { description: '選擇假別（如特休、事假、病假），填寫起迄日期與請假事由', screenshotKeys: [] },
          { description: '確認內容無誤後，點擊「送出申請」', screenshotKeys: [], tip: '送出後可在「我的出勤」頁面追蹤審核進度。主管審核通過後，假別餘額會自動扣減。' },
        ],
      },
      {
        title: '加班申請',
        summary: '加班前或加班後，透過系統提交加班申請。',
        steps: [
          { description: '從左側選單點選「考勤管理」→「加班申請」', screenshotKeys: ['HR03-P04-Overtime'] },
          { description: '填寫加班日期、起迄時間與加班原因', screenshotKeys: [] },
          { description: '點擊「送出申請」，等待主管審核', screenshotKeys: [], tip: '加班時數經主管核准後，會自動計入當月薪資結算。' },
        ],
      },
      {
        title: '查詢出勤紀錄',
        summary: '查看自己的打卡紀錄、請假與加班歷史，以及假別剩餘天數。',
        steps: [
          { description: '從左側選單點選「考勤管理」→「我的出勤」，查看當月出勤紀錄', screenshotKeys: ['HR03-P03-MyAttendance'] },
          { description: '切換月份可查看歷史紀錄，異常打卡會以紅色標記', screenshotKeys: [] },
          { description: '點選「假別餘額」頁籤，確認各假別的剩餘天數', screenshotKeys: ['HR03-P05-LeaveBalance'] },
        ],
      },
      {
        title: '查詢薪資單',
        summary: '每月發薪後，可在系統查看個人薪資明細。',
        steps: [
          { description: '從左側選單點選「個人專區」→「我的薪資單」', screenshotKeys: ['HR04-P02-Payslips'] },
          { description: '選擇年月查看該期薪資明細，包含本薪、加項、扣項與實發金額', screenshotKeys: [], tip: '如對薪資有疑問，請聯繫人資部門。' },
        ],
      },
      {
        title: '查詢個人保險',
        summary: '查看自己的勞保、健保與退休金投保資訊。',
        steps: [
          { description: '從左側選單點選「個人專區」→「我的保險」', screenshotKeys: ['HR05-P03-MyInsurance'] },
          { description: '頁面顯示目前投保級距、保費分攤與退休金提撥等資訊', screenshotKeys: [] },
        ],
      },
      {
        title: '工時回報',
        summary: '每週填報工時紀錄，記錄各專案的投入時數。',
        steps: [
          { description: '從左側選單點選「個人專區」→「我的工時」', screenshotKeys: ['HR07-P01-Timesheet'] },
          { description: '選擇週別，在表格中填入每天各專案的工作時數', screenshotKeys: [] },
          { description: '確認總時數正確後，點擊「送出」提交給專案經理審核', screenshotKeys: [], tip: '每週五前完成填報，逾期可能影響專案結算。' },
        ],
      },
      {
        title: '績效自評',
        summary: '在考核週期內完成個人績效自評。',
        steps: [
          { description: '從左側選單點選「個人專區」→「我的績效」', screenshotKeys: ['HR08-P04-MyPerformance'] },
          { description: '系統顯示當前考核週期與待填項目，依指標逐項填寫自評分數與說明', screenshotKeys: [] },
          { description: '填寫完成後點擊「送出自評」，等待主管進行考核', screenshotKeys: [] },
        ],
      },
      {
        title: '訓練課程',
        summary: '瀏覽並報名公司提供的訓練課程，查看個人學習紀錄。',
        steps: [
          { description: '從左側選單點選「訓練管理」', screenshotKeys: ['HR10-P01-Training'] },
          { description: '瀏覽可報名的課程列表，點選課程名稱查看詳情', screenshotKeys: [] },
          { description: '點擊「報名」按鈕完成報名，完課後紀錄會自動更新', screenshotKeys: [] },
        ],
      },
      {
        title: '個人設定',
        summary: '管理個人資料、密碼、通知偏好、代理人設定與個人文件。',
        steps: [
          { description: '點選右上角頭像或「個人專區」→「個人資料」，可修改聯絡電話、緊急聯絡人等基本資訊', screenshotKeys: ['HR01-P02-Profile'] },
          { description: '進入「變更密碼」頁面，輸入舊密碼與新密碼完成密碼修改', screenshotKeys: ['HR01-P03-PasswordChange'], tip: '密碼須至少 8 碼，包含大小寫英文與數字。' },
          { description: '進入「通知偏好」設定接收通知的管道（系統通知、Email）', screenshotKeys: ['HR12-P02-Settings'] },
          { description: '進入「代理人設定」，指定請假期間的職務代理人', screenshotKeys: ['HR11-P03-Delegation'] },
          { description: '進入「我的文件」，上傳或下載個人相關文件', screenshotKeys: ['HR13-P01-MyDocuments'] },
        ],
      },
    ],
  },

  // ========== 第 3 章：部門主管操作 ==========
  {
    title: '部門主管操作',
    role: '部門主管（MANAGER）',
    description: '本章說明部門主管的管理功能，包括審核部屬假勤與團隊績效考核。使用「manager / Admin@123」帳號登入。',
    flows: [
      {
        title: '差勤審核（請假／加班）',
        summary: '審核部屬提交的請假申請與加班申請。',
        steps: [
          { description: '從左側選單點選「考勤管理」→「假勤審核」', screenshotKeys: ['HR03-P06-Approvals'] },
          { description: '待審核清單會列出所有部屬的申請，點選任一筆查看詳細內容', screenshotKeys: [] },
          { description: '確認申請內容合理後，點擊「核准」；若有疑慮，填寫退回原因後點擊「退回」', screenshotKeys: [], tip: '退回的申請，員工可修改後重新送出。建議當天處理待審項目，避免延誤員工權益。' },
        ],
      },
      {
        title: '團隊績效考核',
        summary: '在考核週期內，對部屬進行績效評分與評語。',
        steps: [
          { description: '從左側選單點選「績效管理」→「團隊績效」', screenshotKeys: ['HR08-P02-Team'] },
          { description: '系統列出所有待考核的部屬名單，點選員工姓名進入考核頁面', screenshotKeys: [] },
          { description: '參考員工的自評內容，逐項填寫主管評分與評語', screenshotKeys: [] },
          { description: '完成所有部屬考核後，點擊「送出考核」提交給人資部門', screenshotKeys: [], tip: '考核一旦送出即無法修改，請仔細確認後再提交。' },
        ],
      },
    ],
  },

  // ========== 第 4 章：專案經理操作 ==========
  {
    title: '專案經理操作',
    role: '專案經理（PM）',
    description: '本章說明專案經理的管理功能，包括專案管理、工時審核與報表查閱。使用「pm / Admin@123」帳號登入。',
    flows: [
      {
        title: '專案建立與管理',
        summary: '建立新專案、管理客戶資料與專案設定。',
        steps: [
          { description: '從左側選單點選「專案管理」→「專案清單」，查看目前所有專案', screenshotKeys: ['HR06-P01-ProjectList'] },
          { description: '點擊「新增專案」按鈕，填寫專案名稱、客戶、起迄日期與預算', screenshotKeys: ['HR06-P03-NewProject'] },
          { description: '如需新增客戶，先進入「客戶管理」頁面建立客戶資料', screenshotKeys: ['HR06-P02-Customers'] },
          { description: '專案建立後，可在專案詳情頁設定工作分解結構（WBS）與成員', screenshotKeys: [], tip: '專案狀態包含「規劃中」→「進行中」→「已完成」，變更狀態前請確認所有工時已結算。' },
        ],
      },
      {
        title: '工時審核',
        summary: '審核專案成員提交的每週工時紀錄。',
        steps: [
          { description: '從左側選單點選「工時管理」→「工時審核」', screenshotKeys: ['HR07-P02-Approval'] },
          { description: '選擇專案與週別，查看各成員填報的工時', screenshotKeys: [] },
          { description: '確認工時合理後點擊「核准」；如有問題，填寫備註後「退回」', screenshotKeys: [], tip: '核准後的工時會納入專案成本計算。' },
        ],
      },
      {
        title: '工時報表與專案報表',
        summary: '查看專案工時統計與成本分析報表。',
        steps: [
          { description: '從左側選單點選「工時管理」→「工時報表」，查看按專案／成員的工時彙總', screenshotKeys: ['HR07-P03-Reports'] },
          { description: '切換到「報表中心」→「專案報表」，查看跨專案的成本與進度分析', screenshotKeys: ['HR14-P03-ProjectReport'] },
        ],
      },
    ],
  },

  // ========== 第 5 章：人資管理員操作 ==========
  {
    title: '人資管理員操作',
    role: '人資管理員（HR）',
    description: '本章說明人資管理員的日常作業流程，涵蓋員工到離職、薪資結算、保險管理等跨模組操作。使用「hr_admin / Admin@123」帳號登入。',
    flows: [
      {
        title: '新人到職（跨模組 5 步驟）',
        summary: '新員工報到時，需依序在多個模組完成建檔作業，確保系統資料完整。',
        steps: [
          {
            description: '步驟一：建立員工基本資料。進入「組織管理」→「員工清單」，點擊「新增員工」，填寫姓名、身分證字號、到職日、部門、職稱等資訊',
            screenshotKeys: ['HR02-P02-EmployeeList'],
          },
          {
            description: '步驟二：建立系統帳號。進入「帳號管理」→「使用者管理」，為新員工建立登入帳號並指派角色（一般為 EMPLOYEE）',
            screenshotKeys: ['HR01-P04-UserManagement'],
          },
          {
            description: '步驟三：辦理勞健保加保。進入「保險管理」→「加退保作業」，新增加保記錄，填寫投保級距',
            screenshotKeys: ['HR05-P01-Enrollments'],
            tip: '加保日期應與到職日一致。投保薪資應依實際薪資對照勞保投保薪資分級表填寫。',
          },
          {
            description: '步驟四：設定薪資結構。進入「薪資管理」→「薪資結構」，為新員工設定本薪、津貼等薪資項目',
            screenshotKeys: ['HR04-P03-Structures'],
          },
          {
            description: '步驟五：確認資料完整。回到「員工清單」確認新員工狀態為「在職」，並檢查各模組資料是否正確',
            screenshotKeys: ['HR02-P02-EmployeeList'],
            tip: '建議製作新人到職檢核表，逐項確認以避免遺漏。',
          },
        ],
      },
      {
        title: '員工離職（跨模組連動）',
        summary: '員工離職時，需處理人事異動、帳號停用、保險退保等作業。',
        steps: [
          {
            description: '在「員工清單」中找到離職員工，點擊「編輯」更新離職日期與離職原因，將狀態改為「離職」',
            screenshotKeys: ['HR02-P02-EmployeeList'],
          },
          {
            description: '進入「保險管理」→「加退保作業」，辦理勞健保退保',
            screenshotKeys: ['HR05-P01-Enrollments'],
            tip: '退保日期為離職日的隔天。離職當月薪資需在薪資結算時一併處理。',
          },
        ],
      },
      {
        title: '班別與假別設定',
        summary: '設定公司的上班班別與可請假別。',
        steps: [
          { description: '進入「考勤管理」→「班別管理」，設定各班別的上下班時間、彈性時間等', screenshotKeys: ['HR03-P07-Shifts'] },
          { description: '進入「考勤管理」→「假別管理」，設定假別名稱、給假天數與適用規則', screenshotKeys: ['HR03-P08-LeaveTypes'], tip: '假別設定會影響員工的假別餘額計算，建議於每年初檢視並更新。' },
        ],
      },
      {
        title: '考勤月結',
        summary: '每月底執行考勤結算，產生當月出勤統計。',
        steps: [
          { description: '進入「考勤管理」→「月結作業」，選擇結算年月', screenshotKeys: ['HR03-P10-MonthClose'] },
          { description: '確認所有員工當月的請假、加班申請皆已審核完畢', screenshotKeys: [] },
          { description: '點擊「執行月結」，系統自動統計出勤、遲到、早退、請假與加班時數', screenshotKeys: [], tip: '月結完成後的資料會自動傳送給薪資模組，作為薪資結算依據。' },
        ],
      },
      {
        title: '薪資結算與發放（跨模組 4 步驟）',
        summary: '每月進行薪資結算，從計算到銀行轉帳的完整流程。',
        steps: [
          {
            description: '步驟一：確認薪資項目。進入「薪資管理」→「薪資項目」，檢視本月是否有新增或調整的薪資項目',
            screenshotKeys: ['HR04-P04-PayrollItems'],
          },
          {
            description: '步驟二：執行薪資批次計算。進入「薪資管理」→「薪資批次」，點擊「新增批次」選擇年月，系統自動計算所有員工的薪資',
            screenshotKeys: ['HR04-P01-PayrollRuns'],
          },
          {
            description: '步驟三：薪資審核。進入「薪資管理」→「薪資審核」，逐筆或批次審核薪資計算結果',
            screenshotKeys: ['HR04-P05-Approval'],
            tip: '審核時請特別留意加班費、請假扣款與新進／離職人員的日薪計算。',
          },
          {
            description: '步驟四：銀行轉帳。審核通過後，進入「薪資管理」→「銀行轉帳」，產生轉帳檔案並執行發薪',
            screenshotKeys: ['HR04-P06-BankTransfer'],
          },
        ],
      },
      {
        title: '保險管理',
        summary: '管理員工的勞健保加退保作業與保費試算。',
        steps: [
          { description: '進入「保險管理」→「加退保作業」，查看所有員工的投保狀態，處理加保、退保或調整投保級距', screenshotKeys: ['HR05-P01-Enrollments'] },
          { description: '使用「保費試算」功能，輸入投保薪資計算勞保、健保與退休金的各項費用', screenshotKeys: ['HR05-P02-Calculator'], tip: '每年 1 月及 7 月需配合政府公告調整投保級距與費率。' },
        ],
      },
      {
        title: '績效考核管理',
        summary: '建立考核週期、追蹤考核進度並彙整考核報表。',
        steps: [
          { description: '進入「績效管理」→「考核週期」，建立新的考核週期，設定期間與考核指標', screenshotKeys: ['HR08-P01-Cycles'] },
          { description: '考核期間結束後，從「績效報表」查看全公司的考核統計與分布', screenshotKeys: ['HR08-P03-Reports'] },
        ],
      },
      {
        title: '招募管理',
        summary: '管理職缺刊登、面試安排與錄取作業。',
        steps: [
          { description: '進入「招募管理」，查看所有職缺與應徵者狀態', screenshotKeys: ['HR09-P01-Recruitment'] },
          { description: '新增職缺時，填寫職位名稱、部門、職責描述與任職條件', screenshotKeys: [] },
          { description: '應徵者進入後，透過看板拖拉更新面試進度（初篩→面試→錄取→報到）', screenshotKeys: [], tip: '錄取確認後，可直接從招募模組發起新人到職流程。' },
        ],
      },
      {
        title: '組織架構維護',
        summary: '管理公司的部門組織結構與層級關係。',
        steps: [
          { description: '進入「組織管理」→「組織架構」，以樹狀圖檢視目前的部門結構', screenshotKeys: ['HR02-P01-OrgTree'] },
          { description: '點擊部門節點可新增子部門、修改部門名稱或調整上級部門', screenshotKeys: [], tip: '調整組織架構前，請確認相關員工的部門歸屬已妥善處理。' },
        ],
      },
      {
        title: '出勤報表／HR 報表',
        summary: '查看出勤統計報表與人資管理相關報表。',
        steps: [
          { description: '進入「考勤管理」→「出勤報表」，按部門或個人查看出勤統計', screenshotKeys: ['HR03-P09-Reports'] },
          { description: '進入「報表中心」→「HR 報表」，查看人力結構、離職率等管理報表', screenshotKeys: ['HR14-P02-HRReport'] },
        ],
      },
    ],
  },

  // ========== 第 6 章：系統管理員操作 ==========
  {
    title: '系統管理員操作',
    role: '系統管理員（ADMIN）',
    description: '本章說明系統管理員的管理功能，包括帳號管理、系統參數設定、簽核流程與報表等。使用「admin / Admin@123」帳號登入。',
    flows: [
      {
        title: '使用者與角色管理',
        summary: '管理系統帳號的建立、停用，以及角色權限的設定。',
        steps: [
          { description: '進入「帳號管理」→「使用者管理」，查看所有系統帳號。可新增、編輯或停用帳號', screenshotKeys: ['HR01-P04-UserManagement'] },
          { description: '進入「帳號管理」→「角色管理」，設定各角色可存取的功能模組與頁面', screenshotKeys: ['HR01-P05-RoleManagement'], tip: '停用帳號後，該使用者將無法登入系統。如需恢復，可重新啟用。' },
        ],
      },
      {
        title: '系統參數／功能開關／排程任務',
        summary: '管理系統運行所需的參數設定、功能開關與排程任務。',
        steps: [
          { description: '進入「帳號管理」→「系統管理」', screenshotKeys: ['HR01-P06-SystemManagement'] },
          { description: '在「系統參數」頁籤中，調整密碼長度、會議逾時時間、員編格式等系統參數', screenshotKeys: [] },
          { description: '在「功能開關」頁籤中，啟用或停用特定功能模組', screenshotKeys: [] },
          { description: '在「排程任務」頁籤中，查看並調整自動執行的排程任務（如考勤結算、通知發送等）', screenshotKeys: [], tip: '修改系統參數後會立即生效，建議於非上班時間調整以降低影響。' },
        ],
      },
      {
        title: '簽核流程設計',
        summary: '設計與管理各類業務的簽核流程。',
        steps: [
          { description: '進入「簽核管理」→「簽核清單」，查看目前使用中的簽核流程', screenshotKeys: ['HR11-P01-WorkflowList'] },
          { description: '進入「流程定義」，使用視覺化流程設計器建立或修改簽核流程', screenshotKeys: ['HR11-P02-Definitions'], tip: '流程支援串簽、會簽與條件分支，可依據金額或層級設定不同審核路徑。' },
        ],
      },
      {
        title: '通知範本與公告',
        summary: '管理系統通知範本與發布全公司公告。',
        steps: [
          { description: '進入「通知管理」→「通知範本」，建立或修改各類通知的內容範本', screenshotKeys: ['HR12-P03-Templates'] },
          { description: '進入「通知管理」→「公告管理」，發布、編輯或下架公司公告', screenshotKeys: ['HR12-P04-Announcements'], tip: '公告可設定生效與到期日，到期後自動下架。' },
        ],
      },
      {
        title: '文件管理與範本',
        summary: '管理系統文件庫與文件範本。',
        steps: [
          { description: '進入「文件管理」→「文件管理」，瀏覽、上傳或管理系統文件', screenshotKeys: ['HR13-P02-Management'] },
          { description: '進入「文件管理」→「文件範本」，建立或維護可供下載的文件範本（如離職申請書、在職證明等）', screenshotKeys: ['HR13-P03-Templates'] },
        ],
      },
      {
        title: '報表儀表板',
        summary: '查看系統整體的統計儀表板與財務報表。',
        steps: [
          { description: '進入「報表中心」→「報表儀表板」，查看關鍵人力指標與即時統計圖表', screenshotKeys: ['HR14-P01-Dashboard'] },
          { description: '進入「報表中心」→「財務報表」，查看薪資成本、保費支出等財務分析', screenshotKeys: ['HR14-P04-FinanceReport'] },
        ],
      },
    ],
  },
];

// ===== HTML 內容生成 =====

/** 讀取截圖並轉為 base64，若不存在回傳 null */
async function readScreenshotBase64(screenshotDir: string, filename: string): Promise<string | null> {
  const screenshotPath = path.join(screenshotDir, `${filename}.png`);
  try {
    await fs.access(screenshotPath);
    const imgBuffer = await fs.readFile(screenshotPath);
    return imgBuffer.toString('base64');
  } catch {
    return null;
  }
}

/** 生成操作手冊 HTML 內容（角色導向流程） */
async function generateManualContent(screenshotDir: string): Promise<string> {
  let html = '';

  // ===== 第 1 章：系統概述 =====
  html += `<h1>1. 系統概述</h1>
    <p>${DOC_META.company}是一套企業級人力資源暨專案管理系統，涵蓋 14 個功能模組，
    包含員工管理、考勤、薪資、保險、專案、工時、績效、招募、訓練、簽核、通知、文件及報表等完整功能。</p>

    <h2>1.1 系統帳號</h2>
    <table style="table-layout:fixed; width:100%">
      <thead><tr>
        <th style="width:20%">帳號</th>
        <th style="width:20%">角色</th>
        <th style="width:25%">密碼</th>
        <th style="width:35%">說明</th>
      </tr></thead>
      <tbody>
        <tr><td>admin</td><td>ADMIN</td><td>Admin@123</td><td>系統管理員，擁有所有功能的存取權限</td></tr>
        <tr><td>hr_admin</td><td>HR</td><td>Admin@123</td><td>人資管理員，負責員工管理、薪資、保險等人事作業</td></tr>
        <tr><td>employee</td><td>EMPLOYEE</td><td>Admin@123</td><td>一般員工，可打卡、請假、查詢個人資訊</td></tr>
        <tr><td>manager</td><td>MANAGER</td><td>Admin@123</td><td>部門主管，負責差勤審核與團隊績效考核</td></tr>
        <tr><td>pm</td><td>PM</td><td>Admin@123</td><td>專案經理，負責專案管理與工時審核</td></tr>
      </tbody>
    </table>

    <h2>1.2 登入方式</h2>
    <ol>
      <li>開啟瀏覽器，輸入系統網址</li>
      <li>在登入頁面輸入<strong>帳號</strong>與<strong>密碼</strong></li>
      <li>點擊「<strong>登入</strong>」按鈕</li>
      <li>成功後自動導向首頁儀表板</li>
    </ol>`;

  // 嵌入登入頁截圖
  const loginBase64 = await readScreenshotBase64(screenshotDir, 'HR01-P01-Login');
  if (loginBase64) {
    html += `<div class="screenshot-container">
      <img src="data:image/png;base64,${loginBase64}" alt="登入頁面" />
      <p class="screenshot-caption">圖 1.1 — 登入頁面</p>
    </div>`;
  }

  // ===== 第 2～6 章：角色導向流程 =====
  let chapterNum = 2;
  for (const chapter of ROLE_CHAPTERS) {
    html += `<h1>${chapterNum}. ${chapter.title}</h1>`;
    html += `<p><strong>適用角色：</strong>${chapter.role}</p>`;
    html += `<p>${chapter.description}</p>`;

    let flowNum = 1;
    for (const flow of chapter.flows) {
      html += `<h2>${chapterNum}.${flowNum} ${flow.title}</h2>`;
      html += `<p>${flow.summary}</p>`;

      for (let i = 0; i < flow.steps.length; i++) {
        const step = flow.steps[i];
        html += `<div class="flow-step">`;
        html += `<div class="flow-step-header">步驟 ${i + 1}</div>`;
        html += `<p>${step.description}</p>`;

        // 嵌入截圖
        for (const key of step.screenshotKeys) {
          const base64 = await readScreenshotBase64(screenshotDir, key);
          if (base64) {
            const capture = ROUTE_CAPTURES.find(r => r.filename === key);
            const caption = capture?.description || key;
            html += `<div class="screenshot-container">
              <img src="data:image/png;base64,${base64}" alt="${caption}" />
              <p class="screenshot-caption">${caption}</p>
            </div>`;
          }
        }

        // 注意事項
        if (step.tip) {
          html += `<div class="flow-tip">${step.tip}</div>`;
        }

        html += `</div>`;
      }

      flowNum++;
    }

    chapterNum++;
  }

  return html;
}

/** E5 主入口：生成操作手冊 */
export async function generateE5(): Promise<void> {
  console.log('\n--- E5: 系統操作手冊 ---\n');

  // 步驟一：截圖（檢查既有截圖或透過 Puppeteer 擷取）
  let screenshotsAvailable = false;

  // 檢查是否已有截圖（由 Playwright MCP 或其他工具生成）
  try {
    const existingFiles = await fs.readdir(SCREENSHOT_DIR);
    const pngCount = existingFiles.filter(f => f.endsWith('.png')).length;
    if (pngCount > 0) {
      console.log(`[E5] 發現 ${pngCount} 張既有截圖，跳過截圖步驟`);
      screenshotsAvailable = true;
    }
  } catch { /* 目錄不存在 */ }

  // 無既有截圖時嘗試 Puppeteer 擷取
  if (!screenshotsAvailable) {
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
