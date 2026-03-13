import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { guardEnum } from '../../../shared/utils/adapterGuard';
import { MockWorkflowApi } from '../../../shared/api/SupportModuleMockApis';
import type {
    ApproveTaskRequest,
    ApproveTaskResponse,
    ApprovalTaskDto,
    CreateDelegationRequest,
    CreateDelegationResponse,
    CreateWorkflowDefinitionRequest,
    CreateWorkflowDefinitionResponse,
    DelegationDto,
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
    WorkflowDefinitionDto,
    WorkflowInstanceDto,
} from './WorkflowTypes';

const BASE_URL = '/workflows';

// ========== Response Adapter ==========

/** 分頁參數轉換（前端 1-indexed → 後端 0-indexed） */
function adaptPageParams(params?: { page?: number; page_size?: number; [key: string]: any }) {
  if (!params) return params;
  const { page, page_size, ...rest } = params;
  return {
    ...rest,
    ...(page != null ? { page: page - 1 } : {}),
    ...(page_size != null ? { size: page_size } : {}),
  };
}

/** Spring Page → 前端分頁格式 */
function adaptPage<T>(raw: any, adaptFn: (item: any) => T): { data: T[]; total: number; page: number; page_size: number } {
  const content = raw.content ?? raw.data ?? (Array.isArray(raw) ? raw : []);
  return {
    data: content.map(adaptFn),
    total: raw.totalElements ?? raw.total ?? content.length,
    page: (raw.pageable?.pageNumber ?? raw.number ?? 0) + 1,
    page_size: raw.pageable?.pageSize ?? raw.size ?? content.length,
  };
}

/** 後端 camelCase → 前端 WorkflowDefinitionDto */
function adaptDefinition(raw: any): WorkflowDefinitionDto {
  let nodes = raw.nodes ?? raw.nodesJson ?? '[]';
  let edges = raw.edges ?? raw.edgesJson ?? '[]';
  if (typeof nodes === 'string') { try { nodes = JSON.parse(nodes); } catch { nodes = []; } }
  if (typeof edges === 'string') { try { edges = JSON.parse(edges); } catch { edges = []; } }
  return {
    definition_id: raw.definitionId ?? raw.definition_id ?? '',
    flow_name: raw.flowName ?? raw.flow_name ?? '',
    flow_type: raw.flowType ?? raw.flow_type ?? '',
    nodes,
    edges,
    is_active: raw.isActive ?? raw.is_active ?? false,
    version: raw.version ?? 0,
    created_at: raw.createdAt ?? raw.created_at ?? '',
  };
}

/** 後端 camelCase → 前端 WorkflowInstanceDto */
function adaptInstance(raw: any): WorkflowInstanceDto {
  return {
    instance_id: raw.instanceId ?? raw.instance_id ?? '',
    definition_id: raw.definitionId ?? raw.definition_id ?? '',
    flow_name: raw.flowName ?? raw.flow_name ?? '',
    business_type: raw.businessType ?? raw.business_type ?? '',
    business_id: raw.businessId ?? raw.business_id ?? '',
    applicant_id: raw.applicantId ?? raw.applicant_id ?? '',
    applicant_name: raw.applicantName ?? raw.applicant_name ?? '',
    current_node: raw.currentNodeId ?? raw.current_node ?? '',
    current_node_name: raw.currentNodeName ?? raw.current_node_name ?? '',
    status: guardEnum('workflowInstance.status', raw.status, ['RUNNING', 'COMPLETED', 'REJECTED', 'CANCELLED'] as const, 'RUNNING'),
    started_at: raw.startedAt ?? raw.started_at ?? '',
    completed_at: raw.completedAt ?? raw.completed_at,
  };
}

/** 後端 camelCase → 前端 ApprovalTaskDto */
function adaptTask(raw: any): ApprovalTaskDto {
  return {
    task_id: raw.taskId ?? raw.task_id ?? '',
    instance_id: raw.instanceId ?? raw.instance_id ?? '',
    flow_name: raw.flowName ?? raw.flow_name ?? '',
    business_type: raw.businessType ?? raw.business_type ?? '',
    business_id: raw.businessId ?? raw.business_id ?? '',
    business_summary: raw.businessSummary ?? raw.summary ?? raw.business_summary ?? '',
    node_id: raw.nodeId ?? raw.node_id ?? '',
    node_name: raw.nodeName ?? raw.taskName ?? raw.node_name ?? '',
    applicant_id: raw.applicantId ?? raw.applicant_id ?? '',
    applicant_name: raw.applicantName ?? raw.applicant_name ?? '',
    assignee_id: raw.assigneeId ?? raw.assignee_id ?? '',
    assignee_name: raw.assigneeName ?? raw.assignee_name ?? '',
    delegated_to: raw.delegatedToId ?? raw.delegated_to ?? '',
    delegated_to_name: raw.delegatedToName ?? raw.delegated_to_name ?? '',
    status: guardEnum('approvalTask.status', raw.status, ['PENDING', 'APPROVED', 'REJECTED'] as const, 'PENDING'),
    comments: raw.comments ?? '',
    due_date: raw.dueDate ?? raw.due_date ?? '',
    is_overdue: raw.isOverdue ?? raw.is_overdue ?? false,
    created_at: raw.createdAt ?? raw.created_at ?? '',
    completed_at: raw.completedAt ?? raw.approvedAt ?? raw.completed_at ?? '',
  };
}

/** 後端 camelCase → 前端 DelegationDto */
function adaptDelegation(raw: any): DelegationDto {
  return {
    delegation_id: raw.delegationId ?? raw.id ?? raw.delegation_id ?? '',
    delegator_id: raw.delegatorId ?? raw.delegator_id ?? '',
    delegator_name: raw.delegatorName ?? raw.delegator_name ?? '',
    delegatee_id: raw.delegateId ?? raw.delegateeId ?? raw.delegatee_id ?? '',
    delegatee_name: raw.delegateeName ?? raw.delegatee_name ?? '',
    start_date: raw.startDate ?? raw.start_date ?? '',
    end_date: raw.endDate ?? raw.end_date ?? '',
    is_active: raw.isActive ?? raw.is_active ?? (raw.status === 'ACTIVE') ?? false,
    created_at: raw.createdAt ?? raw.created_at ?? '',
  };
}

export const WorkflowApi = {
  // ========== Workflow Definitions ==========

  /**
   * 取得流程定義列表
   */
  getDefinitions: async (
    params?: GetWorkflowDefinitionsRequest
  ): Promise<GetWorkflowDefinitionsResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) {
       return MockWorkflowApi.getDefinitions();
    }
    const raw = await apiClient.get<any>(`${BASE_URL}/definitions`, { params: adaptPageParams(params) });
    return adaptPage(raw, adaptDefinition);
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
    const response = await apiClient.post<any>(`${BASE_URL}/definitions`, request);
    return { definition_id: response.definitionId ?? response.definition_id ?? '', message: response.message ?? '流程已建立' };
  },

  // ========== Workflow Instances ==========

  /**
   * 啟動流程
   */
  startWorkflow: async (request: StartWorkflowRequest): Promise<StartWorkflowResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return { instance_id: 'mock-instance-123', message: '流程已啟動 (Mock)' };
    const response = await apiClient.post<any>(`${BASE_URL}/start`, request);
    return { instance_id: response.instanceId ?? response.instance_id ?? '', message: response.message ?? '流程已啟動' };
  },

  /**
   * 取得流程實例詳情 (歷史)
   */
  getInstance: async (instanceId: string): Promise<GetWorkflowInstanceResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return MockWorkflowApi.getInstance(instanceId);
    const raw = await apiClient.get<any>(`${BASE_URL}/${instanceId}/history`);
    return {
      instance: adaptInstance(raw),
      tasks: (raw.timeline ?? raw.tasks ?? []).map(adaptTask),
    };
  },

  /**
   * 取得我的申請列表
   */
  getMyApplications: async (
    params?: GetMyApplicationsRequest
  ): Promise<GetMyApplicationsResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return MockWorkflowApi.getMyApplications();
    const raw = await apiClient.get<any>(`${BASE_URL}/my/applications`, { params: adaptPageParams(params) });
    return adaptPage(raw, adaptInstance);
  },

  // ========== Approval Tasks ==========

  /**
   * 取得待辦任務列表
   */
  getPendingTasks: async (params?: GetPendingTasksRequest): Promise<GetPendingTasksResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return MockWorkflowApi.getPendingTasks();
    const raw = await apiClient.get<any>(`${BASE_URL}/pending-tasks`, { params: adaptPageParams(params) });
    return adaptPage(raw, adaptTask);
  },

  /**
   * 核准任務
   */
  approveTask: async (taskId: string, request?: Partial<ApproveTaskRequest>): Promise<ApproveTaskResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return MockWorkflowApi.approveTask();
    const body: ApproveTaskRequest = { ...request, task_id: taskId };
    return apiClient.post<ApproveTaskResponse>(`${BASE_URL}/approve`, body);
  },

  /**
   * 駁回任務
   */
  rejectTask: async (taskId: string, request: Partial<RejectTaskRequest>): Promise<RejectTaskResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return MockWorkflowApi.rejectTask();
    const body: RejectTaskRequest = { ...request, task_id: taskId, comments: request.comments || '' };
    return apiClient.post<RejectTaskResponse>(`${BASE_URL}/reject`, body);
  },

  // ========== Delegations ==========

  /**
   * 建立代理人設定
   */
  createDelegation: async (
    request: CreateDelegationRequest
  ): Promise<CreateDelegationResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return MockWorkflowApi.createDelegation();
    const response = await apiClient.post<any>(`${BASE_URL}/delegations`, request);
    return { delegation_id: response.delegationId ?? response.delegation_id ?? '', message: response.message ?? '代理人已設定' };
  },

  /**
   * 取得代理人設定列表
   */
  getDelegations: async (params?: GetDelegationsRequest): Promise<GetDelegationsResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return MockWorkflowApi.getDelegations();
    const raw = await apiClient.get<any>(`${BASE_URL}/delegations`, { params });
    const items = raw.content ?? raw.data ?? (Array.isArray(raw) ? raw : []);
    return { data: items.map(adaptDelegation) };
  },

  /**
   * 刪除代理人設定
   */
  deleteDelegation: async (delegationId: string): Promise<DeleteDelegationResponse> => {
    if (MockConfig.isEnabled('WORKFLOW')) return MockWorkflowApi.deleteDelegation();
    const response = await apiClient.delete<DeleteDelegationResponse>(
      `${BASE_URL}/delegations/${delegationId}`
    );
    return response;
  },
};
