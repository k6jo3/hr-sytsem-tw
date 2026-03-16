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
  full_name: string;
  email: string;
  phone?: string;
  department_id: string;
  department_name: string;
  position: string;
  status: 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE' | 'TERMINATED' | 'PROBATION';
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

/**
 * Organization DTO
 */
export interface OrganizationDto {
  organizationId: string;
  organizationCode: string;
  organizationName: string;
  organizationType: 'PARENT' | 'SUBSIDIARY';
  taxId?: string;
  address?: string;
  phoneNumber?: string;
  establishedDate?: string;
  status: 'ACTIVE' | 'INACTIVE';
  parentOrganizationId?: string;
  employeeCount?: number;
  createdAt: string;
}

/**
 * Department DTO
 */
export interface DepartmentDto {
  departmentId: string;
  code: string;
  name: string;
  level: number;
  sortOrder: number;
  organizationId: string;
  parentId?: string;
  managerId?: string;
  managerName?: string;
  status: 'ACTIVE' | 'INACTIVE';
  statusDisplay?: string;
  employeeCount: number;
  subDepartments?: DepartmentDto[];
}

/**
 * Create/Update Organization Request
 */
export interface OrganizationRequest {
  organizationCode: string;
  organizationName: string;
  organizationType: 'PARENT' | 'SUBSIDIARY';
  parentOrganizationId?: string;
  taxId?: string;
  address?: string;
  phoneNumber?: string;
  establishedDate?: string;
}

/**
 * Create/Update Department Request
 */
export interface DepartmentRequest {
  organizationId: string;
  parentId?: string;
  code: string;
  name: string;
  managerId?: string;
  sortOrder?: number;
}

