import { apiClient } from '@shared/api';
import type {
    CreateProjectRequest,
    CreateProjectResponse,
    CreateTaskRequest,
    GetCustomerListResponse,
    GetProjectDetailResponse,
    GetProjectListRequest,
    GetProjectListResponse,
    TaskDto,
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
    return apiClient.get<GetProjectListResponse>(this.BASE_PATH, { params });
  }

  /**
   * 取得專案詳情
   */
  static async getProjectDetail(id: string): Promise<GetProjectDetailResponse> {
    return apiClient.get<GetProjectDetailResponse>(`${this.BASE_PATH}/${id}`);
  }

  /**
   * 建立專案
   */
  static async createProject(request: CreateProjectRequest): Promise<CreateProjectResponse> {
    return apiClient.post<CreateProjectResponse>(this.BASE_PATH, request);
  }

  /**
   * 更新專案
   */
  static async updateProject(id: string, request: UpdateProjectRequest): Promise<void> {
    return apiClient.put<void>(`${this.BASE_PATH}/${id}`, request);
  }

  /**
   * 取得客戶列表
   */
  static async getCustomerList(): Promise<GetCustomerListResponse> {
    return apiClient.get<GetCustomerListResponse>('/projects/customers');
  }

  // ========== WBS / Task APIs ==========

  /**
   * GET /api/v1/projects/{id}/tasks - 取得專案工項
   */
  static async getProjectTasks(projectId: string): Promise<TaskDto[]> {
    return apiClient.get<TaskDto[]>(`${this.BASE_PATH}/${projectId}/tasks`);
  }

  /**
   * POST /api/v1/projects/{id}/tasks - 建立工項
   */
  static async createTask(projectId: string, request: CreateTaskRequest): Promise<TaskDto> {
    return apiClient.post<TaskDto>(`${this.BASE_PATH}/${projectId}/tasks`, request);
  }

  /**
   * PUT /api/v1/tasks/{id}/progress - 更新工項進度
   */
  static async updateTaskProgress(taskId: string, request: UpdateTaskProgressRequest): Promise<void> {
    return apiClient.put(`${this.BASE_PATH}/tasks/${taskId}/progress`, request);
  }
}
