/**
 * Organization DTOs (組織員工管理 資料傳輸物件)
 * Domain Code: HR02
 */

/**
 * 員工 DTO
 */
export interface EmployeeDto {
  id: string;
  employee_number: string;
  first_name: string;
  last_name: string;
  email: string;
  phone?: string;
  department_id: string;
  department_name?: string;
  position: string;
  status: 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE';
  hire_date: string;
  created_at: string;
  updated_at: string;
}

/**
 * 部門 DTO
 */
export interface DepartmentDto {
  id: string;
  code: string;
  name: string;
  parent_id?: string;
  manager_id?: string;
  created_at: string;
}
