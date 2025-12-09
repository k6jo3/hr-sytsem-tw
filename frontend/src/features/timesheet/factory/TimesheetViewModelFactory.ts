import type {
  TimesheetEntryDto,
  WeeklyTimesheetDto,
  TimesheetStatus,
} from '../api/TimesheetTypes';
import type {
  TimesheetEntryViewModel,
  WeeklyTimesheetSummary,
  DailyHoursSummary,
} from '../model/TimesheetViewModel';

/**
 * Timesheet ViewModel Factory
 * 將 API 回傳的工時 DTO 轉換為前端 ViewModel
 */
export class TimesheetViewModelFactory {
  /**
   * 將單一工時記錄 DTO 轉換為 ViewModel
   */
  static createFromDTO(dto: TimesheetEntryDto): TimesheetEntryViewModel {
    return {
      id: dto.id,
      employeeName: dto.employee_name,
      projectName: dto.project_name,
      projectDisplay: dto.project_name,
      wbsDisplay: dto.wbs_code && dto.wbs_name ? `${dto.wbs_code} - ${dto.wbs_name}` : undefined,
      workDate: dto.work_date,
      workDateDisplay: this.formatDateDisplay(dto.work_date),
      hours: dto.hours,
      description: dto.description,
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      canEdit: this.canEdit(dto.status),
      canDelete: this.canDelete(dto.status),
    };
  }

  /**
   * 批量轉換工時記錄 DTO 列表
   */
  static createListFromDTOs(dtos: TimesheetEntryDto[]): TimesheetEntryViewModel[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  /**
   * 建立週工時摘要
   */
  static createWeeklySummary(dto: WeeklyTimesheetDto): WeeklyTimesheetSummary {
    return {
      weekStartDate: dto.week_start_date,
      weekEndDate: dto.week_end_date,
      weekDisplay: this.formatWeekDisplay(dto.week_start_date, dto.week_end_date),
      entries: this.createListFromDTOs(dto.entries),
      totalHours: dto.total_hours,
      statusLabel: this.mapStatusLabel(dto.status),
      statusColor: this.mapStatusColor(dto.status),
      canSubmit: this.canSubmit(dto.status),
      canEdit: this.canEdit(dto.status),
    };
  }

  /**
   * 按日期分組工時記錄
   */
  static groupByDate(entries: TimesheetEntryDto[]): DailyHoursSummary[] {
    if (entries.length === 0) return [];

    const grouped = new Map<string, TimesheetEntryDto[]>();

    entries.forEach((entry) => {
      const date = entry.work_date;
      if (!grouped.has(date)) {
        grouped.set(date, []);
      }
      grouped.get(date)!.push(entry);
    });

    return Array.from(grouped.entries()).map(([date, entries]) => ({
      date,
      dateDisplay: this.formatDateDisplay(date),
      totalHours: entries.reduce((sum, entry) => sum + entry.hours, 0),
      entries: this.createListFromDTOs(entries),
    }));
  }

  /**
   * 將狀態對應為中文標籤
   */
  private static mapStatusLabel(status: TimesheetStatus): string {
    const labelMap: Record<TimesheetStatus, string> = {
      DRAFT: '草稿',
      SUBMITTED: '已提交',
      APPROVED: '已核准',
      REJECTED: '已駁回',
    };
    return labelMap[status];
  }

  /**
   * 將狀態對應為顏色
   */
  private static mapStatusColor(status: TimesheetStatus): string {
    const colorMap: Record<TimesheetStatus, string> = {
      DRAFT: 'default',
      SUBMITTED: 'processing',
      APPROVED: 'success',
      REJECTED: 'error',
    };
    return colorMap[status];
  }

  /**
   * 判斷是否可以編輯
   */
  private static canEdit(status: TimesheetStatus): boolean {
    return status === 'DRAFT' || status === 'REJECTED';
  }

  /**
   * 判斷是否可以刪除
   */
  private static canDelete(status: TimesheetStatus): boolean {
    return status === 'DRAFT' || status === 'REJECTED';
  }

  /**
   * 判斷是否可以提交
   */
  private static canSubmit(status: TimesheetStatus): boolean {
    return status === 'DRAFT' || status === 'REJECTED';
  }

  /**
   * 格式化日期顯示為 MM/DD
   */
  private static formatDateDisplay(isoDate: string): string {
    const date = new Date(isoDate);
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${month}/${day}`;
  }

  /**
   * 格式化週區間顯示
   */
  private static formatWeekDisplay(startDate: string, endDate: string): string {
    const startDisplay = this.formatDateDisplay(startDate);
    const endDisplay = this.formatDateDisplay(endDate);
    return `${startDisplay} - ${endDisplay}`;
  }
}
