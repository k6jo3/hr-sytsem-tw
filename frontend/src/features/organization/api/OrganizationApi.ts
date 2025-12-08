import { apiClient } from '@shared/api';
import type { EmployeeDto, DepartmentDto } from './OrganizationTypes';

/**
 * Organization API (組織員工管理 API)
 * Domain Code: HR02
 */
export class OrganizationApi {
  private static readonly BASE_PATH = '/organization';

  /**
   * 取得員工列表
   */
  static async getEmployees(params?: { department?: string; status?: string }): Promise<EmployeeDto[]> {
    return apiClient.get(`${this.BASE_PATH}/employees`, { params });
  }

  /**
   * 取得員工詳情
   */
  static async getEmployeeById(id: string): Promise<EmployeeDto> {
    return apiClient.get(`${this.BASE_PATH}/employees/${id}`);
  }

  /**
   * 取得部門列表
   */
  static async getDepartments(): Promise<DepartmentDto[]> {
    return apiClient.get(`${this.BASE_PATH}/departments`);
  }
}
