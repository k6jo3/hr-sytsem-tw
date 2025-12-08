/**
 * Attendance DTOs (考勤管理 資料傳輸物件)
 * Domain Code: HR03
 */

/**
 * 打卡請求
 */
export interface CheckInRequest {
  type: 'IN' | 'OUT';
  location?: {
    latitude: number;
    longitude: number;
  };
}

/**
 * 考勤記錄 DTO
 */
export interface AttendanceDto {
  id: string;
  employee_id: string;
  date: string;
  check_in_time?: string;
  check_out_time?: string;
  work_hours?: number;
  status: 'NORMAL' | 'LATE' | 'EARLY_LEAVE' | 'ABSENT';
  created_at: string;
}

/**
 * 請假申請 DTO
 */
export interface LeaveRequestDto {
  id: string;
  employee_id: string;
  leave_type: string;
  start_date: string;
  end_date: string;
  days: number;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  created_at: string;
}
