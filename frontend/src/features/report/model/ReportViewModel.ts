/**
 * Report ViewModels (報表分析 前端視圖模型)
 * Domain Code: HR14
 */

import type { ReportType, ReportFormat, ReportStatus, ChartType } from '../api/ReportTypes';

// ========== Dashboard ViewModels ==========

/**
 * 儀表板 KPI ViewModel
 */
export interface DashboardKpiViewModel {
  totalEmployees: number;
  activeEmployees: number;
  newHiresThisMonth: number;
  turnoverRate: number;
  turnoverRateDisplay: string;
  averageAttendanceRate: number;
  attendanceRateDisplay: string;
  pendingLeaveRequests: number;
  overtimeHoursThisMonth: number;
  trainingCompletionRate: number;
  trainingCompletionDisplay: string;
}

/**
 * 人員趨勢圖表數據
 */
export interface HeadcountTrendViewModel {
  month: string;
  monthLabel: string;
  headcount: number;
  newHires: number;
  terminations: number;
  netChange: number;
}

/**
 * 部門分佈圖表數據
 */
export interface DepartmentDistributionViewModel {
  departmentId: string;
  departmentName: string;
  employeeCount: number;
  percentage: number;
  percentageDisplay: string;
}

/**
 * 出勤統計圖表數據
 */
export interface AttendanceStatsViewModel {
  date: string;
  dateLabel: string;
  presentCount: number;
  absentCount: number;
  lateCount: number;
  leaveCount: number;
  attendanceRate: number;
  attendanceRateDisplay: string;
}

/**
 * 薪資分佈圖表數據
 */
export interface SalaryDistributionViewModel {
  range: string;
  count: number;
  percentage: number;
  percentageDisplay: string;
}

/**
 * 儀表板完整 ViewModel
 */
export interface DashboardViewModel {
  kpis: DashboardKpiViewModel;
  headcountTrend: HeadcountTrendViewModel[];
  departmentDistribution: DepartmentDistributionViewModel[];
  attendanceStats: AttendanceStatsViewModel[];
  salaryDistribution: SalaryDistributionViewModel[];
}

// ========== Report ViewModels ==========

/**
 * 報表定義 ViewModel
 */
export interface ReportDefinitionViewModel {
  definitionId: string;
  reportCode: string;
  reportName: string;
  reportType: ReportType;
  reportTypeLabel: string;
  reportTypeIcon: string;
  description: string;
  parameters: ReportParameterViewModel[];
  availableFormats: ReportFormat[];
  availableFormatsDisplay: string;
  isScheduled: boolean;
}

/**
 * 報表參數 ViewModel
 */
export interface ReportParameterViewModel {
  name: string;
  label: string;
  type: 'DATE' | 'DATE_RANGE' | 'SELECT' | 'MULTI_SELECT' | 'TEXT';
  required: boolean;
  defaultValue?: string;
  options?: { value: string; label: string }[];
}

/**
 * 報表記錄 ViewModel
 */
export interface ReportViewModel {
  reportId: string;
  definitionId: string;
  reportName: string;
  reportType: ReportType;
  reportTypeLabel: string;
  format: ReportFormat;
  formatLabel: string;
  formatIcon: string;
  parameters: Record<string, string>;
  status: ReportStatus;
  statusLabel: string;
  statusColor: string;
  filePath?: string;
  downloadUrl?: string;
  generatedBy: string;
  generatedByName: string;
  generatedAt?: string;
  generatedAtDisplay?: string;
  expiresAt?: string;
  expiresAtDisplay?: string;
  errorMessage?: string;
  canDownload: boolean;
  isProcessing: boolean;
}

/**
 * 排程報表 ViewModel
 */
export interface ScheduledReportViewModel {
  scheduleId: string;
  definitionId: string;
  reportName: string;
  scheduleType: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  scheduleTypeLabel: string;
  scheduleTime: string;
  scheduleDay?: number;
  scheduleDayLabel?: string;
  format: ReportFormat;
  formatLabel: string;
  recipients: string[];
  recipientsDisplay: string;
  isActive: boolean;
  statusLabel: string;
  statusColor: string;
  lastRunAt?: string;
  lastRunAtDisplay?: string;
  nextRunAt: string;
  nextRunAtDisplay: string;
}

/**
 * 圖表配置 ViewModel
 */
export interface ChartConfigViewModel {
  type: ChartType;
  title: string;
  data: unknown[];
  xAxisKey?: string;
  yAxisKey?: string;
  colorPalette?: string[];
}
