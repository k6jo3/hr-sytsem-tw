import { MockConfig } from '../../../config/MockConfig';
import { apiClient } from '../../../shared/api/apiClient';
import { guardEnum } from '../../../shared/utils/adapterGuard';
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
/** 員工狀態允許值 */
const EMPLOYEE_STATUS_VALUES = ['ACTIVE', 'PROBATION', 'TERMINATED', 'ON_LEAVE', 'INACTIVE'] as const;

function adaptEmployeeItem(raw: any): EmployeeDto {
  const backendStatus = raw.employmentStatus ?? raw.status;

  // 後端詳細查詢回傳 department 為巢狀物件，列表查詢為扁平欄位
  const departmentId = raw.departmentId ?? raw.department_id ?? raw.department?.departmentId ?? '';
  const departmentName = raw.departmentName ?? raw.department_name ?? raw.department?.departmentName ?? '';

  return {
    id: raw.employeeId ?? raw.id,
    employee_number: raw.employeeNumber ?? raw.employee_number ?? '',
    full_name: raw.fullName ?? raw.full_name ?? '',
    email: raw.companyEmail ?? raw.email ?? '',
    phone: raw.mobilePhone ?? raw.phone,
    department_id: departmentId,
    department_name: departmentName,
    position: raw.jobTitle ?? raw.position ?? '',
    status: guardEnum('employee.status', backendStatus, EMPLOYEE_STATUS_VALUES, 'ACTIVE'),
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
    // 前端 page_size → 後端 size 欄位映射
    const backendParams: Record<string, any> = {};
    if (params) {
      if (params.page != null) backendParams.page = params.page;
      if (params.page_size != null) backendParams.size = params.page_size;
      if (params.search) backendParams.search = params.search;
      if (params.department_id) backendParams.departmentId = params.department_id;
      if (params.status) backendParams.status = params.status;
      if (params.sort_by) backendParams.sortBy = params.sort_by;
      if (params.sort_order) backendParams.sortOrder = params.sort_order;
    }
    const raw = await apiClient.get('/employees', { params: backendParams });
    return adaptEmployeeListResponse(raw);
  },

  /**
   * 取得員工詳細資料
   * 後端直接回傳 EmployeeDetailResponse（扁平結構），需適配為 { employee: EmployeeDto }
   */
  getEmployeeDetail: async (id: string): Promise<GetEmployeeDetailResponse> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.getEmployeeDetail(id);
    const raw = await apiClient.get<any>(`/employees/${id}`);
    return { employee: adaptEmployeeItem(raw) };
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
   * 離職員工（後端沒有 DELETE /employees/{id}，改為呼叫離職 API）
   * 修正：後端不支援刪除員工，僅支援離職處理
   */
  deleteEmployee: (id: string): Promise<void> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return MockOrganizationApi.deleteEmployee(id);
    return apiClient.post(`/employees/${id}/terminate`, {});
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
      taxId: o.taxId,
      address: o.address,
      phoneNumber: o.phone ?? o.phoneNumber,
      status: guardEnum('organization.status', o.status, ['ACTIVE', 'INACTIVE'] as const, 'ACTIVE'),
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
    return apiClient.put<OrganizationDto>(`/organizations/${id}`, {
      name: data.organizationName,
      address: data.address,
      phone: data.phoneNumber,
      taxId: data.taxId,
    });
  },

  /**
   * 停用組織
   */
  deactivateOrganization: (id: string): Promise<void> => {
    if (MockConfig.isEnabled('ORGANIZATION')) return Promise.resolve();
    return apiClient.put(`/organizations/${id}/deactivate`);
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
      parentOrganizationId: raw.parentId ?? raw.parentOrganizationId,
      taxId: raw.taxId,
      address: raw.address,
      phoneNumber: raw.phone ?? raw.phoneNumber,
      status: guardEnum('organization.status', raw.status, ['ACTIVE', 'INACTIVE'] as const, 'ACTIVE'),
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
      status: guardEnum('department.status', d.status, ['ACTIVE', 'INACTIVE'] as const, 'ACTIVE'),
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
