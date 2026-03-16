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
  employeeId: string;
  employeeName: string;
  checkType: CheckType;
  checkTime: string;
  status: AttendanceStatus;
  latitude?: number;
  longitude?: number;
  address?: string;
  deviceInfo?: string;
  createdAt: string;
}

/**
 * Check In Request
 */
export interface CheckInRequest {
  employeeId: string;
  checkTime?: string; // Optional, backend might use current time if null
  latitude?: number;
  longitude?: number;
  address?: string;
  ipAddress?: string;
  deviceInfo?: string;
}

/**
 * Check Out Request
 */
export interface CheckOutRequest {
  employeeId: string;
  checkOutTime?: string;
  latitude?: number;
  longitude?: number;
  address?: string;
  ipAddress?: string;
  deviceInfo?: string;
}

/**
 * Check In Response
 */
export interface CheckInResponse {
  success: boolean;
  recordId: string;
  checkInTime: string;
  isLate: boolean;
  lateMinutes: number;
  shiftName: string;
  message: string;
}

/**
 * Check Out Response
 */
export interface CheckOutResponse {
  success: boolean;
  recordId: string;
  checkOutTime: string;
  isEarlyLeave: boolean;
  earlyLeaveMinutes: number;
  shiftName: string;
  message: string;
}

/**
 * Today Attendance Request
 */
export interface GetTodayAttendanceRequest {
  employeeId?: string;
  date?: string;
}

/**
 * Today Attendance Response
 */
export interface GetTodayAttendanceResponse {
  records: AttendanceRecordDto[];
  hasCheckedIn: boolean;
  hasCheckedOut: boolean;
  totalWorkHours?: number;
}

/**
 * Attendance History Request
 */
export interface GetAttendanceHistoryRequest {
  employeeId?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  pageSize?: number;
}

/**
 * Attendance History Response
 */
export interface GetAttendanceHistoryResponse {
  records: AttendanceRecordDto[];
  total: number;
  page: number;
  pageSize: number;
}
/**
 * Leave Application Status
 */
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

/**
 * Leave Application DTO
 */
export interface LeaveApplicationDto {
  applicationId: string;
  employeeId: string;
  employeeName: string;
  employeeNumber: string;
  leaveTypeCode: string;
  leaveTypeName: string;
  startDate: string;
  endDate: string;
  leaveDays: number;
  status: LeaveStatus;
  appliedAt: string;
  reason?: string;
  rejectionReason?: string;
}

/**
 * Leave Type DTO
 */
export interface LeaveTypeDto {
  leaveTypeId: string;
  leaveTypeCode: string;
  leaveTypeName: string;
  isPaid: boolean;
  annualQuotaDays: number;
  allowCarryOver: boolean;
  isActive: boolean;
}

/**
 * Leave Balance DTO
 */
export interface LeaveBalanceDto {
  leaveTypeId: string;
  leaveTypeName: string;
  totalDays: number;
  usedDays: number;
  remainingDays: number;
  year: number;
}

/**
 * Apply Leave Request
 */
export interface ApplyLeaveRequest {
  employeeId: string;
  leaveTypeCode: string;
  startDate: string;
  endDate: string;
  startTime?: string;
  endTime?: string;
  reason: string;
  proofAttachmentUrl?: string;
}

/**
 * Apply Leave Response
 */
export interface ApplyLeaveResponse {
  success: boolean;
  applicationId: string;
  message: string;
}

/**
 * Get Leave Applications Request
 */
export interface GetLeaveApplicationsRequest {
  employeeId?: string;
  status?: LeaveStatus;
  startDate?: string;
  endDate?: string;
  page?: number;
  pageSize?: number;
}

/**
 * Get Leave Applications Response
 */
export interface GetLeaveApplicationsResponse {
  items: LeaveApplicationDto[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

/**
 * Leave Balance List Response
 */
export interface LeaveBalanceListResponse {
  balances: LeaveBalanceDto[];
  employeeId: string;
}
/**
 * Overtime Status
 */
export type OvertimeStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

/**
 * 加班類型（對齊後端 OvertimeType 枚舉）
 * 後端枚舉值：WORKDAY / REST_DAY / HOLIDAY
 */
export type OvertimeType = 'WORKDAY' | 'REST_DAY' | 'HOLIDAY';

/**
 * Overtime Application DTO
 */
export interface OvertimeApplicationDto {
  applicationId: string;
  employeeId: string;
  employeeName?: string;
  overtimeDate: string;
  overtimeHours: number;
  overtimeType: OvertimeType;
  status: OvertimeStatus;
  appliedAt: string;
  reason?: string;
}

/**
 * Apply Overtime Request
 */
export interface ApplyOvertimeRequest {
  employeeId: string;
  overtimeDate: string;
  startTime: string;
  endTime: string;
  overtimeType: string;
  reason: string;
}

/**
 * Apply Overtime Response
 */
export interface ApplyOvertimeResponse {
  success: boolean;
  applicationId: string;
  message: string;
}

/**
 * Get Overtime Applications Request
 */
export interface GetOvertimeApplicationsRequest {
  employeeId?: string;
  status?: OvertimeStatus;
  startDate?: string;
  endDate?: string;
  page?: number;
  pageSize?: number;
}

/**
 * Get Overtime Applications Response
 */
export interface GetOvertimeApplicationsResponse {
  items: OvertimeApplicationDto[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

/**
 * Correction Status
 */
export type CorrectionStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

/**
 * Correction Type
 */
export type CorrectionType = 'FORGET_CHECK_IN' | 'FORGET_CHECK_OUT' | 'DEVICE_FAILURE' | 'OUT_FOR_BUSINESS' | 'OTHER';

/**
 * Create Correction Request
 */
export interface CreateCorrectionRequest {
  employeeId: string;
  attendanceRecordId?: string;
  correctionDate: string;
  correctionType: CorrectionType;
  correctedCheckInTime?: string;
  correctedCheckOutTime?: string;
  reason: string;
}

/**
 * Create Correction Response
 * 對齊後端 CreateCorrectionResponse DTO 結構
 */
export interface CreateCorrectionResponse {
  success: boolean;
  correctionId: string;
  status: string;
  workflowInstanceId?: string;
  createdAt?: string;
  message: string;
}
/**
 * Correction Application DTO
 */
export interface CorrectionApplicationDto {
  correctionId: string;
  employeeId: string;
  employeeName: string;
  applicationDate: string;
  correctionDate: string;
  correctionType: 'CHECK_IN' | 'CHECK_OUT' | 'BOTH';
  status: CorrectionStatus;
  appliedAt: string;
  reviewedAt?: string;
  reason?: string;
}

/**
 * Get Correction List Request
 */
export interface GetCorrectionListRequest {
  employeeId?: string;
  status?: CorrectionStatus;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

/**
 * Get Correction List Response
 */
export interface GetCorrectionListResponse {
  items: CorrectionApplicationDto[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

/**
 * Approve Correction Request
 */
export interface ApproveCorrectionRequest {
  comment?: string;
}

/**
 * Approve Correction Response
 * 對齊後端 ApproveCorrectionResponse DTO 結構
 */
export interface ApproveCorrectionResponse {
  success: boolean;
  correctionId: string;
  status: string;
  approvedBy?: string;
  approvedAt?: string;
  message: string;
}

/**
 * Reject Leave Request
 */
export interface RejectLeaveRequest {
  reason: string;
}

/**
 * Shift DTO
 */
export interface ShiftDto {
  shiftId: string;
  shiftCode: string;
  shiftName: string;
  shiftType: 'STANDARD' | 'FLEXIBLE' | 'ROTATING';
  workStartTime: string;
  workEndTime: string;
  workingHours: number;
  isActive: boolean;
  employeeCount?: number;
}

/**
 * Create Shift Request
 */
export interface CreateShiftRequest {
  organizationId: string;
  shiftCode: string;
  shiftName: string;
  shiftType: string;
  workStartTime: string;
  workEndTime: string;
  breakStartTime?: string;
  breakEndTime?: string;
  lateToleranceMinutes?: number;
  earlyLeaveToleranceMinutes?: number;
}

/**
 * Update Shift Request
 */
export interface UpdateShiftRequest extends Partial<CreateShiftRequest> {
  isActive?: boolean;
}

/**
 * Create Leave Type Request
 */
export interface CreateLeaveTypeRequest {
  leaveTypeCode: string;
  leaveTypeName: string;
  isPaid: boolean;
  annualQuotaDays: number;
  allowCarryOver: boolean;
  isActive: boolean;
}

/**
 * Update Leave Type Request
 */
export interface UpdateLeaveTypeRequest extends Partial<CreateLeaveTypeRequest> {}

/**
 * Monthly Report Types
 */
export interface MonthlyReportItem {
  employeeId: string;
  employeeName: string;
  employeeNumber: string;
  departmentName: string;
  scheduledDays: number;
  actualDays: number;
  absentDays: number;
  lateCount: number;
  earlyLeaveCount: number;
  leaveDays: number;
  overtimeHours: number;
  totalWorkHours: number;
}

export interface ReportSummary {
  totalEmployees: number;
  averageAttendanceRate: number;
  totalLateCount: number;
  totalEarlyLeaveCount: number;
  totalOvertimeHours: number;
}

export interface GetMonthlyReportResponse {
  year: number;
  month: number;
  items: MonthlyReportItem[];
  summary: ReportSummary;
}

/**
 * Daily Report Types
 */
export interface DailyReportItem {
  employeeId: string;
  employeeName: string;
  employeeNumber: string;
  departmentName: string;
  status: string;
  checkInTime?: string;
  checkOutTime?: string;
  isLate: boolean;
  isEarlyLeave: boolean;
}

export interface GetDailyReportResponse {
  date: string;
  items: DailyReportItem[];
  totalPresent: number;
  totalAbsent: number;
}

/**
 * Month Close Types
 */
export interface ExecuteMonthCloseRequest {
  year: number;
  month: number;
}

export interface ExecuteMonthCloseResponse {
  success: boolean;
  message: string;
  batchId?: string;
}
