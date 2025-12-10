/**
 * Workflow DTOs (簽核流程 資料傳輸物件)
 * Domain Code: HR11
 */

// ========== Enums ==========

/**
 * 流程實例狀態
 */
export type InstanceStatus =
  | 'RUNNING' // 執行中
  | 'COMPLETED' // 已完成
  | 'REJECTED' // 已駁回
  | 'CANCELLED'; // 已取消

/**
 * 審核任務狀態
 */
export type TaskStatus =
  | 'PENDING' // 待審核
  | 'APPROVED' // 已核准
  | 'REJECTED'; // 已駁回

/**
 * 節點類型
 */
export type NodeType =
  | 'START' // 開始節點
  | 'APPROVAL' // 審核節點
  | 'CONDITION' // 條件分流
  | 'PARALLEL' // 平行會簽
  | 'END'; // 結束節點

/**
 * 業務類型
 */
export type BusinessType =
  | 'LEAVE' // 請假申請
  | 'OVERTIME' // 加班申請
  | 'EXPENSE' // 費用申請
  | 'RECRUITMENT' // 招募申請
  | 'PURCHASE'; // 採購申請

// ========== DTOs ==========

/**
 * 流程節點 DTO
 */
export interface WorkflowNodeDto {
  node_id: string;
  node_type: NodeType;
  node_name: string;
  assignee_type?: 'ROLE' | 'USER' | 'DEPARTMENT';
  assignee_ids?: string[];
  condition?: string;
}

/**
 * 流程連線 DTO
 */
export interface WorkflowEdgeDto {
  edge_id: string;
  source_node: string;
  target_node: string;
  condition?: string;
}

/**
 * 流程定義 DTO
 */
export interface WorkflowDefinitionDto {
  definition_id: string;
  flow_name: string;
  flow_type: string;
  nodes: WorkflowNodeDto[];
  edges: WorkflowEdgeDto[];
  is_active: boolean;
  version: number;
  created_at: string;
}

/**
 * 流程實例 DTO
 */
export interface WorkflowInstanceDto {
  instance_id: string;
  definition_id: string;
  flow_name?: string;
  business_type: BusinessType;
  business_id: string;
  applicant_id: string;
  applicant_name?: string;
  current_node: string;
  current_node_name?: string;
  status: InstanceStatus;
  started_at: string;
  completed_at?: string;
}

/**
 * 審核任務 DTO
 */
export interface ApprovalTaskDto {
  task_id: string;
  instance_id: string;
  flow_name?: string;
  business_type: BusinessType;
  business_id: string;
  business_summary?: string;
  node_id: string;
  node_name?: string;
  applicant_id: string;
  applicant_name?: string;
  assignee_id: string;
  assignee_name?: string;
  delegated_to?: string;
  delegated_to_name?: string;
  status: TaskStatus;
  comments?: string;
  due_date?: string;
  is_overdue: boolean;
  created_at: string;
  completed_at?: string;
}

/**
 * 代理人設定 DTO
 */
export interface DelegationDto {
  delegation_id: string;
  delegator_id: string;
  delegator_name?: string;
  delegatee_id: string;
  delegatee_name?: string;
  start_date: string;
  end_date: string;
  is_active: boolean;
  created_at: string;
}

// ========== Request/Response Types ==========

export interface GetWorkflowDefinitionsRequest {
  flow_type?: string;
  is_active?: boolean;
  page?: number;
  page_size?: number;
}

export interface GetWorkflowDefinitionsResponse {
  data: WorkflowDefinitionDto[];
  total: number;
  page: number;
  page_size: number;
}

export interface CreateWorkflowDefinitionRequest {
  flow_name: string;
  flow_type: string;
  nodes: WorkflowNodeDto[];
  edges: WorkflowEdgeDto[];
}

export interface CreateWorkflowDefinitionResponse {
  definition_id: string;
  message: string;
}

export interface StartWorkflowRequest {
  flow_type: string;
  business_type: BusinessType;
  business_id: string;
  variables?: Record<string, unknown>;
}

export interface StartWorkflowResponse {
  instance_id: string;
  message: string;
}

export interface GetWorkflowInstanceResponse {
  instance: WorkflowInstanceDto;
  tasks: ApprovalTaskDto[];
}

export interface GetPendingTasksRequest {
  page?: number;
  page_size?: number;
}

export interface GetPendingTasksResponse {
  data: ApprovalTaskDto[];
  total: number;
  page: number;
  page_size: number;
}

export interface ApproveTaskRequest {
  comments?: string;
}

export interface ApproveTaskResponse {
  message: string;
}

export interface RejectTaskRequest {
  comments: string;
}

export interface RejectTaskResponse {
  message: string;
}

export interface GetMyApplicationsRequest {
  status?: InstanceStatus;
  page?: number;
  page_size?: number;
}

export interface GetMyApplicationsResponse {
  data: WorkflowInstanceDto[];
  total: number;
  page: number;
  page_size: number;
}

export interface CreateDelegationRequest {
  delegatee_id: string;
  start_date: string;
  end_date: string;
}

export interface CreateDelegationResponse {
  delegation_id: string;
  message: string;
}

export interface GetDelegationsRequest {
  is_active?: boolean;
}

export interface GetDelegationsResponse {
  data: DelegationDto[];
}

export interface DeleteDelegationResponse {
  message: string;
}
