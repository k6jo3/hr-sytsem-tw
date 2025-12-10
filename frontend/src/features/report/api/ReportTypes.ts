/**
 * Report Types (報表分析 API 類型定義)
 * Domain Code: HR14
 */

// ========== Enums ==========

/**
 * 報表類型
 */
export type ReportType =
  | 'EMPLOYEE_SUMMARY'
  | 'ATTENDANCE_SUMMARY'
  | 'PAYROLL_SUMMARY'
  | 'LEAVE_STATISTICS'
  | 'OVERTIME_STATISTICS'
  | 'TURNOVER_ANALYSIS'
  | 'HEADCOUNT_TREND'
  | 'PROJECT_COST'
  | 'RECRUITMENT_FUNNEL'
  | 'TRAINING_COMPLETION'
  | 'PERFORMANCE_DISTRIBUTION';

/**
 * 報表格式
 */
export type ReportFormat = 'PDF' | 'EXCEL' | 'CSV' | 'HTML';

/**
 * 報表狀態
 */
export type ReportStatus = 'PENDING' | 'GENERATING' | 'COMPLETED' | 'FAILED';

/**
 * 報表週期
 */
export type ReportPeriod = 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'QUARTERLY' | 'YEARLY' | 'CUSTOM';

/**
 * 圖表類型
 */
export type ChartType = 'LINE' | 'BAR' | 'PIE' | 'DONUT' | 'AREA' | 'SCATTER';

// ========== Dashboard DTOs ==========

/**
 * 儀表板 KPI DTO
 */
export interface DashboardKpiDto {
  total_employees: number;
  active_employees: number;
  new_hires_this_month: number;
  turnover_rate: number;
  average_attendance_rate: number;
  pending_leave_requests: number;
  overtime_hours_this_month: number;
  training_completion_rate: number;
}

/**
 * 人員趨勢數據
 */
export interface HeadcountTrendDataDto {
  month: string;
  headcount: number;
  new_hires: number;
  terminations: number;
}

/**
 * 部門分佈數據
 */
export interface DepartmentDistributionDto {
  department_id: string;
  department_name: string;
  employee_count: number;
  percentage: number;
}

/**
 * 出勤統計數據
 */
export interface AttendanceStatsDto {
  date: string;
  present_count: number;
  absent_count: number;
  late_count: number;
  leave_count: number;
  attendance_rate: number;
}

/**
 * 薪資分佈數據
 */
export interface SalaryDistributionDto {
  range: string;
  count: number;
  percentage: number;
}

// ========== Report DTOs ==========

/**
 * 報表定義 DTO
 */
export interface ReportDefinitionDto {
  id: string;
  report_code: string;
  report_name: string;
  report_type: ReportType;
  description: string;
  parameters: ReportParameterDto[];
  available_formats: ReportFormat[];
  is_scheduled: boolean;
  created_at: string;
  updated_at: string;
}

/**
 * 報表參數 DTO
 */
export interface ReportParameterDto {
  name: string;
  label: string;
  type: 'DATE' | 'DATE_RANGE' | 'SELECT' | 'MULTI_SELECT' | 'TEXT';
  required: boolean;
  default_value?: string;
  options?: { value: string; label: string }[];
}

/**
 * 報表產生記錄 DTO
 */
export interface ReportDto {
  id: string;
  report_definition_id: string;
  report_name: string;
  report_type: ReportType;
  format: ReportFormat;
  parameters: Record<string, string>;
  status: ReportStatus;
  file_path?: string;
  download_url?: string;
  generated_by: string;
  generated_by_name: string;
  generated_at?: string;
  expires_at?: string;
  error_message?: string;
  created_at: string;
  updated_at: string;
}

/**
 * 排程報表 DTO
 */
export interface ScheduledReportDto {
  id: string;
  report_definition_id: string;
  report_name: string;
  schedule_type: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  schedule_time: string;
  schedule_day?: number;
  format: ReportFormat;
  parameters: Record<string, string>;
  recipients: string[];
  is_active: boolean;
  last_run_at?: string;
  next_run_at: string;
  created_at: string;
  updated_at: string;
}

// ========== Request Types ==========

/**
 * 取得儀表板資料請求
 */
export interface GetDashboardRequest {
  period?: ReportPeriod;
  start_date?: string;
  end_date?: string;
}

/**
 * 取得報表定義列表請求
 */
export interface GetReportDefinitionsRequest {
  report_type?: ReportType;
  keyword?: string;
  page?: number;
  page_size?: number;
}

/**
 * 取得報表列表請求
 */
export interface GetReportsRequest {
  report_type?: ReportType;
  status?: ReportStatus;
  page?: number;
  page_size?: number;
}

/**
 * 產生報表請求
 */
export interface GenerateReportRequest {
  report_definition_id: string;
  format: ReportFormat;
  parameters: Record<string, string>;
}

/**
 * 建立排程報表請求
 */
export interface CreateScheduledReportRequest {
  report_definition_id: string;
  schedule_type: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  schedule_time: string;
  schedule_day?: number;
  format: ReportFormat;
  parameters: Record<string, string>;
  recipients: string[];
}

// ========== Response Types ==========

/**
 * 分頁資訊
 */
export interface PaginationInfo {
  page: number;
  page_size: number;
  total: number;
  total_pages: number;
}

/**
 * 取得儀表板資料回應
 */
export interface GetDashboardResponse {
  kpis: DashboardKpiDto;
  headcount_trend: HeadcountTrendDataDto[];
  department_distribution: DepartmentDistributionDto[];
  attendance_stats: AttendanceStatsDto[];
  salary_distribution: SalaryDistributionDto[];
}

/**
 * 取得報表定義列表回應
 */
export interface GetReportDefinitionsResponse {
  definitions: ReportDefinitionDto[];
  pagination: PaginationInfo;
}

/**
 * 取得報表列表回應
 */
export interface GetReportsResponse {
  reports: ReportDto[];
  pagination: PaginationInfo;
}

/**
 * 產生報表回應
 */
export interface GenerateReportResponse {
  report: ReportDto;
  message: string;
}

/**
 * 取得排程報表列表回應
 */
export interface GetScheduledReportsResponse {
  scheduled_reports: ScheduledReportDto[];
  pagination: PaginationInfo;
}
