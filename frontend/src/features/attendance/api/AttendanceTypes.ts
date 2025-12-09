/**
 * Attendance API Types
 * 考勤相關的 API 資料型別定義
 */

/**
 * 打卡類型
 */
export type CheckType = 'CHECK_IN' | 'CHECK_OUT' | 'BREAK_OUT' | 'BREAK_IN';

/**
 * 打卡狀態
 */
export type AttendanceStatus = 'NORMAL' | 'LATE' | 'EARLY_LEAVE' | 'ABSENT';

/**
 * Attendance Record DTO (from API)
 */
export interface AttendanceRecordDto {
  id: string;
  employee_id: string;
  employee_name: string;
  check_type: CheckType;
  check_time: string;
  status: AttendanceStatus;
  latitude?: number;
  longitude?: number;
  address?: string;
  device_info?: string;
  created_at: string;
}

/**
 * Check In Request
 */
export interface CheckInRequest {
  check_type: CheckType;
  latitude?: number;
  longitude?: number;
  address?: string;
  device_info?: string;
}

/**
 * Check In Response
 */
export interface CheckInResponse {
  record: AttendanceRecordDto;
  message: string;
}

/**
 * Today Attendance Request
 */
export interface GetTodayAttendanceRequest {
  employee_id?: string;
  date?: string;
}

/**
 * Today Attendance Response
 */
export interface GetTodayAttendanceResponse {
  records: AttendanceRecordDto[];
  has_checked_in: boolean;
  has_checked_out: boolean;
  total_work_hours?: number;
}

/**
 * Attendance History Request
 */
export interface GetAttendanceHistoryRequest {
  employee_id?: string;
  start_date?: string;
  end_date?: string;
  page?: number;
  page_size?: number;
}

/**
 * Attendance History Response
 */
export interface GetAttendanceHistoryResponse {
  records: AttendanceRecordDto[];
  total: number;
  page: number;
  page_size: number;
}
