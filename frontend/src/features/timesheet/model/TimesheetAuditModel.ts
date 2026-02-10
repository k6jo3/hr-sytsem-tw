/**
 * Timesheet Audit Model (工時與考勤勾稽模型)
 */

export type AuditStatus = 'OK' | 'WARN' | 'ERROR';

export interface DailyAuditResult {
  date: string;
  timesheetHours: number;
  attendanceHours: number;
  leaveHours: number;
  status: AuditStatus;
  message?: string;
}

export interface WeeklyAuditSummary {
  weekStartDate: string;
  totalTimesheetHours: number;
  totalAttendanceHours: number;
  totalLeaveHours: number;
  dailyResults: DailyAuditResult[];
  hasMismatch: boolean;
}
