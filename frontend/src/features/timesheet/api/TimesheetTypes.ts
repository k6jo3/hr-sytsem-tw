/**
 * 工時狀態
 */
export type TimesheetStatus = 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'REJECTED' | 'LOCKED';

/**
 * Timesheet Entry DTO (from API)
 */
export interface TimesheetEntryDto {
  id: string;
  timesheet_id?: string;
  employee_id: string;
  employee_name: string;
  project_id: string;
  project_name: string;
  wbs_code?: string;
  wbs_name?: string;
  work_date: string;
  hours: number;
  description?: string;
  status: TimesheetStatus;
  created_at: string;
  updated_at: string;
}

/**
 * Weekly Timesheet DTO
 */
export interface WeeklyTimesheetDto {
  id: string;
  employee_id: string;
  employee_name: string;
  week_start_date: string;
  week_end_date: string;
  entries: TimesheetEntryDto[];
  total_hours: number;
  status: TimesheetStatus;
  submitted_at?: string;
  rejection_reason?: string;
}

/**
 * Submit Timesheet Entry (Individual)
 */
export interface SaveTimesheetEntryRequest {
  timesheet_id?: string;
  project_id: string;
  wbs_code?: string;
  work_date: string;
  hours: number;
  description?: string;
}

/**
 * Submit Timesheet Response
 */
export interface SubmitTimesheetResponse {
  timesheet_id: string;
  message: string;
}

/**
 * Get Weekly Timesheet Request
 */
export interface GetWeeklyTimesheetRequest {
  id?: string;
  employee_id?: string;
  week_start_date?: string;
}

/**
 * Get Weekly Timesheet Response
 */
export interface GetWeeklyTimesheetResponse {
  timesheet: WeeklyTimesheetDto;
}

/**
 * Get Pending Approvals Request
 */
export interface GetPendingApprovalsRequest {
  project_id?: string;
  employee_id?: string;
  page?: number;
  page_size?: number;
}

/**
 * Get Pending Approvals Response
 */
export interface GetPendingApprovalsResponse {
  timesheets: WeeklyTimesheetDto[];
  total: number;
}

/**
 * Approve/Reject Request
 */
export interface TimesheetApprovalRequest {
  rejection_reason?: string;
}

/**
 * Batch Approval Request
 */
export interface BatchApprovalRequest {
  timesheet_ids: string[];
}

/**
 * Timesheet Report Summary DTO
 */
export interface TimesheetReportSummaryDto {
  total_hours: number;
  project_hours: { project_name: string; hours: number }[];
  department_hours: { department_name: string; hours: number }[];
  unreported_employees: { id: string; name: string }[];
}

/**
 * Project DTO (簡化版，用於選擇器)
 */
export interface ProjectDto {
  id: string;
  code: string;
  name: string;
  customer_name?: string;
}

/**
 * WBS Item DTO (簡化版，用於選擇器)
 */
export interface WbsItemDto {
  id: string;
  code: string;
  name: string;
  level: number;
}
