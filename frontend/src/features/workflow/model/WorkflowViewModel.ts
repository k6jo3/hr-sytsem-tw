/**
 * Workflow ViewModels (簽核流程 前端視圖模型)
 * Domain Code: HR11
 */

import type { BusinessType, InstanceStatus, NodeType, TaskStatus } from '../api/WorkflowTypes';

/**
 * 流程節點 ViewModel
 */
export interface WorkflowNodeViewModel {
  nodeId: string;
  nodeType: NodeType;
  nodeTypeLabel: string;
  nodeName: string;
  assigneeType?: string;
  assigneeIds?: string[];
  condition?: string;
}

/**
 * 流程連線 ViewModel
 */
export interface WorkflowEdgeViewModel {
  edgeId: string;
  sourceNode: string;
  targetNode: string;
  condition?: string;
}

/**
 * 流程定義 ViewModel
 */
export interface WorkflowDefinitionViewModel {
  definitionId: string;
  flowName: string;
  flowType: string;
  flowTypeLabel: string;
  nodes: WorkflowNodeViewModel[];
  edges: WorkflowEdgeViewModel[];
  nodeCount: number;
  isActive: boolean;
  statusLabel: string;
  statusColor: string;
  version: number;
  createdAt: string;
  createdAtDisplay: string;
}

/**
 * 流程實例 ViewModel
 */
export interface WorkflowInstanceViewModel {
  instanceId: string;
  definitionId: string;
  flowName: string;
  businessType: BusinessType;
  businessTypeLabel: string;
  businessId: string;
  applicantId: string;
  applicantName: string;
  currentNode: string;
  currentNodeName: string;
  status: InstanceStatus;
  statusLabel: string;
  statusColor: string;
  startedAt: string;
  startedAtDisplay: string;
  completedAt?: string;
  completedAtDisplay?: string;
  isRunning: boolean;
  isCompleted: boolean;
  isRejected: boolean;
  duration?: string;
}

/**
 * 審核任務 ViewModel
 */
export interface ApprovalTaskViewModel {
  taskId: string;
  instanceId: string;
  flowName: string;
  businessType: BusinessType;
  businessTypeLabel: string;
  businessId: string;
  businessSummary: string;
  nodeId: string;
  nodeName: string;
  applicantId: string;
  applicantName: string;
  assigneeId: string;
  assigneeName: string;
  delegatedTo?: string;
  delegatedToName?: string;
  status: TaskStatus;
  statusLabel: string;
  statusColor: string;
  comments?: string;
  dueDate?: string;
  dueDateDisplay?: string;
  isOverdue: boolean;
  overdueLabel: string;
  createdAt: string;
  createdAtDisplay: string;
  completedAt?: string;
  completedAtDisplay?: string;
  isPending: boolean;
  canApprove: boolean;
  canReject: boolean;
  urgencyColor: string;
}

/**
 * 代理人設定 ViewModel
 */
export interface DelegationViewModel {
  delegationId: string;
  delegatorId: string;
  delegatorName: string;
  delegateeId: string;
  delegateeName: string;
  startDate: string;
  startDateDisplay: string;
  endDate: string;
  endDateDisplay: string;
  isActive: boolean;
  statusLabel: string;
  statusColor: string;
  createdAt: string;
  dateRangeDisplay: string;
  canDelete: boolean;
}

/**
 * 待辦任務統計 ViewModel
 */
export interface TaskSummaryViewModel {
  totalPending: number;
  overdueCount: number;
  dueTodayCount: number;
  normalCount: number;
}

/**
 * 我的申請統計 ViewModel
 */
export interface ApplicationSummaryViewModel {
  totalApplications: number;
  runningCount: number;
  completedCount: number;
  rejectedCount: number;
}
