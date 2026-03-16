/**
 * Insurance DTOs (保險管理 資料傳輸物件)
 * Domain Code: HR05
 */

/**
 * 保險類型
 */
export type InsuranceType = 'LABOR' | 'HEALTH' | 'PENSION';

/**
 * 投保狀態
 */
export type EnrollmentStatus = 'PENDING' | 'ACTIVE' | 'WITHDRAWN';

/**
 * 異動類型
 */
export type ChangeType = 'ENROLL' | 'WITHDRAW' | 'ADJUST_LEVEL';

/**
 * 投保級距 DTO
 */
export interface InsuranceLevelDto {
  level_id: string;
  insurance_type: InsuranceType;
  level_number: number;
  monthly_salary: number;
  labor_employee_rate?: number;
  labor_employer_rate?: number;
  health_employee_rate?: number;
  health_employer_rate?: number;
  pension_employer_rate?: number;
  effective_date: string;
  end_date?: string;
  is_active: boolean;
}

/**
 * 投保記錄 DTO
 */
export interface EnrollmentDto {
  enrollment_id: string;
  employee_id: string;
  employee_name: string;
  insurance_unit_id: string;
  insurance_unit_name: string;
  insurance_type: InsuranceType;
  enroll_date: string;
  withdraw_date?: string;
  monthly_salary: number;
  level_number: number;
  status: EnrollmentStatus;
  is_reported: boolean;
  reported_at?: string;
  created_at: string;
  updated_at: string;
}

/**
 * 保費明細 DTO
 */
export interface InsuranceFeesDto {
  labor_employee: number;
  labor_employer: number;
  health_employee: number;
  health_employer: number;
  pension_employer: number;
  total_employee: number;
  total_employer: number;
}

/**
 * 投保歷程記錄 DTO
 */
export interface EnrollmentHistoryDto {
  history_id: string;
  change_date: string;
  change_type: ChangeType;
  insurance_type: InsuranceType;
  monthly_salary: number;
  level_number: number;
  reason: string;
  operator_name?: string;
  created_at: string;
}

/**
 * 我的保險資訊 DTO
 */
export interface MyInsuranceInfoDto {
  employee_id: string;
  employee_name: string;
  employee_code: string;
  unit_name: string;
  enrollments: EnrollmentDto[];
  fees: InsuranceFeesDto;
  history: EnrollmentHistoryDto[];
  has_active_enrollment: boolean;
}

/**
 * 投保單位 DTO
 */
export interface InsuranceUnitDto {
  unit_id: string;
  unit_code: string;
  unit_name: string;
  labor_insurance_number?: string;
  health_insurance_number?: string;
  pension_number?: string;
  is_active: boolean;
}

/**
 * GET /api/v1/insurance/my - 查詢我的保險資訊 (ESS)
 */
export interface GetMyInsuranceRequest {
  // No parameters needed - gets current user's info from JWT
}

export interface GetMyInsuranceResponse {
  insurance_info: MyInsuranceInfoDto;
}

/**
 * GET /api/v1/insurance/enrollments - 查詢加退保記錄
 */
export interface GetEnrollmentsRequest {
  employee_id?: string;
  insurance_type?: InsuranceType;
  status?: EnrollmentStatus;
  start_date?: string;
  end_date?: string;
  page?: number;
  page_size?: number;
}

export interface GetEnrollmentsResponse {
  enrollments: EnrollmentDto[];
  total: number;
  page: number;
  page_size: number;
}

/**
 * POST /api/v1/insurance/enrollments - 手動加保
 */
export interface CreateEnrollmentRequest {
  employee_id: string;
  insurance_unit_id: string;
  insurance_types: InsuranceType[];
  enroll_date: string;
  monthly_salary: number;
  reason: string;
}

export interface CreateEnrollmentResponse {
  enrollment_ids: string[];
  message: string;
}

/**
 * PUT /api/v1/insurance/enrollments/{id}/withdraw - 退保
 */
export interface WithdrawEnrollmentRequest {
  withdraw_date: string;
  reason: string;
}

export interface WithdrawEnrollmentResponse {
  enrollment_id: string;
  message: string;
}

/**
 * PUT /api/v1/insurance/enrollments/{id}/adjust-level - 調整投保級距
 */
export interface AdjustLevelRequest {
  new_monthly_salary: number;
  effective_date: string;
  reason: string;
}

export interface AdjustLevelResponse {
  enrollment_id: string;
  new_level_number: number;
  new_monthly_salary: number;
  message: string;
}

/**
 * POST /api/v1/insurance/fees/calculate - 計算保費
 */
export interface CalculateFeesRequest {
  monthly_salary: number;
}

export interface CalculateFeesResponse {
  fees: InsuranceFeesDto;
  level_number: number;
}

/**
 * GET /api/v1/insurance/levels - 查詢投保級距
 */
export interface GetLevelsRequest {
  insurance_type?: InsuranceType;
  effective_date?: string;
}

export interface GetLevelsResponse {
  levels: InsuranceLevelDto[];
  total: number;
}

/**
 * POST /api/v1/insurance/levels/batch-adjust - 批量調整投保級距
 */
export interface BatchAdjustLevelsRequest {
  insurance_types: InsuranceType[];
  adjustment_amount: number;
  effective_date: string;
  new_highest_level_salary?: number;
}

export interface BatchAdjustLevelsResponse {
  old_levels_deactivated: number;
  new_levels_created: number;
  message: string;
}
