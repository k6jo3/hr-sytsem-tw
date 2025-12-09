/**
 * Attendance Record ViewModel (考勤記錄視圖模型)
 * 前端顯示用的資料模型
 */
export interface AttendanceRecordViewModel {
  id: string;
  employeeName: string;
  checkTypeLabel: string;
  checkTypeColor: string;
  checkTime: string;
  checkTimeDisplay: string;
  statusLabel: string;
  statusColor: string;
  address?: string;
  isNormal: boolean;
}

/**
 * Today Attendance Summary ViewModel
 */
export interface TodayAttendanceSummary {
  hasCheckedIn: boolean;
  hasCheckedOut: boolean;
  totalWorkHours?: number;
  records: AttendanceRecordViewModel[];
  canCheckIn: boolean;
  canCheckOut: boolean;
}
