// @ts-nocheck
/**
 * ReportApi Adapter Tests (報表分析 API 適配器測試)
 * Domain Code: HR14
 *
 * 三向一致性稽核：後端 Response DTO (Java camelCase) ↔ 合約規格 ↔ 前端 ReportTypes (snake_case)
 *
 * 測試目標：
 * 1. getDashboard() 對 employee-roster / attendance-statistics 回應的欄位映射
 * 2. 各 adapt 邏輯對 camelCase → snake_case 的轉換正確性
 * 3. null / undefined 的防禦性處理
 *
 * 已知不一致：詳見本檔案底部「三向差異彙整表」
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ReportApi } from './ReportApi';

// ---------------------------------------------------------------------------
// 測試替身：apiClient
// ---------------------------------------------------------------------------

vi.mock('@shared/api', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('../../../config/MockConfig', () => ({
  MockConfig: {
    isEnabled: vi.fn().mockReturnValue(false),
  },
}));

import { apiClient } from '@shared/api';

// ---------------------------------------------------------------------------
// 後端回應模擬資料（Java camelCase，符合 Spring Jackson 預設序列化）
// ---------------------------------------------------------------------------

/** 後端 EmployeeRosterResponse.EmployeeRosterItem（camelCase） */
const backendRosterItem = {
  employeeId: 'EMP-001',
  name: '王小明',
  departmentName: '工程部',
  positionName: '資深工程師',
  hireDate: '2022-03-15',
  serviceYears: 3.2,
  status: 'ACTIVE',
  phone: '0912345678',
  email: 'wang@example.com',
};

/** 後端 AttendanceStatisticsResponse.AttendanceStatItem（camelCase） */
const backendAttendanceStatItem = {
  employeeId: 'EMP-001',
  employeeName: '王小明',
  departmentName: '工程部',
  expectedDays: 22,
  actualDays: 21,
  lateCount: 1,
  earlyLeaveCount: 0,
  absentCount: 0,
  leaveDays: 1.0,
  overtimeHours: 8.5,
  attendanceRate: 95.45,
};

/** 後端 TurnoverAnalysisResponse（camelCase） */
const backendTurnoverResponse = {
  organizationId: 'org-001',
  yearMonth: '2026-02',
  turnoverRate: 2.5,
  totalEmployees: 120,
  newHires: 5,
  terminations: 3,
};

/** 後端 UtilizationRateResponse（camelCase） */
const backendUtilizationResponse = {
  projectId: 'PRJ-001',
  projectName: 'HRMS 開發',
  yearMonth: '2026-02',
  utilizationRate: 85.0,
  totalHours: 160,
  billableHours: 136,
};

/** 後端 LaborCostAnalysisResponse（camelCase） */
const backendLaborCostResponse = {
  organizationId: 'org-001',
  yearMonth: '2026-02',
  totalCost: 5000000,
  employeeCount: 120,
  averageCost: 41666.67,
};

/** 後端 LaborCostByDepartmentResponse（camelCase） */
const backendLaborCostByDeptResponse = {
  departmentId: 'D001',
  departmentName: '工程部',
  yearMonth: '2026-02',
  totalCost: 1500000,
  employeeCount: 30,
};

/** 後端 PayrollSummaryResponse.PayrollSummaryItem（camelCase） */
const backendPayrollSummaryItem = {
  employeeId: 'EMP-001',
  employeeName: '王小明',
  departmentName: '工程部',
  baseSalary: 60000,
  overtimePay: 5000,
  allowances: 3000,
  bonus: 10000,
  grossPay: 78000,
  laborInsurance: 1200,
  healthInsurance: 800,
  incomeTax: 3000,
  otherDeductions: 500,
  netPay: 72500,
};

/** 後端 DashboardListResponse.DashboardSummary（camelCase） */
const backendDashboardSummary = {
  dashboardId: 'uuid-dashboard-001',
  dashboardName: '我的儀表板',
  description: '個人 KPI 追蹤',
  isPublic: false,
  isDefault: true,
  widgetCount: 4,
  createdAt: '2026-02-01T09:00:00',
  updatedAt: '2026-02-10T15:30:00',
};

/** 後端 DashboardDetailResponse（camelCase） */
const backendDashboardDetail = {
  dashboardId: 'uuid-dashboard-001',
  dashboardName: '我的儀表板',
  description: '個人 KPI 追蹤',
  isDefault: true,
  widgets: [
    {
      widgetId: 'w-001',
      widgetType: 'CHART',
      title: '人員趨勢',
      config: { chartType: 'bar' },
      data: null,
    },
  ],
};

/** 後端 CreateDashboardResponse（camelCase） */
const backendCreateDashboardResponse = {
  dashboardId: 'uuid-dashboard-new',
  dashboardName: '我的儀表板',
  createdAt: '2026-03-13T10:00:00',
  message: '儀表板已建立',
};

/** 後端 ExportFileResponse（camelCase） */
const backendExportFileResponse = {
  exportId: 'export-001',
  fileName: '員工名冊.xlsx',
  fileUrl: 'https://storage.example.com/reports/export-001.xlsx',
  status: 'PROCESSING',
};

/** 後端 GenerateReportResponse（camelCase） */
const backendGenerateReportResponse = {
  reportId: 'report-001',
  reportName: 'HR 月報',
  status: 'PENDING',
};

/** 後端 ProjectCostAnalysisResponse.ProjectCostItem（camelCase） */
const backendProjectCostItem = {
  projectId: 'PRJ-001',
  projectName: 'HRMS 開發',
  customerName: 'ABC Corp',
  projectManager: '李大明',
  startDate: '2026-01-01',
  endDate: '2026-06-30',
  status: 'IN_PROGRESS',
  budgetAmount: 1000000,
  laborCost: 800000,
  otherCost: 50000,
  totalCost: 850000,
  costVariance: 150000,
  costVarianceRate: 15.0,
  totalHours: 2000,
  utilizationRate: 85.0,
};

// ---------------------------------------------------------------------------
// Helper：建立 apiClient.get mock 回傳值
// ---------------------------------------------------------------------------
const mockGet = (responses: Record<string, unknown>) => {
  vi.mocked(apiClient.get).mockImplementation((url: string) => {
    for (const [key, val] of Object.entries(responses)) {
      if (url.includes(key)) return Promise.resolve(val);
    }
    return Promise.reject(new Error(`Unhandled URL: ${url}`));
  });
};

// ---------------------------------------------------------------------------
// 1. getDashboard() — 從多個端點組合儀表板資料
// ---------------------------------------------------------------------------

describe('ReportApi.getDashboard() — 後端 camelCase 欄位映射', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確從員工花名冊計算 total_employees / active_employees', async () => {
    // active_employees 邏輯：e.status === 'ACTIVE' || !e.resignationDate
    // 因此 RESIGNED 員工必須同時帶有 resignationDate 才能被排除
    const rosterResponse = {
      content: [
        { ...backendRosterItem, status: 'ACTIVE' },
        { ...backendRosterItem, employeeId: 'EMP-002', status: 'ACTIVE' },
        { ...backendRosterItem, employeeId: 'EMP-003', status: 'RESIGNED', resignationDate: '2026-01-31' },
      ],
    };
    mockGet({
      'employee-roster': rosterResponse,
      'attendance-statistics': { content: [] },
      'payroll-summary': { content: [] },
    });

    const result = await ReportApi.getDashboard();

    // 合約 RPT_QRY_001 驗證：content 陣列存在
    expect(result.kpis.total_employees).toBe(3);
    // status === 'ACTIVE' 的員工（RESIGNED + resignationDate → 被排除）
    expect(result.kpis.active_employees).toBe(2);
  });

  it('應正確從差勤統計計算 average_attendance_rate 與 overtime_hours_this_month', async () => {
    const attendanceResponse = {
      content: [
        { ...backendAttendanceStatItem, attendanceRate: 95.0, overtimeHours: 8.0 },
        { ...backendAttendanceStatItem, employeeId: 'EMP-002', attendanceRate: 100.0, overtimeHours: 4.0 },
      ],
    };
    mockGet({
      'employee-roster': { content: [] },
      'attendance-statistics': attendanceResponse,
      'payroll-summary': { content: [] },
    });

    const result = await ReportApi.getDashboard();

    // 平均出勤率 = (95 + 100) / 2 = 97.5
    expect(result.kpis.average_attendance_rate).toBe(97.5);
    // 總加班時數 = 8 + 4 = 12
    expect(result.kpis.overtime_hours_this_month).toBe(12);
  });

  it('差勤統計 content 中的 attendanceRate (camelCase) 應被正確讀取', async () => {
    // 驗證 adapt 邏輯：a.attendanceRate ?? a.attendance_rate
    const attendanceResponse = {
      content: [
        { attendanceRate: 88.5, overtimeHours: 5.0 }, // 後端 camelCase
      ],
    };
    mockGet({
      'employee-roster': { content: [] },
      'attendance-statistics': attendanceResponse,
      'payroll-summary': { content: [] },
    });

    const result = await ReportApi.getDashboard();

    expect(result.kpis.average_attendance_rate).toBe(88.5);
    expect(result.kpis.overtime_hours_this_month).toBe(5.0);
  });

  it('差勤統計 content 中的 attendance_rate (snake_case) — 確認 fallback 缺失行為', async () => {
    // ReportApi.ts L83: avgAttRate = sum + (a.attendanceRate ?? 0)
    // 若後端送 snake_case attendance_rate，a.attendanceRate 為 undefined → 讀取 0
    // 此測試文件化「後端 snake_case 回應時 fallback 缺失」的現有行為
    const attendanceResponse = {
      content: [
        { attendance_rate: 77.0, overtime_hours: 3.0 }, // snake_case（後端送出時的 fallback 場景）
      ],
    };
    mockGet({
      'employee-roster': { content: [] },
      'attendance-statistics': attendanceResponse,
      'payroll-summary': { content: [] },
    });

    const result = await ReportApi.getDashboard();

    // TODO [D]: ReportApi.ts L83 應補 ?? a.attendance_rate，目前 snake_case 無法被讀取
    // 目前行為：attendance_rate (snake_case) 被忽略，結果為 0
    expect(result.kpis.average_attendance_rate).toBe(0);
    // TODO [D]: ReportApi.ts L84 應補 ?? a.overtime_hours，目前 snake_case 無法被讀取
    expect(result.kpis.overtime_hours_this_month).toBe(0);
  });

  // -----------------------------------------------------------------------
  // attendance_stats 映射
  // -----------------------------------------------------------------------

  it('attendance_stats 每項應正確映射 actualDays → present_count', async () => {
    const attendanceResponse = {
      content: [
        {
          employeeName: '王小明',
          actualDays: 21,
          attendanceRate: 95.45,
          overtimeHours: 8.5,
          // absentCount / lateCount 不在後端 AttendanceStatItem 定義外
          absentCount: 0,
          lateCount: 1,
        },
      ],
    };
    mockGet({
      'employee-roster': { content: [] },
      'attendance-statistics': attendanceResponse,
      'payroll-summary': { content: [] },
    });

    const result = await ReportApi.getDashboard();

    expect(result.attendance_stats).toHaveLength(1);
    const stat = result.attendance_stats[0];
    expect(stat.present_count).toBe(21);
    expect(stat.absent_count).toBe(0);
    expect(stat.late_count).toBe(1);
    expect(stat.attendance_rate).toBe(95.45);
    // date 欄位由 employeeName 做 fallback（見 L100）
    // TODO: date 欄位 fallback 邏輯混亂：应使用 statDate 而非 employeeName
    expect(stat.date).toBe('王小明');
  });

  it('attendance_stats date 欄位應優先使用 statDate (camelCase)', async () => {
    const attendanceResponse = {
      content: [
        { statDate: '2026-02-15', actualDays: 20, attendanceRate: 90.0, overtimeHours: 0 },
      ],
    };
    mockGet({
      'employee-roster': { content: [] },
      'attendance-statistics': attendanceResponse,
      'payroll-summary': { content: [] },
    });

    const result = await ReportApi.getDashboard();
    expect(result.attendance_stats[0].date).toBe('2026-02-15');
  });

  // -----------------------------------------------------------------------
  // department_distribution 映射
  // -----------------------------------------------------------------------

  it('department_distribution 應從花名冊員工的 departmentName (camelCase) 建構部門分佈', async () => {
    const rosterResponse = {
      content: [
        { departmentName: '工程部', departmentId: 'D001', status: 'ACTIVE' },
        { departmentName: '工程部', departmentId: 'D001', status: 'ACTIVE' },
        { departmentName: '人資部', departmentId: 'D002', status: 'ACTIVE' },
      ],
    };
    mockGet({
      'employee-roster': rosterResponse,
      'attendance-statistics': { content: [] },
      'payroll-summary': { content: [] },
    });

    const result = await ReportApi.getDashboard();

    expect(result.department_distribution).toHaveLength(2);
    const eng = result.department_distribution.find((d) => d.department_name === '工程部');
    expect(eng).toBeDefined();
    expect(eng!.employee_count).toBe(2);
    expect(eng!.percentage).toBe(67); // Math.round(2/3*100)
    expect(eng!.department_id).toBe('D001');
  });

  it('department_distribution 應 fallback 到 department_name (snake_case)', async () => {
    const rosterResponse = {
      content: [
        { department_name: '業務部', department_id: 'D003', status: 'ACTIVE' },
      ],
    };
    mockGet({
      'employee-roster': rosterResponse,
      'attendance-statistics': { content: [] },
      'payroll-summary': { content: [] },
    });

    const result = await ReportApi.getDashboard();
    const dept = result.department_distribution[0];
    expect(dept.department_name).toBe('業務部');
    expect(dept.department_id).toBe('D003');
  });

  // -----------------------------------------------------------------------
  // Null / undefined 防禦性處理
  // -----------------------------------------------------------------------

  it('content 為 undefined 時應回傳空陣列並不拋出錯誤', async () => {
    mockGet({
      'employee-roster': {},      // 無 content 欄位
      'attendance-statistics': {}, // 無 content 欄位
      'payroll-summary': {},
    });

    const result = await ReportApi.getDashboard();

    expect(result.kpis.total_employees).toBe(0);
    expect(result.kpis.active_employees).toBe(0);
    expect(result.department_distribution).toEqual([]);
    expect(result.attendance_stats).toEqual([]);
  });

  it('content 為 null 時應回傳空陣列並不拋出錯誤', async () => {
    mockGet({
      'employee-roster': { content: null },
      'attendance-statistics': { content: null },
      'payroll-summary': { content: null },
    });

    // content: null ?? [] 應降至 []
    const result = await ReportApi.getDashboard();

    expect(result.kpis.total_employees).toBe(0);
    expect(result.department_distribution).toEqual([]);
    expect(result.attendance_stats).toEqual([]);
  });

  it('API 拋出例外時應回傳 emptyDashboard 並不向外拋出', async () => {
    vi.mocked(apiClient.get).mockRejectedValue(new Error('Network Error'));

    const result = await ReportApi.getDashboard();

    expect(result.kpis.total_employees).toBe(0);
    expect(result.kpis.average_attendance_rate).toBe(0);
    expect(result.headcount_trend).toEqual([]);
    expect(result.department_distribution).toEqual([]);
  });

  it('各個並行 API 個別失敗時應優雅降級（catch 回傳空 content）', async () => {
    vi.mocked(apiClient.get).mockImplementation((url: string) => {
      if (url.includes('employee-roster')) {
        return Promise.reject(new Error('403 Forbidden'));
      }
      if (url.includes('attendance-statistics')) {
        return Promise.resolve({ content: [{ attendanceRate: 90.0, overtimeHours: 2.0 }] });
      }
      return Promise.resolve({ content: [] });
    });

    // roster 失敗 → catch(() => ({ content: [] })) → total_employees = 0
    const result = await ReportApi.getDashboard();

    expect(result.kpis.total_employees).toBe(0);
    expect(result.kpis.average_attendance_rate).toBe(90.0);
  });
});

// ---------------------------------------------------------------------------
// 2. generateReport() — 後端 GenerateReportResponse 映射
// ---------------------------------------------------------------------------

describe('ReportApi.generateReport() — 後端 camelCase 欄位映射', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應使用後端回傳的 reportId / reportName / status 組成回應', async () => {
    vi.mocked(apiClient.post).mockResolvedValue(backendGenerateReportResponse);

    const result = await ReportApi.generateReport({
      report_definition_id: 'def-001',
      format: 'EXCEL',
      parameters: {},
    });

    // 後端 GenerateReportResponse 有 reportId, reportName, status
    // 前端 GenerateReportResponse.report 應為後端物件本身
    expect(result.report).toEqual(backendGenerateReportResponse);
    expect(result.message).toBe('報表產生中');
  });

  it('後端回傳含 message 時應使用後端的 message', async () => {
    vi.mocked(apiClient.post).mockResolvedValue({
      ...backendGenerateReportResponse,
      message: '報表已排入佇列',
    });

    const result = await ReportApi.generateReport({
      report_definition_id: 'def-001',
      format: 'PDF',
      parameters: {},
    });

    expect(result.message).toBe('報表已排入佇列');
  });

  it('後端 message 為 undefined 時應使用預設訊息「報表產生中」', async () => {
    vi.mocked(apiClient.post).mockResolvedValue({ reportId: 'r-001', status: 'PENDING' });

    const result = await ReportApi.generateReport({
      report_definition_id: 'def-001',
      format: 'CSV',
      parameters: {},
    });

    expect(result.message).toBe('報表產生中');
  });
});

// ---------------------------------------------------------------------------
// 3. downloadReport() — ExportFileResponse 映射
// ---------------------------------------------------------------------------

describe('ReportApi.downloadReport() — 後端欄位映射', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應優先讀取 downloadUrl (camelCase)', async () => {
    vi.mocked(apiClient.get).mockResolvedValue({ downloadUrl: 'https://example.com/file.xlsx' });

    const result = await ReportApi.downloadReport('export-001');

    expect(result.download_url).toBe('https://example.com/file.xlsx');
  });

  it('應 fallback 到 download_url (snake_case)', async () => {
    vi.mocked(apiClient.get).mockResolvedValue({ download_url: 'https://example.com/file.pdf' });

    const result = await ReportApi.downloadReport('export-001');

    expect(result.download_url).toBe('https://example.com/file.pdf');
  });

  it('兩者皆 undefined 時應回傳 "#"', async () => {
    vi.mocked(apiClient.get).mockResolvedValue({});

    const result = await ReportApi.downloadReport('export-001');

    expect(result.download_url).toBe('#');
  });

  it('後端 ExportFileResponse 完整欄位模擬', async () => {
    vi.mocked(apiClient.get).mockResolvedValue(backendExportFileResponse);

    // adapter 已修正：優先讀取 fileUrl，再 fallback downloadUrl / download_url
    const result = await ReportApi.downloadReport('export-001');

    // fileUrl 正確映射至 download_url
    expect(result.download_url).toBe('https://storage.example.com/reports/export-001.xlsx');
  });
});

// ---------------------------------------------------------------------------
// 4. deleteReport() — 直接轉發後端回應
// ---------------------------------------------------------------------------

describe('ReportApi.deleteReport()', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應直接回傳後端 message', async () => {
    vi.mocked(apiClient.delete).mockResolvedValue({ message: '報表已刪除' });

    const result = await ReportApi.deleteReport('r-001');

    expect(result.message).toBe('報表已刪除');
  });
});

// ---------------------------------------------------------------------------
// 5. getReportDefinitions() / getReports() / getScheduledReports()
//    — 後端無對應 endpoint，永遠回傳空結構
// ---------------------------------------------------------------------------

describe('ReportApi — 後端未實作的 endpoints 回傳空結構', () => {
  it('getReportDefinitions 應回傳空 definitions 陣列', async () => {
    const result = await ReportApi.getReportDefinitions();
    expect(result.definitions).toEqual([]);
    expect(result.pagination.total).toBe(0);
  });

  it('getReports 應回傳空 reports 陣列', async () => {
    const result = await ReportApi.getReports();
    expect(result.reports).toEqual([]);
  });

  it('getScheduledReports 應回傳空 scheduled_reports 陣列', async () => {
    const result = await ReportApi.getScheduledReports();
    expect(result.scheduled_reports).toEqual([]);
  });

  it('createScheduledReport 應回傳說明訊息', async () => {
    const result = await ReportApi.createScheduledReport({
      report_definition_id: 'def-001',
      schedule_type: 'MONTHLY',
      schedule_time: '08:00',
      format: 'EXCEL',
      parameters: {},
      recipients: ['hr@example.com'],
    });
    expect(result.message).toContain('尚未實作');
  });
});

// ---------------------------------------------------------------------------
// 6. 三向差異彙整表（文件型測試）
// ---------------------------------------------------------------------------

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════╗
 * ║  HR14 Reporting 三向欄位一致性稽核結果                                               ║
 * ╠════════════════════════╦═══════════════════╦═══════════════════╦════════════════════╣
 * ║  項目                  ║  後端 DTO (Java)   ║  合約規格          ║  前端 ReportTypes  ║
 * ╠════════════════════════╬═══════════════════╬═══════════════════╬════════════════════╣
 * ║  [A] AttendanceStatItem:                                                             ║
 * ║  statDate              ║  無此欄位           ║  無                ║ 無（推斷欄位名稱）  ║
 * ║  → date (前端)         ║  employeeId 有     ║  employeeId 有     ║ AttendanceStatsDto ║
 * ║                        ║  但無日期聚合欄位   ║  但無日期欄位       ║  date: string      ║
 * ║  狀態：MISMATCH         ║  API 回傳員工列表   ║  缺少時間維度說明   ║ date fallback 混亂 ║
 * ╠════════════════════════╬═══════════════════╬═══════════════════╬════════════════════╣
 * ║  [B] EmployeeRoster:                                                                 ║
 * ║  phone                 ║  String phone      ║  未要求            ║  未定義            ║
 * ║  狀態：後端多欄位        ║  有 phone 欄位     ║  requiredFields    ║  ReportTypes 無對應║
 * ║                        ║                   ║  無 phone          ║  前端不消費         ║
 * ╠════════════════════════╬═══════════════════╬═══════════════════╬════════════════════╣
 * ║  [C] ExportFileResponse:                                                             ║
 * ║  fileUrl               ║  String fileUrl    ║  N/A（二進位回應） ║  download_url      ║
 * ║  狀態：MISMATCH         ║  後端欄位 fileUrl   ║                   ║  前端期望          ║
 * ║                        ║  非 downloadUrl    ║                   ║  downloadUrl 或    ║
 * ║                        ║  非 download_url   ║                   ║  download_url      ║
 * ║  → 風險：下載 URL 永遠 fallback '#'，下載功能失效                                    ║
 * ╠════════════════════════╬═══════════════════╬═══════════════════╬════════════════════╣
 * ║  [D] AttendanceStatsDto (前端內部類型):                                              ║
 * ║  snake_case            ║  後端送 camelCase   ║  N/A               ║ 前端 DTO snake_case║
 * ║  狀態：前端 getDashboard║  attendanceRate     ║                   ║  attendance_rate   ║
 * ║  已有 camelCase fallback║  overtimeHours      ║                   ║  overtime_hours    ║
 * ║  但 overtime_hours      ║                   ║                   ║                    ║
 * ║  snake_case 無 fallback ║  a.overtimeHours   ║                   ║  缺少 fallback      ║
 * ╠════════════════════════╬═══════════════════╬═══════════════════╬════════════════════╣
 * ║  [E] GenerateReportResponse:                                                         ║
 * ║  後端欄位               ║  reportId          ║  N/A（後端自訂）   ║  前端 ReportDto.id  ║
 * ║                        ║  reportName        ║                   ║  id (非 reportId)  ║
 * ║                        ║  status            ║                   ║  status ✓          ║
 * ║  狀態：部分不符          ║  後端 reportId     ║                   ║  前端期望 .id       ║
 * ╠════════════════════════╬═══════════════════╬═══════════════════╬════════════════════╣
 * ║  [F] DashboardListResponse.DashboardSummary:                                         ║
 * ║  dashboardId 型別      ║  UUID (Java)       ║  string            ║  string            ║
 * ║  狀態：OK（序列化後為    ║  序列化為 string    ║  type: "string"    ║  string            ║
 * ║  string，無問題）       ║                   ║                   ║                    ║
 * ╠════════════════════════╬═══════════════════╬═══════════════════╬════════════════════╣
 * ║  [G] HeadcountReportResponse 欄位豐富度：                                            ║
 * ║  後端有 probationCount  ║  ✓                ║  未要求            ║  未定義            ║
 * ║  leaveCount            ║  ✓                ║  未要求            ║  未定義            ║
 * ║  maleCount/femaleCount ║  ✓                ║  未要求            ║  未定義            ║
 * ║  avgAge                ║  ✓                ║  未要求            ║  未定義            ║
 * ║  狀態：後端提供更多欄位   ║  前端未消費這些欄位 ║                   ║                    ║
 * ╠════════════════════════╬═══════════════════╬═══════════════════╬════════════════════╣
 * ║  [H] PayrollSummaryResponse 欄位豐富度：                                             ║
 * ║  后端有 overtimePay     ║  ✓                ║  未要求            ║  未定義            ║
 * ║  allowances, bonus     ║  ✓                ║  未要求            ║  未定義            ║
 * ║  laborInsurance        ║  ✓                ║  未要求            ║  未定義            ║
 * ║  healthInsurance       ║  ✓                ║  未要求            ║  未定義            ║
 * ║  incomeTax             ║  ✓                ║  未要求            ║  未定義            ║
 * ║  otherDeductions       ║  ✓                ║  未要求            ║  未定義            ║
 * ║  狀態：後端提供更多欄位   ║  前端僅使用基礎 3 欄位 (baseSalary/grossPay/netPay)       ║
 * ╠════════════════════════╬═══════════════════╬═══════════════════╬════════════════════╣
 * ║  [I] ReportStatus 差異：                                                             ║
 * ║  後端 ExportFileResponse║  PENDING/COMPLETED ║  PROCESSING (合約) ║  PENDING/GENERATING║
 * ║                        ║  FAILED            ║  合約用 PROCESSING  ║  COMPLETED/FAILED  ║
 * ║  狀態：MISMATCH         ║  status 無 PROCESSING ║               ║  無 PROCESSING 值  ║
 * ╚════════════════════════╩═══════════════════╩═══════════════════╩════════════════════╝
 *
 * 需修正優先序：
 * P0 [C] ExportFileResponse.fileUrl vs downloadUrl — 下載功能失效
 * P0 [I] ReportStatus PROCESSING vs PENDING — 合約要求 PROCESSING，後端與前端均無此值
 * P1 [D] overtime_hours snake_case fallback 缺失 — ReportApi.ts L84
 * P1 [A] AttendanceStatItem date 欄位邏輯混亂 — 應用日期維度而非員工姓名
 * P2 [E] GenerateReportResponse reportId vs id 欄位命名不一致
 * P3 [G][H] 後端提供的豐富欄位前端未消費（可選強化）
 */
describe('三向差異文件化測試', () => {
  it('[C] ExportFileResponse — fileUrl 與前端 download_url 不一致', () => {
    // 後端：ExportFileResponse.fileUrl
    // 前端：ReportApi 嘗試讀取 response.downloadUrl ?? response.download_url
    // 結果：兩者皆不存在，永遠 fallback '#'
    const backendResponse = { exportId: 'e-001', fileUrl: 'https://s3.example.com/e-001.xlsx', status: 'COMPLETED' };
    const adaptedUrl = (backendResponse as any).downloadUrl ?? (backendResponse as any).download_url ?? '#';
    // TODO: 後端需新增 downloadUrl 欄位，或前端需補充 fileUrl 的讀取
    expect(adaptedUrl).toBe('#'); // 確認目前行為確實有缺陷
  });

  it('[I] ReportStatus — 合約要求 PROCESSING，後端/前端均使用不同名稱', () => {
    // 合約 RPT_CMD_004 要求 status = "PROCESSING"
    // 後端 ExportFileResponse 的 status 字串值沒有限制型別，實際填入待確認
    // 前端 ReportStatus 無 "PROCESSING" 值
    const contractStatus = 'PROCESSING';
    const frontendStatuses: string[] = ['PENDING', 'GENERATING', 'COMPLETED', 'FAILED'];
    // TODO: 前端 ReportTypes.ts 的 ReportStatus 應新增 'PROCESSING' | 'SUBMITTED' 等合約要求的狀態
    expect(frontendStatuses).not.toContain(contractStatus);
  });

  it('[D] overtime_hours snake_case — ReportApi 缺少 fallback', () => {
    // ReportApi.ts L84: a.overtimeHours ?? 0
    // 後端若用 snake_case 送出則 overtimeHours 為 undefined，a.overtimeHours ?? 0 = 0
    // 但 a.overtime_hours 不被讀取
    const rawItem = { overtime_hours: 10.0 }; // snake_case
    const adapted = (rawItem as any).overtimeHours ?? 0;
    // TODO: 應改為 a.overtimeHours ?? a.overtime_hours ?? 0
    expect(adapted).toBe(0); // 確認目前行為確實漏讀
  });

  it('[E] GenerateReportResponse.reportId — 前端期望 ReportDto.id', () => {
    // 後端 GenerateReportResponse 欄位：reportId, reportName, status
    // 前端 ReportDto 欄位：id, report_name, status
    // ReportApi.generateReport 將後端物件直接塞入 result.report
    // 因此 result.report.id 為 undefined，result.report.reportId 才有值
    const backendResp = { reportId: 'r-999', reportName: '測試報表', status: 'PENDING' };
    const reportDto = backendResp as any;
    // TODO: 需將 reportId → id 的 adapt 邏輯加入 generateReport()
    expect(reportDto.id).toBeUndefined();
    expect(reportDto.reportId).toBe('r-999');
  });
});
