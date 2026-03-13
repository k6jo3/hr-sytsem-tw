import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { guardEnum } from '../../../shared/utils/adapterGuard';
import { MockProjectApi } from './MockProjectApi';
import type {
    AddProjectMemberRequest,
    CreateCustomerRequest,
    CreateProjectRequest,
    CreateProjectResponse,
    CreateTaskRequest,
    CustomerDto,
    GetCustomerListRequest,
    GetCustomerListResponse,
    GetProjectDetailResponse,
    GetProjectListRequest,
    GetProjectListResponse,
    ProjectCostDto,
    ProjectDto,
    ProjectMemberDto,
    TaskDto,
    UpdateCustomerRequest,
    UpdateProjectRequest,
    UpdateTaskProgressRequest,
} from './ProjectTypes';

// ========== Response Adapters ==========
// 後端 camelCase → 前端 snake_case

/**
 * 後端 ProjectListItemResponse → 前端 ProjectDto（列表用）
 */
function adaptProjectListItem(raw: any): ProjectDto {
  return {
    id: raw.projectId,
    project_code: raw.projectCode ?? '',
    project_name: raw.projectName ?? '',
    customer_id: raw.customerId ?? '',
    customer_name: '', // 列表 API 不含客戶名稱
    project_type: raw.projectType ?? 'DEVELOPMENT',
    project_manager_id: raw.ownerId ?? '',
    project_manager_name: '', // 列表 API 不含 PM 名稱
    budget_type: raw.budgetType ?? 'FIXED_PRICE',
    budget_amount: raw.totalBudget ?? raw.budgetAmount ?? 0,
    budget_hours: raw.budgetHours ?? 0,
    actual_cost: raw.actualCost ?? 0,
    actual_hours: raw.actualHours ?? 0,
    progress: raw.progress ?? 0,
    status: guardEnum('project.status', raw.status, ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'] as const, 'PLANNING'),
    planned_start_date: raw.startDate ?? raw.plannedStartDate ?? '',
    planned_end_date: raw.endDate ?? raw.plannedEndDate ?? '',
    actual_start_date: raw.actualStartDate,
    actual_end_date: raw.actualEndDate,
    created_at: raw.createdAt ?? '',
    updated_at: raw.updatedAt ?? '',
  };
}

/**
 * 後端 GetProjectDetailResponse → 前端 ProjectDto（詳情用）
 */
function adaptProjectDetail(raw: any): ProjectDto {
  return {
    id: raw.projectId,
    project_code: raw.projectCode ?? '',
    project_name: raw.projectName ?? '',
    customer_id: raw.customerId ?? '',
    customer_name: '', // 詳情 API 不含客戶名稱
    project_type: raw.projectType ?? 'DEVELOPMENT',
    project_manager_id: '', // 詳情 API 不含 PM ID
    project_manager_name: '', // 詳情 API 不含 PM 名稱
    budget_type: raw.budgetType ?? 'FIXED_PRICE',
    budget_amount: raw.budgetAmount ?? 0,
    budget_hours: raw.budgetHours ?? 0,
    actual_cost: raw.actualCost ?? 0,
    actual_hours: raw.actualHours ?? 0,
    progress: raw.progress ?? 0,
    status: guardEnum('project.status', raw.status, ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'] as const, 'PLANNING'),
    planned_start_date: raw.plannedStartDate ?? '',
    planned_end_date: raw.plannedEndDate ?? '',
    actual_start_date: raw.actualStartDate,
    actual_end_date: raw.actualEndDate,
    created_at: raw.createdAt ?? '',
    updated_at: raw.updatedAt ?? '',
  };
}

/**
 * 後端 ProjectMemberDto → 前端 ProjectMemberDto
 */
function adaptProjectMember(raw: any, projectId?: string): ProjectMemberDto {
  return {
    member_id: raw.id ?? raw.memberId ?? '',
    project_id: projectId ?? raw.projectId ?? '',
    employee_id: raw.employeeId ?? '',
    employee_name: raw.employeeName ?? '',
    role: raw.role ?? '',
    allocated_hours: raw.allocatedHours ?? 0,
    actual_hours: raw.actualHours ?? 0,
    join_date: raw.joinDate ?? '',
  };
}

/**
 * 後端 CustomerListItemResponse → 前端 CustomerDto
 */
function adaptCustomerItem(raw: any): CustomerDto {
  return {
    id: raw.customerId,
    customer_code: raw.customerCode ?? '',
    customer_name: raw.customerName ?? '',
    tax_id: raw.taxId,
    industry: raw.industry,
    email: raw.email,
    phone_number: raw.phoneNumber,
    status: guardEnum('customer.status', raw.status, ['ACTIVE', 'INACTIVE'] as const, 'ACTIVE'),
    created_at: raw.createdAt ?? '',
  };
}

/**
 * 後端 TaskTreeNodeDto → 前端 TaskDto
 */
function adaptTaskItem(raw: any): TaskDto {
  return {
    id: raw.taskId ?? raw.id,
    project_id: raw.projectId ?? '',
    parent_task_id: raw.parentId ?? raw.parentTaskId,
    task_code: raw.taskCode ?? '',
    task_name: raw.taskName ?? '',
    level: raw.level ?? 1,
    estimated_hours: raw.estimatedHours ?? 0,
    actual_hours: raw.actualHours ?? 0,
    assignee_id: raw.assigneeId,
    assignee_name: raw.assigneeName,
    status: guardEnum('task.status', raw.status, ['NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD'] as const, 'NOT_STARTED'),
    progress: raw.progress ?? 0,
    display_order: raw.displayOrder ?? 0,
    children: raw.children?.map(adaptTaskItem),
    created_at: raw.createdAt ?? '',
    updated_at: raw.updatedAt ?? '',
  };
}

/**
 * Project API (專案管理 API)
 * Domain Code: HR06
 */
export class ProjectApi {
  private static readonly BASE_PATH = '/projects';

  /**
   * 取得專案列表
   */
  static async getProjectList(params: GetProjectListRequest): Promise<GetProjectListResponse> {
    if (MockConfig.isEnabled('PROJECT')) return MockProjectApi.getProjects(params);
    // 前端 1-indexed page + page_size → 後端 0-indexed page + size
    const backendParams: any = { ...params };
    if (backendParams.page != null) backendParams.page = backendParams.page - 1;
    if (backendParams.page_size != null) {
      backendParams.size = backendParams.page_size;
      delete backendParams.page_size;
    }
    const raw: any = await apiClient.get(this.BASE_PATH, { params: backendParams });
    const items = raw.items ?? [];
    return {
      projects: items.map(adaptProjectListItem),
      total: raw.total ?? raw.totalElements ?? items.length,
      page: (raw.page ?? 0) + 1, // 後端 0-indexed → 前端 1-indexed
      page_size: raw.size ?? 20,
    };
  }

  /**
   * 取得專案詳情
   */
  static async getProjectDetail(id: string): Promise<GetProjectDetailResponse> {
    if (MockConfig.isEnabled('PROJECT')) {
      const project = await MockProjectApi.getProjectById(id);
      return { project };
    }
    const raw: any = await apiClient.get(`${this.BASE_PATH}/${id}`);
    return { project: adaptProjectDetail(raw) };
  }

  /**
   * 建立專案
   */
  static async createProject(request: CreateProjectRequest): Promise<CreateProjectResponse> {
    if (MockConfig.isEnabled('PROJECT')) return MockProjectApi.createProject(request);
    return apiClient.post<CreateProjectResponse>(this.BASE_PATH, request);
  }

  /**
   * 更新專案
   */
  static async updateProject(id: string, request: UpdateProjectRequest): Promise<void> {
    if (MockConfig.isEnabled('PROJECT')) {
      await MockProjectApi.updateProject(id, request);
      return;
    }
    return apiClient.put<void>(`${this.BASE_PATH}/${id}`, request);
  }

  /**
   * 取得客戶詳情
   */
  static async getCustomerDetail(id: string): Promise<CustomerDto> {
    if (MockConfig.isEnabled('PROJECT')) return MockProjectApi.getCustomerById(id);
    const raw: any = await apiClient.get(`/customers/${id}`);
    return adaptCustomerItem(raw);
  }

  /**
   * 建立客戶
   */
  static async createCustomer(request: CreateCustomerRequest): Promise<void> {
    if (MockConfig.isEnabled('PROJECT')) {
      await MockProjectApi.createCustomer(request);
      return;
    }
    return apiClient.post('/customers', request);
  }

  /**
   * 更新客戶
   */
  static async updateCustomer(id: string, request: UpdateCustomerRequest): Promise<void> {
    if (MockConfig.isEnabled('PROJECT')) {
      await MockProjectApi.updateCustomer(id, request);
      return;
    }
    return apiClient.put(`/customers/${id}`, request);
  }

  /**
   * 取得客戶列表
   */
  static async getCustomerList(params?: GetCustomerListRequest): Promise<GetCustomerListResponse> {
    if (MockConfig.isEnabled('PROJECT')) return MockProjectApi.getCustomers(params);
    // 前端 page(1-indexed) + size → 後端 page(0-indexed) + size
    const backendParams: any = { ...(params ?? {}) };
    if (backendParams.page != null && backendParams.page > 0) {
      backendParams.page = backendParams.page - 1;
    } else {
      backendParams.page = 0;
    }
    const raw: any = await apiClient.get('/customers', { params: backendParams });
    const items = raw.items ?? [];
    return {
      customers: items.map(adaptCustomerItem),
      total: raw.total ?? raw.totalElements ?? items.length,
    };
  }

  // ========== Member Management APIs ==========

  /**
   * 取得專案成員列表
   */
  static async getProjectMembers(projectId: string): Promise<ProjectMemberDto[]> {
    if (MockConfig.isEnabled('PROJECT')) return [] as any;
    const raw: any = await apiClient.get(`${this.BASE_PATH}/${projectId}/members`);
    const items = Array.isArray(raw) ? raw : (raw.items ?? []);
    return items.map((m: any) => adaptProjectMember(m, projectId));
  }

  /**
   * 新增專案成員
   */
  static async addMember(projectId: string, request: AddProjectMemberRequest): Promise<void> {
    // Mock not implemented yet
    return apiClient.post(`${this.BASE_PATH}/${projectId}/members`, request);
  }

  /**
   * 移除專案成員
   */
  static async removeMember(projectId: string, employeeId: string): Promise<void> {
    // Mock not implemented yet
    return apiClient.delete(`${this.BASE_PATH}/${projectId}/members/${employeeId}`);
  }

  // ========== Cost Analysis APIs ==========

  /**
   * 取得專案成本分析
   */
  static async getProjectCost(projectId: string): Promise<ProjectCostDto> {
    if (MockConfig.isEnabled('PROJECT')) return { totalCost: 0, budget: 0, actualCost: 0 } as any;
    return apiClient.get<ProjectCostDto>(`${this.BASE_PATH}/${projectId}/cost`);
  }

  // ========== WBS / Task APIs ==========

  /**
   * GET /api/v1/projects/{id}/wbs - 取得專案工項（WBS 結構）
   */
  static async getProjectTasks(projectId: string): Promise<TaskDto[]> {
    if (MockConfig.isEnabled('PROJECT')) return [] as any;
    const raw: any = await apiClient.get(`${this.BASE_PATH}/${projectId}/wbs`);
    const nodes = raw.rootTasks ?? raw.nodes ?? raw.items ?? (Array.isArray(raw) ? raw : []);
    return nodes.map(adaptTaskItem);
  }

  /**
   * POST /api/v1/projects/{id}/tasks - 建立工項
   */
  static async createTask(projectId: string, request: CreateTaskRequest): Promise<TaskDto> {
    // Mock not implemented yet
    const raw: any = await apiClient.post(`${this.BASE_PATH}/${projectId}/tasks`, request);
    return adaptTaskItem(raw);
  }

  /**
   * PUT /api/v1/tasks/{id}/progress - 更新工項進度
   */
  static async updateTaskProgress(taskId: string, request: UpdateTaskProgressRequest): Promise<void> {
    // Mock not implemented yet
    return apiClient.put(`/tasks/${taskId}/progress`, request);
  }
}
