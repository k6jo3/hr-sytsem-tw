/**
 * Timesheet API Types
 * 工時填報相關的 API 資料型別定義
 */

/**
 * 工時狀態
 */
export type TimesheetStatus = 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'REJECTED';

/**
 * Timesheet Entry DTO (from API)
 */
export interface TimesheetEntryDto {
  id: string;
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
  week_start_date: string;
  week_end_date: string;
  entries: TimesheetEntryDto[];
  total_hours: number;
  status: TimesheetStatus;
}

/**
 * Submit Timesheet Request
 */
export interface SubmitTimesheetRequest {
  entries: {
    project_id: string;
    wbs_code?: string;
    work_date: string;
    hours: number;
    description?: string;
  }[];
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
  employee_id?: string;
  week_start_date: string;
}

/**
 * Get Weekly Timesheet Response
 */
export interface GetWeeklyTimesheetResponse {
  timesheet: WeeklyTimesheetDto;
}

/**
 * Get Timesheet History Request
 */
export interface GetTimesheetHistoryRequest {
  employee_id?: string;
  start_date?: string;
  end_date?: string;
  status?: TimesheetStatus;
  page?: number;
  page_size?: number;
}

/**
 * Get Timesheet History Response
 */
export interface GetTimesheetHistoryResponse {
  entries: TimesheetEntryDto[];
  total: number;
  page: number;
  page_size: number;
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
  code: string;
  name: string;
  level: number;
}
