/**
 * Workflow API (簽核流程 API)
 * Domain Code: HR11
 */

import { apiClient } from '@shared/api';
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

const BASE_URL = '/workflows';

export const WorkflowApi = {
  // ========== Workflow Definitions ==========

  /**
   * 取得流程定義列表
   */
  getDefinitions: async (
    params?: GetWorkflowDefinitionsRequest
  ): Promise<GetWorkflowDefinitionsResponse> => {
    const response = await apiClient.get<GetWorkflowDefinitionsResponse>(
      `${BASE_URL}/definitions`,
      { params }
    );
    return response.data;
  },

  /**
   * 建立流程定義
   */
  createDefinition: async (
    request: CreateWorkflowDefinitionRequest
  ): Promise<CreateWorkflowDefinitionResponse> => {
    const response = await apiClient.post<CreateWorkflowDefinitionResponse>(
      `${BASE_URL}/definitions`,
      request
    );
    return response.data;
  },

  // ========== Workflow Instances ==========

  /**
   * 啟動流程
   */
  startWorkflow: async (request: StartWorkflowRequest): Promise<StartWorkflowResponse> => {
    const response = await apiClient.post<StartWorkflowResponse>(`${BASE_URL}/start`, request);
    return response.data;
  },

  /**
   * 取得流程實例詳情
   */
  getInstance: async (instanceId: string): Promise<GetWorkflowInstanceResponse> => {
    const response = await apiClient.get<GetWorkflowInstanceResponse>(
      `${BASE_URL}/instances/${instanceId}`
    );
    return response.data;
  },

  /**
   * 取得我的申請列表
   */
  getMyApplications: async (
    params?: GetMyApplicationsRequest
  ): Promise<GetMyApplicationsResponse> => {
    const response = await apiClient.get<GetMyApplicationsResponse>(`${BASE_URL}/my/applications`, {
      params,
    });
    return response.data;
  },

  // ========== Approval Tasks ==========

  /**
   * 取得待辦任務列表
   */
  getPendingTasks: async (params?: GetPendingTasksRequest): Promise<GetPendingTasksResponse> => {
    const response = await apiClient.get<GetPendingTasksResponse>(`${BASE_URL}/tasks/pending`, {
      params,
    });
    return response.data;
  },

  /**
   * 核准任務
   */
  approveTask: async (taskId: string, request?: ApproveTaskRequest): Promise<ApproveTaskResponse> => {
    const response = await apiClient.put<ApproveTaskResponse>(
      `${BASE_URL}/tasks/${taskId}/approve`,
      request
    );
    return response.data;
  },

  /**
   * 駁回任務
   */
  rejectTask: async (taskId: string, request: RejectTaskRequest): Promise<RejectTaskResponse> => {
    const response = await apiClient.put<RejectTaskResponse>(
      `${BASE_URL}/tasks/${taskId}/reject`,
      request
    );
    return response.data;
  },

  // ========== Delegations ==========

  /**
   * 建立代理人設定
   */
  createDelegation: async (
    request: CreateDelegationRequest
  ): Promise<CreateDelegationResponse> => {
    const response = await apiClient.post<CreateDelegationResponse>(
      `${BASE_URL}/delegations`,
      request
    );
    return response.data;
  },

  /**
   * 取得代理人設定列表
   */
  getDelegations: async (params?: GetDelegationsRequest): Promise<GetDelegationsResponse> => {
    const response = await apiClient.get<GetDelegationsResponse>(`${BASE_URL}/delegations`, {
      params,
    });
    return response.data;
  },

  /**
   * 刪除代理人設定
   */
  deleteDelegation: async (delegationId: string): Promise<DeleteDelegationResponse> => {
    const response = await apiClient.delete<DeleteDelegationResponse>(
      `${BASE_URL}/delegations/${delegationId}`
    );
    return response.data;
  },
};
