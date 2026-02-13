import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { MockWorkflowApi } from '../../../shared/api/SupportModuleMockApis';
import type {
    ApproveTaskRequest,
    ApproveTaskResponse,
    CreateDelegationRequest,
    CreateDelegationResponse,
    CreateWorkflowDefinitionRequest,
    CreateWorkflowDefinitionResponse,
    DeleteDelegationResponse,
    GetDelegationsRequest,
    GetDelegationsResponse,
    GetMyApplicationsRequest,
    GetMyApplicationsResponse,
    GetPendingTasksRequest,
    GetPendingTasksResponse,
    GetWorkflowDefinitionsRequest,
    GetWorkflowDefinitionsResponse,
    GetWorkflowInstanceResponse,
    RejectTaskRequest,
    RejectTaskResponse,
    StartWorkflowRequest,
    StartWorkflowResponse,
} from './WorkflowTypes';

const BASE_URL = '/workflow';

export const WorkflowApi = {
  // ========== Workflow Definitions ==========

  /**
   * 取得流程定義列表
   */
  getDefinitions: async (
    params?: GetWorkflowDefinitionsRequest
  ): Promise<GetWorkflowDefinitionsResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) {
       const res = await MockWorkflowApi.getWorkflows();
       return { data: res.workflows, total: res.total, page: 1, page_size: 10 };
    }
    const response = await apiClient.get<GetWorkflowDefinitionsResponse>(
      `${BASE_URL}/definitions`,
      { params }
    );
    return response;
  },

  /**
   * 建立流程定義
   */
  createDefinition: async (
    request: CreateWorkflowDefinitionRequest
  ): Promise<CreateWorkflowDefinitionResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) {
      const res = await MockWorkflowApi.createWorkflow(request);
      return { definition_id: res.workflow_id, message: res.message };
    }
    const response = await apiClient.post<CreateWorkflowDefinitionResponse>(
      `${BASE_URL}/definitions`,
      request
    );
    return response;
  },

  // ========== Workflow Instances ==========

  /**
   * 啟動流程
   */
  startWorkflow: async (request: StartWorkflowRequest): Promise<StartWorkflowResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { instance_id: 'mock-instance-123', message: '流程已啟動 (Mock)' };
    const response = await apiClient.post<StartWorkflowResponse>(`${BASE_URL}/start`, request);
    return response;
  },

  /**
   * 取得流程實例詳情 (歷史)
   */
  getInstance: async (instanceId: string): Promise<GetWorkflowInstanceResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { instanceId } as any;
    const response = await apiClient.get<GetWorkflowInstanceResponse>(
      `${BASE_URL}/${instanceId}/history`
    );
    return response;
  },

  /**
   * 取得我的申請列表
   */
  getMyApplications: async (
    params?: GetMyApplicationsRequest
  ): Promise<GetMyApplicationsResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { data: [], total: 0, page: 1, page_size: 10 };
    const response = await apiClient.get<GetMyApplicationsResponse>(`${BASE_URL}/my/applications`, {
      params,
    });
    return response;
  },

  // ========== Approval Tasks ==========

  /**
   * 取得待辦任務列表
   */
  getPendingTasks: async (params?: GetPendingTasksRequest): Promise<GetPendingTasksResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { data: [], total: 0, page: 1, page_size: 10 };
    const response = await apiClient.get<GetPendingTasksResponse>(`${BASE_URL}/pending-tasks`, {
      params,
    });
    return response;
  },

  /**
   * 核准任務
   */
  approveTask: async (taskId: string, request?: Partial<ApproveTaskRequest>): Promise<ApproveTaskResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { message: '任務已核准 (Mock)' };
    const body: ApproveTaskRequest = {
      ...request,
      task_id: taskId
    };
    
    return apiClient.post<ApproveTaskResponse>(
      `${BASE_URL}/approve`,
      body
    );
  },

  /**
   * 駁回任務
   */
  rejectTask: async (taskId: string, request: Partial<RejectTaskRequest>): Promise<RejectTaskResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { message: '任務已駁回 (Mock)' };
    const body: RejectTaskRequest = { 
      ...request, 
      task_id: taskId,
      comments: request.comments || ''
    };
    return apiClient.post<RejectTaskResponse>(
      `${BASE_URL}/reject`,
      body
    );
  },

  // ========== Delegations ==========
  
  /**
   * 建立代理人設定
   */
  createDelegation: async (
    request: CreateDelegationRequest
  ): Promise<CreateDelegationResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { success: true } as any;
    const response = await apiClient.post<CreateDelegationResponse>(
      `${BASE_URL}/delegations`,
      request
    );
    return response;
  },

  /**
   * 取得代理人設定列表
   */
  getDelegations: async (params?: GetDelegationsRequest): Promise<GetDelegationsResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { data: [] };
    const response = await apiClient.get<GetDelegationsResponse>(`${BASE_URL}/delegations`, {
      params,
    });
    return response;
  },

  /**
   * 刪除代理人設定
   */
  deleteDelegation: async (delegationId: string): Promise<DeleteDelegationResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { message: '代理人設定已刪除 (Mock)' };
    const response = await apiClient.delete<DeleteDelegationResponse>(
      `${BASE_URL}/delegations/${delegationId}`
    );
    return response;
  },
};
