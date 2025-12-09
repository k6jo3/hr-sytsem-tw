/**
 * Attendance ViewModel (考勤視圖模型)
 * 前端顯示用的資料模型
 */
export interface AttendanceViewModel {
  id: string;
  employeeId: string;
  date: string;
  checkInTime?: string;
  checkOutTime?: string;
  workHours?: number;
  statusLabel: string;
  statusColor: string;
}
