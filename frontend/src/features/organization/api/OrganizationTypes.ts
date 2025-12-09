/**
 * Organization API Types
 * 組織員工相關的 API 資料型別定義
 */

/**
 * Employee DTO (from API)
 */
export interface EmployeeDto {
  id: string;
  employee_number: string;
  first_name: string;
  last_name: string;
  email: string;
  phone?: string;
  department_id: string;
  department_name: string;
  position: string;
  status: 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE' | 'TERMINATED';
  hire_date: string;
  termination_date?: string;
  created_at: string;
  updated_at: string;
}

/**
 * Employee List Request
 */
export interface GetEmployeeListRequest {
  page?: number;
  page_size?: number;
  search?: string;
  department_id?: string;
  status?: string;
  sort_by?: string;
  sort_order?: 'asc' | 'desc';
}

/**
 * Employee List Response
 */
export interface GetEmployeeListResponse {
  employees: EmployeeDto[];
  total: number;
  page: number;
  page_size: number;
}

/**
 * Employee Detail Response
 */
export interface GetEmployeeDetailResponse {
  employee: EmployeeDto;
}
