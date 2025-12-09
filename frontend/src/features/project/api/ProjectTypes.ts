/**
 * Project Management API Types
 * Domain Code: HR06 - 專案管理服務
 */

// ========== Enums ==========

/**
 * 專案類型
 */
export type ProjectType = 'DEVELOPMENT' | 'MAINTENANCE' | 'CONSULTING';

/**
 * 專案狀態
 */
export type ProjectStatus = 'PLANNING' | 'IN_PROGRESS' | 'COMPLETED' | 'ON_HOLD' | 'CANCELLED';

/**
 * 預算模式
 */
export type BudgetType = 'FIXED_PRICE' | 'TIME_AND_MATERIAL';

// ========== DTOs ==========

/**
 * 專案 DTO
 */
export interface ProjectDto {
  id: string;
  project_code: string;
  project_name: string;
  customer_id: string;
  customer_name: string;
  project_type: ProjectType;
  project_manager_id: string;
  project_manager_name: string;
  budget_type: BudgetType;
  budget_amount: number;
  budget_hours: number;
  actual_cost: number;
  actual_hours: number;
  progress: number; // 0-100
  status: ProjectStatus;
  planned_start_date: string;
  planned_end_date: string;
  actual_start_date?: string;
  actual_end_date?: string;
  created_at: string;
  updated_at: string;
}

/**
 * 客戶 DTO
 */
export interface CustomerDto {
  id: string;
  customer_code: string;
  customer_name: string;
  contact_person?: string;
  contact_email?: string;
  contact_phone?: string;
  created_at: string;
}

// ========== Request/Response Types ==========

/**
 * 取得專案列表請求
 */
export interface GetProjectListRequest {
  page?: number;
  page_size?: number;
  keyword?: string;
  customer_id?: string;
  status?: ProjectStatus;
  project_type?: ProjectType;
}

/**
 * 取得專案列表回應
 */
export interface GetProjectListResponse {
  projects: ProjectDto[];
  total: number;
  page: number;
  page_size: number;
}

/**
 * 取得專案詳情回應
 */
export interface GetProjectDetailResponse {
  project: ProjectDto;
}

/**
 * 建立專案請求
 */
export interface CreateProjectRequest {
  project_code: string;
  project_name: string;
  customer_id: string;
  project_type: ProjectType;
  project_manager_id: string;
  budget_type: BudgetType;
  budget_amount: number;
  budget_hours: number;
  planned_start_date: string;
  planned_end_date: string;
  description?: string;
}

/**
 * 建立專案回應
 */
export interface CreateProjectResponse {
  project_id: string;
  project_code: string;
  message: string;
}

/**
 * 更新專案請求
 */
export interface UpdateProjectRequest {
  project_name?: string;
  project_manager_id?: string;
  budget_amount?: number;
  budget_hours?: number;
  planned_start_date?: string;
  planned_end_date?: string;
  description?: string;
}

/**
 * 取得客戶列表回應
 */
export interface GetCustomerListResponse {
  customers: CustomerDto[];
  total: number;
}
