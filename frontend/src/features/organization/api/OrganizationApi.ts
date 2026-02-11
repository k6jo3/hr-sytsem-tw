import { MockConfig } from '../../../config/MockConfig';
import { apiClient } from '../../../shared/api/apiClient';
import { MockOrganizationApi } from './MockOrganizationApi';
import type {
    DepartmentDto,
    DepartmentRequest,
    GetEmployeeDetailResponse,
    GetEmployeeListRequest,
    GetEmployeeListResponse,
    OrganizationDto,
    OrganizationRequest,
} from './OrganizationTypes';

/**
 * Organization API
 * 組織員工相關的 API 呼叫
 */
export const OrganizationApi = {
  // ========== 員工管理 API ==========

  /**
   * 取得員工列表
   */
  getEmployeeList: (params?: GetEmployeeListRequest): Promise<GetEmployeeListResponse> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getEmployeeList(params);
    return apiClient.get<GetEmployeeListResponse>('/employees', { params });
  },

  /**
   * 取得員工詳細資料
   */
  getEmployeeDetail: (id: string): Promise<GetEmployeeDetailResponse> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getEmployeeDetail(id);
    return apiClient.get<GetEmployeeDetailResponse>(`/employees/${id}`);
  },

  /**
   * 新增員工
   */
  createEmployee: (data: any): Promise<any> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.createEmployee(data);
    return apiClient.post('/employees', data);
  },

  /**
   * 更新員工資料
   */
  updateEmployee: (id: string, data: any): Promise<any> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.updateEmployee(id, data);
    return apiClient.put(`/employees/${id}`, data);
  },

  /**
   * 刪除員工
   */
  deleteEmployee: (id: string): Promise<void> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.deleteEmployee(id);
    return apiClient.delete(`/employees/${id}`);
  },

  // ========== 組織管理 API ==========

  /**
   * 取得組織列表
   */
  getOrganizations: (): Promise<{ content: OrganizationDto[] }> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getOrganizations();
    return apiClient.get<{ content: OrganizationDto[] }>('/organizations');
  },

  /**
   * 取得單一組織詳情
   */
  getOrganization: (id: string): Promise<OrganizationDto> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getOrganization(id);
    return apiClient.get<OrganizationDto>(`/organizations/${id}`);
  },

  /**
   * 建立組織
   */
  createOrganization: (data: OrganizationRequest): Promise<OrganizationDto> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.createOrganization(data);
    return apiClient.post<OrganizationDto>('/organizations', data);
  },

  /**
   * 更新組織
   */
  updateOrganization: (id: string, data: Partial<OrganizationRequest>): Promise<OrganizationDto> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.updateOrganization(id, data);
    return apiClient.put<OrganizationDto>(`/organizations/${id}`, data);
  },

  /**
   * 取得組織結構樹
   */
  getOrganizationTree: (id: string): Promise<{ data: OrganizationDto; departments: DepartmentDto[] }> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getOrganizationTree(id);
    return apiClient.get(`/organizations/${id}/tree`);
  },

  // ========== 部門管理 API ==========

  /**
   * 取得部門列表
   */
  getDepartments: (params?: { organizationId?: string }): Promise<{ items: DepartmentDto[] }> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getDepartments(params);
    return apiClient.get('/departments', { params });
  },

  /**
   * 建立部門
   */
  createDepartment: (data: DepartmentRequest): Promise<DepartmentDto> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.createDepartment(data);
    return apiClient.post<DepartmentDto>('/departments', data);
  },

  /**
   * 更新部門
   */
  updateDepartment: (id: string, data: Partial<DepartmentRequest>): Promise<DepartmentDto> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.updateDepartment(id, data);
    return apiClient.put<DepartmentDto>(`/departments/${id}`, data);
  },

  /**
   * 刪除部門
   */
  deleteDepartment: (id: string): Promise<void> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.deleteDepartment(id);
    return apiClient.delete(`/departments/${id}`);
  },
};
