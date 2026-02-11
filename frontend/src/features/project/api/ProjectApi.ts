import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
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
    ProjectMemberDto,
    TaskDto,
    UpdateCustomerRequest,
    UpdateProjectRequest,
    UpdateTaskProgressRequest,
} from './ProjectTypes';

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
    return apiClient.get<GetProjectListResponse>(this.BASE_PATH, { params });
  }

  /**
   * 取得專案詳情
   */
  static async getProjectDetail(id: string): Promise<GetProjectDetailResponse> {
    if (MockConfig.isEnabled('PROJECT')) {
      const project = await MockProjectApi.getProjectById(id);
      return { project };
    }
    return apiClient.get<GetProjectDetailResponse>(`${this.BASE_PATH}/${id}`);
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
    return apiClient.get<CustomerDto>(`/customers/${id}`);
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
    return apiClient.get<GetCustomerListResponse>('/customers', { params });
  }

  // ========== Member Management APIs ==========

  /**
   * 取得專案成員列表
   */
  static async getProjectMembers(projectId: string): Promise<ProjectMemberDto[]> {
    // Mock not implemented yet
    return apiClient.get<ProjectMemberDto[]>(`${this.BASE_PATH}/${projectId}/members`);
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
    // Mock not implemented yet
    return apiClient.get<ProjectCostDto>(`${this.BASE_PATH}/${projectId}/cost`);
  }

  // ========== WBS / Task APIs ==========

  /**
   * GET /api/v1/projects/{id}/tasks - 取得專案工項
   */
  static async getProjectTasks(projectId: string): Promise<TaskDto[]> {
    // Mock not implemented yet
    return apiClient.get<TaskDto[]>(`${this.BASE_PATH}/${projectId}/tasks`);
  }

  /**
   * POST /api/v1/projects/{id}/tasks - 建立工項
   */
  static async createTask(projectId: string, request: CreateTaskRequest): Promise<TaskDto> {
    // Mock not implemented yet
    return apiClient.post<TaskDto>(`${this.BASE_PATH}/${projectId}/tasks`, request);
  }

  /**
   * PUT /api/v1/tasks/{id}/progress - 更新工項進度
   */
  static async updateTaskProgress(taskId: string, request: UpdateTaskProgressRequest): Promise<void> {
    // Mock not implemented yet
    return apiClient.put(`/tasks/${taskId}/progress`, request);
  }
}
