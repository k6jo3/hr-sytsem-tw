/**
 * Report ViewModel Factory (報表分析視圖模型工廠)
 * Domain Code: HR14
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */

import dayjs from 'dayjs';
import type {
  DashboardKpiDto,
  HeadcountTrendDataDto,
  DepartmentDistributionDto,
  AttendanceStatsDto,
  SalaryDistributionDto,
  ReportDefinitionDto,
  ReportDto,
  ScheduledReportDto,
  ReportType,
  ReportFormat,
  ReportStatus,
  GetDashboardResponse,
} from '../api/ReportTypes';
import type {
  DashboardKpiViewModel,
  HeadcountTrendViewModel,
  DepartmentDistributionViewModel,
  AttendanceStatsViewModel,
  SalaryDistributionViewModel,
  DashboardViewModel,
  ReportDefinitionViewModel,
  ReportViewModel,
  ScheduledReportViewModel,
} from '../model/ReportViewModel';

// ========== Label Mappings ==========

const REPORT_TYPE_LABELS: Record<ReportType, string> = {
  EMPLOYEE_SUMMARY: '員工統計報表',
  ATTENDANCE_SUMMARY: '出勤統計報表',
  PAYROLL_SUMMARY: '薪資統計報表',
  LEAVE_STATISTICS: '請假統計報表',
  OVERTIME_STATISTICS: '加班統計報表',
  TURNOVER_ANALYSIS: '離職分析報表',
  HEADCOUNT_TREND: '人員趨勢報表',
  PROJECT_COST: '專案成本報表',
  RECRUITMENT_FUNNEL: '招募漏斗報表',
  TRAINING_COMPLETION: '訓練完成率報表',
  PERFORMANCE_DISTRIBUTION: '績效分佈報表',
};

const REPORT_TYPE_ICONS: Record<ReportType, string> = {
  EMPLOYEE_SUMMARY: 'TeamOutlined',
  ATTENDANCE_SUMMARY: 'ClockCircleOutlined',
  PAYROLL_SUMMARY: 'DollarOutlined',
  LEAVE_STATISTICS: 'CalendarOutlined',
  OVERTIME_STATISTICS: 'FieldTimeOutlined',
  TURNOVER_ANALYSIS: 'FallOutlined',
  HEADCOUNT_TREND: 'LineChartOutlined',
  PROJECT_COST: 'ProjectOutlined',
  RECRUITMENT_FUNNEL: 'FunnelPlotOutlined',
  TRAINING_COMPLETION: 'BookOutlined',
  PERFORMANCE_DISTRIBUTION: 'PieChartOutlined',
};

const REPORT_FORMAT_LABELS: Record<ReportFormat, string> = {
  PDF: 'PDF',
  EXCEL: 'Excel',
  CSV: 'CSV',
  HTML: 'HTML',
};

const REPORT_FORMAT_ICONS: Record<ReportFormat, string> = {
  PDF: 'FilePdfOutlined',
  EXCEL: 'FileExcelOutlined',
  CSV: 'FileTextOutlined',
  HTML: 'Html5Outlined',
};

const REPORT_STATUS_LABELS: Record<ReportStatus, string> = {
  PENDING: '待處理',
  GENERATING: '產生中',
  COMPLETED: '已完成',
  FAILED: '失敗',
};

const REPORT_STATUS_COLORS: Record<ReportStatus, string> = {
  PENDING: 'default',
  GENERATING: 'processing',
  COMPLETED: 'success',
  FAILED: 'error',
};

const SCHEDULE_TYPE_LABELS: Record<'DAILY' | 'WEEKLY' | 'MONTHLY', string> = {
  DAILY: '每日',
  WEEKLY: '每週',
  MONTHLY: '每月',
};

const WEEKDAY_LABELS = ['日', '一', '二', '三', '四', '五', '六'];

// ========== Helper Functions ==========

const formatDateTime = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm');
};

const formatDate = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD');
};

const formatMonth = (monthString: string): string => {
  return dayjs(monthString).format('YYYY年MM月');
};

const formatPercentage = (value: number): string => {
  return `${(value * 100).toFixed(1)}%`;
};

// ========== Factory Class ==========

export class ReportViewModelFactory {
  // ========== Dashboard Transformations ==========

  /**
   * 轉換 KPI 資料
   */
  static createKpiFromDTO(dto: DashboardKpiDto): DashboardKpiViewModel {
    return {
      totalEmployees: dto.total_employees,
      activeEmployees: dto.active_employees,
      newHiresThisMonth: dto.new_hires_this_month,
      turnoverRate: dto.turnover_rate,
      turnoverRateDisplay: formatPercentage(dto.turnover_rate),
      averageAttendanceRate: dto.average_attendance_rate,
      attendanceRateDisplay: formatPercentage(dto.average_attendance_rate),
      pendingLeaveRequests: dto.pending_leave_requests,
      overtimeHoursThisMonth: dto.overtime_hours_this_month,
      trainingCompletionRate: dto.training_completion_rate,
      trainingCompletionDisplay: formatPercentage(dto.training_completion_rate),
    };
  }

  /**
   * 轉換人員趨勢資料
   */
  static createHeadcountTrendFromDTO(dto: HeadcountTrendDataDto): HeadcountTrendViewModel {
    return {
      month: dto.month,
      monthLabel: formatMonth(dto.month),
      headcount: dto.headcount,
      newHires: dto.new_hires,
      terminations: dto.terminations,
      netChange: dto.new_hires - dto.terminations,
    };
  }

  static createHeadcountTrendListFromDTOs(
    dtos: HeadcountTrendDataDto[]
  ): HeadcountTrendViewModel[] {
    return dtos.map((dto) => this.createHeadcountTrendFromDTO(dto));
  }

  /**
   * 轉換部門分佈資料
   */
  static createDepartmentDistributionFromDTO(
    dto: DepartmentDistributionDto
  ): DepartmentDistributionViewModel {
    return {
      departmentId: dto.department_id,
      departmentName: dto.department_name,
      employeeCount: dto.employee_count,
      percentage: dto.percentage,
      percentageDisplay: formatPercentage(dto.percentage),
    };
  }

  static createDepartmentDistributionListFromDTOs(
    dtos: DepartmentDistributionDto[]
  ): DepartmentDistributionViewModel[] {
    return dtos.map((dto) => this.createDepartmentDistributionFromDTO(dto));
  }

  /**
   * 轉換出勤統計資料
   */
  static createAttendanceStatsFromDTO(dto: AttendanceStatsDto): AttendanceStatsViewModel {
    return {
      date: dto.date,
      dateLabel: formatDate(dto.date),
      presentCount: dto.present_count,
      absentCount: dto.absent_count,
      lateCount: dto.late_count,
      leaveCount: dto.leave_count,
      attendanceRate: dto.attendance_rate,
      attendanceRateDisplay: formatPercentage(dto.attendance_rate),
    };
  }

  static createAttendanceStatsListFromDTOs(dtos: AttendanceStatsDto[]): AttendanceStatsViewModel[] {
    return dtos.map((dto) => this.createAttendanceStatsFromDTO(dto));
  }

  /**
   * 轉換薪資分佈資料
   */
  static createSalaryDistributionFromDTO(dto: SalaryDistributionDto): SalaryDistributionViewModel {
    return {
      range: dto.range,
      count: dto.count,
      percentage: dto.percentage,
      percentageDisplay: formatPercentage(dto.percentage),
    };
  }

  static createSalaryDistributionListFromDTOs(
    dtos: SalaryDistributionDto[]
  ): SalaryDistributionViewModel[] {
    return dtos.map((dto) => this.createSalaryDistributionFromDTO(dto));
  }

  /**
   * 轉換完整儀表板資料
   */
  static createDashboardFromDTO(dto: GetDashboardResponse): DashboardViewModel {
    return {
      kpis: this.createKpiFromDTO(dto.kpis),
      headcountTrend: this.createHeadcountTrendListFromDTOs(dto.headcount_trend),
      departmentDistribution: this.createDepartmentDistributionListFromDTOs(
        dto.department_distribution
      ),
      attendanceStats: this.createAttendanceStatsListFromDTOs(dto.attendance_stats),
      salaryDistribution: this.createSalaryDistributionListFromDTOs(dto.salary_distribution),
    };
  }

  // ========== Report Transformations ==========

  /**
   * 轉換報表定義
   */
  static createDefinitionFromDTO(dto: ReportDefinitionDto): ReportDefinitionViewModel {
    return {
      definitionId: dto.id,
      reportCode: dto.report_code,
      reportName: dto.report_name,
      reportType: dto.report_type,
      reportTypeLabel: REPORT_TYPE_LABELS[dto.report_type] || dto.report_type,
      reportTypeIcon: REPORT_TYPE_ICONS[dto.report_type] || 'FileOutlined',
      description: dto.description,
      parameters: dto.parameters.map((p) => ({
        name: p.name,
        label: p.label,
        type: p.type,
        required: p.required,
        defaultValue: p.default_value,
        options: p.options,
      })),
      availableFormats: dto.available_formats,
      availableFormatsDisplay: dto.available_formats
        .map((f) => REPORT_FORMAT_LABELS[f])
        .join(', '),
      isScheduled: dto.is_scheduled,
    };
  }

  static createDefinitionListFromDTOs(dtos: ReportDefinitionDto[]): ReportDefinitionViewModel[] {
    return dtos.map((dto) => this.createDefinitionFromDTO(dto));
  }

  /**
   * 轉換報表記錄
   */
  static createReportFromDTO(dto: ReportDto): ReportViewModel {
    return {
      reportId: dto.id,
      definitionId: dto.report_definition_id,
      reportName: dto.report_name,
      reportType: dto.report_type,
      reportTypeLabel: REPORT_TYPE_LABELS[dto.report_type] || dto.report_type,
      format: dto.format,
      formatLabel: REPORT_FORMAT_LABELS[dto.format] || dto.format,
      formatIcon: REPORT_FORMAT_ICONS[dto.format] || 'FileOutlined',
      parameters: dto.parameters,
      status: dto.status,
      statusLabel: REPORT_STATUS_LABELS[dto.status] || dto.status,
      statusColor: REPORT_STATUS_COLORS[dto.status] || 'default',
      filePath: dto.file_path,
      downloadUrl: dto.download_url,
      generatedBy: dto.generated_by,
      generatedByName: dto.generated_by_name,
      generatedAt: dto.generated_at,
      generatedAtDisplay: dto.generated_at ? formatDateTime(dto.generated_at) : undefined,
      expiresAt: dto.expires_at,
      expiresAtDisplay: dto.expires_at ? formatDateTime(dto.expires_at) : undefined,
      errorMessage: dto.error_message,
      canDownload: dto.status === 'COMPLETED' && !!dto.download_url,
      isProcessing: dto.status === 'PENDING' || dto.status === 'GENERATING',
    };
  }

  static createReportListFromDTOs(dtos: ReportDto[]): ReportViewModel[] {
    return dtos.map((dto) => this.createReportFromDTO(dto));
  }

  /**
   * 轉換排程報表
   */
  static createScheduledReportFromDTO(dto: ScheduledReportDto): ScheduledReportViewModel {
    let scheduleDayLabel: string | undefined;
    if (dto.schedule_day !== undefined) {
      if (dto.schedule_type === 'WEEKLY') {
        scheduleDayLabel = `週${WEEKDAY_LABELS[dto.schedule_day]}`;
      } else if (dto.schedule_type === 'MONTHLY') {
        scheduleDayLabel = `${dto.schedule_day}日`;
      }
    }

    return {
      scheduleId: dto.id,
      definitionId: dto.report_definition_id,
      reportName: dto.report_name,
      scheduleType: dto.schedule_type,
      scheduleTypeLabel: SCHEDULE_TYPE_LABELS[dto.schedule_type] || dto.schedule_type,
      scheduleTime: dto.schedule_time,
      scheduleDay: dto.schedule_day,
      scheduleDayLabel,
      format: dto.format,
      formatLabel: REPORT_FORMAT_LABELS[dto.format] || dto.format,
      recipients: dto.recipients,
      recipientsDisplay: dto.recipients.length > 0 ? `${dto.recipients.length} 位收件人` : '無',
      isActive: dto.is_active,
      statusLabel: dto.is_active ? '啟用' : '停用',
      statusColor: dto.is_active ? 'success' : 'default',
      lastRunAt: dto.last_run_at,
      lastRunAtDisplay: dto.last_run_at ? formatDateTime(dto.last_run_at) : undefined,
      nextRunAt: dto.next_run_at,
      nextRunAtDisplay: formatDateTime(dto.next_run_at),
    };
  }

  static createScheduledReportListFromDTOs(
    dtos: ScheduledReportDto[]
  ): ScheduledReportViewModel[] {
    return dtos.map((dto) => this.createScheduledReportFromDTO(dto));
  }
}
