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
  description?: string;
  members?: ProjectMemberDto[];
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
  tax_id?: string;
  industry?: string;
  email?: string;
  phone_number?: string;
  status: 'ACTIVE' | 'INACTIVE';
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
 * 取得客戶列表請求
 */
export interface GetCustomerListRequest {
  page?: number;
  size?: number;
  keyword?: string;
  status?: string;
  industry?: string;
}

/**
 * 取得客戶列表回應
 */
export interface GetCustomerListResponse {
  customers: CustomerDto[];
  total: number;
}

/**
 * 建立客戶請求
 */
export interface CreateCustomerRequest {
  customer_code: string;
  customer_name: string;
  tax_id?: string;
  industry?: string;
  email?: string;
  phone_number?: string;
}

/**
 * 更新客戶請求
 */
export interface UpdateCustomerRequest {
  customer_name?: string;
  tax_id?: string;
  industry?: string;
  email?: string;
  phone_number?: string;
  status?: 'ACTIVE' | 'INACTIVE';
}

// ========== WBS / Task Types ==========

/**
 * 工項狀態
 */
export type TaskStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'ON_HOLD' | 'BLOCKED';

/**
 * 工項 DTO
 */
export interface TaskDto {
  id: string;
  project_id: string;
  parent_task_id?: string;
  task_code: string;
  task_name: string;
  level: number;
  estimated_hours: number;
  actual_hours: number;
  assignee_id?: string;
  assignee_name?: string;
  status: TaskStatus;
  progress: number;
  start_date?: string;
  end_date?: string;
  display_order: number;
  children?: TaskDto[];
  created_at: string;
  updated_at: string;
}

/**
 * 取得專案工項請求
 */
export interface GetProjectTasksRequest {
  project_id: string;
}

/**
 * 建立工項請求
 */
export interface CreateTaskRequest {
  project_id: string;
  parent_task_id?: string;
  task_code: string;
  task_name: string;
  estimated_hours: number;
  assignee_id?: string;
  description?: string;
}

/**
 * 更新工項進度請求
 */
export interface UpdateTaskProgressRequest {
  progress: number;
  status?: TaskStatus;
}

// ========== Member / Cost Types ==========

/**
 * 專案成員 DTO
 */
export interface ProjectMemberDto {
  member_id: string;
  project_id: string;
  employee_id: string;
  employee_name: string;
  role: string;
  allocated_hours: number;
  actual_hours: number;
  join_date: string;
}

/**
 * 專案成本統計 DTO
 */
export interface ProjectCostDto {
  project_id: string;
  budget_amount: number;
  actual_cost: number;
  cost_utilization: number;
  budget_hours: number;
  actual_hours: number;
  hour_utilization: number;
  profit_margin: number;
  member_costs: MemberCostDto[];
}

/**
 * 成員成本 DTO
 */
export interface MemberCostDto {
  employee_id: string;
  employee_name: string;
  role: string;
  hours: number;
  hourly_rate: number;
  cost: number;
}

/**
 * 新增專案成員請求
 */
export interface AddProjectMemberRequest {
  employee_id: string;
  role: string;
  allocated_hours: number;
  join_date: string;
}
