import { MockConfig } from '../../../config/MockConfig';
import { apiClient } from '../../../shared/api/apiClient';
import { MockOrganizationApi } from './MockOrganizationApi';
import type {
    DepartmentDto,
    DepartmentRequest,
    EmployeeDto,
    GetEmployeeDetailResponse,
    GetEmployeeListRequest,
    GetEmployeeListResponse,
    OrganizationDto,
    OrganizationRequest,
} from './OrganizationTypes';

/**
 * 將後端 camelCase 員工項目轉換為前端 snake_case EmployeeDto
 */
function adaptEmployeeItem(raw: any): EmployeeDto {
  const statusMap: Record<string, EmployeeDto['status']> = {
    ACTIVE: 'ACTIVE',
    PROBATION: 'ACTIVE',
    TERMINATED: 'TERMINATED',
    ON_LEAVE: 'ON_LEAVE',
    INACTIVE: 'INACTIVE',
  };
  const backendStatus = raw.employmentStatus ?? raw.status ?? 'ACTIVE';

  return {
    id: raw.employeeId ?? raw.id,
    employee_number: raw.employeeNumber ?? raw.employee_number ?? '',
    first_name: raw.fullName ?? raw.first_name ?? '',
    last_name: raw.last_name ?? '',
    email: raw.companyEmail ?? raw.email ?? '',
    phone: raw.phone,
    department_id: raw.departmentId ?? raw.department_id ?? '',
    department_name: raw.departmentName ?? raw.department_name ?? '',
    position: raw.jobTitle ?? raw.position ?? '',
    status: statusMap[backendStatus] ?? 'INACTIVE',
    hire_date: raw.hireDate ?? raw.hire_date ?? '',
    termination_date: raw.terminationDate ?? raw.termination_date,
    created_at: raw.createdAt ?? raw.created_at ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

/**
 * 將後端分頁回應轉換為前端 GetEmployeeListResponse
 */
function adaptEmployeeListResponse(raw: any): GetEmployeeListResponse {
  const items = raw.items ?? raw.content ?? raw.employees ?? [];
  return {
    employees: items.map(adaptEmployeeItem),
    total: raw.total ?? raw.totalElements ?? 0,
    page: raw.page ?? raw.number ?? 1,
    page_size: raw.size ?? raw.page_size ?? 20,
  };
}

/**
 * Organization API
 * 組織員工相關的 API 呼叫
 */
export const OrganizationApi = {
  // ========== 員工管理 API ==========

  /**
   * 取得員工列表
   */
  getEmployeeList: async (params?: GetEmployeeListRequest): Promise<GetEmployeeListResponse> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getEmployeeList(params);
    const raw = await apiClient.get('/employees', { params });
    return adaptEmployeeListResponse(raw);
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
  getOrganizations: async (): Promise<{ content: OrganizationDto[] }> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getOrganizations();
    const raw = await apiClient.get<any>('/organizations');
    const items = raw.content ?? raw.items ?? [];
    const content: OrganizationDto[] = items.map((o: any) => ({
      organizationId: o.organizationId ?? o.id,
      organizationCode: o.code ?? o.organizationCode ?? '',
      organizationName: o.name ?? o.organizationName ?? '',
      organizationType: (o.type ?? o.organizationType) === 'PARENT' ? 'PARENT' : 'SUBSIDIARY',
      parentOrganizationId: o.parentId ?? o.parentOrganizationId,
      status: o.status ?? 'ACTIVE',
      employeeCount: o.employeeCount ?? 0,
      createdAt: o.createdAt ?? '',
    }));
    return { content };
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
    return apiClient.post<OrganizationDto>('/organizations', {
      code: data.organizationCode,
      name: data.organizationName,
      type: data.organizationType,
      parentId: data.parentOrganizationId,
      taxId: data.taxId,
      phone: data.phoneNumber,
      address: data.address,
      establishedDate: data.establishedDate,
    });
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
  getOrganizationTree: async (id: string): Promise<{ data: OrganizationDto; departments: DepartmentDto[] }> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getOrganizationTree(id);
    const raw = await apiClient.get<any>(`/organizations/${id}/tree`);
    // 後端回傳扁平結構，需適配為 { data, departments }
    const orgData: OrganizationDto = {
      organizationId: raw.organizationId ?? raw.id,
      organizationCode: raw.code ?? raw.organizationCode ?? '',
      organizationName: raw.name ?? raw.organizationName ?? '',
      organizationType: raw.type === 'PARENT' ? 'PARENT' : 'SUBSIDIARY',
      status: raw.status ?? 'ACTIVE',
      employeeCount: raw.employeeCount ?? 0,
      createdAt: raw.createdAt ?? '',
    };
    const departments: DepartmentDto[] = (raw.departments ?? []).map((d: any) => ({
      departmentId: d.departmentId ?? d.id,
      code: d.code ?? d.departmentCode ?? '',
      name: d.name ?? d.departmentName ?? '',
      level: d.level ?? 1,
      sortOrder: d.sortOrder ?? d.displayOrder ?? 0,
      organizationId: d.organizationId ?? id,
      parentId: d.parentId ?? d.parentDepartmentId,
      managerId: d.managerId,
      managerName: d.managerName,
      status: d.status ?? 'ACTIVE',
      employeeCount: d.employeeCount ?? 0,
      subDepartments: d.subDepartments ?? d.children,
    }));
    return { data: orgData, departments };
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
