/**
 * Timesheet ViewModel (工時填報視圖模型)
 * 前端顯示用的資料模型
 */

/**
 * Timesheet Entry ViewModel
 */
export interface TimesheetEntryViewModel {
  id: string;
  employeeName: string;
  projectName: string;
  projectDisplay: string;
  wbsDisplay?: string;
  workDate: string;
  workDateDisplay: string;
  hours: number;
  description?: string;
  statusLabel: string;
  statusColor: string;
  canEdit: boolean;
  canDelete: boolean;
}

/**
 * Weekly Timesheet Summary ViewModel
 */
export interface WeeklyTimesheetSummary {
  id: string;
  employeeId: string;
  employeeName: string;
  weekStartDate: string;
  weekEndDate: string;
  weekDisplay: string;
  entries: TimesheetEntryViewModel[];
  totalHours: number;
  status: string;
  statusLabel: string;
  statusColor: string;
  canSubmit: boolean;
  canEdit: boolean;
  rejectionReason?: string;
}

/**
 * Daily Hours Summary (用於週視圖)
 */
export interface DailyHoursSummary {
  date: string;
  dateDisplay: string;
  totalHours: number;
  entries: TimesheetEntryViewModel[];
}
