/**
 * Workflow ViewModel Factory (簽核流程 視圖模型工廠)
 * Domain Code: HR11
 */

import type {
  ApprovalTaskDto,
  BusinessType,
  DelegationDto,
  InstanceStatus,
  NodeType,
  TaskStatus,
  WorkflowDefinitionDto,
  WorkflowEdgeDto,
  WorkflowInstanceDto,
  WorkflowNodeDto,
} from '../api/WorkflowTypes';
import type {
  ApplicationSummaryViewModel,
  ApprovalTaskViewModel,
  DelegationViewModel,
  TaskSummaryViewModel,
  WorkflowDefinitionViewModel,
  WorkflowEdgeViewModel,
  WorkflowInstanceViewModel,
  WorkflowNodeViewModel,
} from '../model/WorkflowViewModel';

export class WorkflowViewModelFactory {
  // ========== Workflow Definition ==========

  static createDefinitionViewModel(dto: WorkflowDefinitionDto): WorkflowDefinitionViewModel {
    return {
      definitionId: dto.definition_id,
      flowName: dto.flow_name,
      flowType: dto.flow_type,
      flowTypeLabel: this.mapFlowTypeLabel(dto.flow_type),
      nodes: dto.nodes.map((node) => this.createNodeViewModel(node)),
      edges: dto.edges.map((edge) => this.createEdgeViewModel(edge)),
      nodeCount: dto.nodes.length,
      isActive: dto.is_active,
      statusLabel: dto.is_active ? '啟用中' : '已停用',
      statusColor: dto.is_active ? 'success' : 'default',
      version: dto.version,
      createdAt: dto.created_at,
      createdAtDisplay: this.formatDate(dto.created_at),
    };
  }

  static createDefinitionList(dtos: WorkflowDefinitionDto[]): WorkflowDefinitionViewModel[] {
    return dtos.map((dto) => this.createDefinitionViewModel(dto));
  }

  private static createNodeViewModel(dto: WorkflowNodeDto): WorkflowNodeViewModel {
    return {
      nodeId: dto.node_id,
      nodeType: dto.node_type,
      nodeTypeLabel: this.mapNodeTypeLabel(dto.node_type),
      nodeName: dto.node_name,
      assigneeType: dto.assignee_type,
      assigneeIds: dto.assignee_ids,
      condition: dto.condition,
    };
  }

  private static createEdgeViewModel(dto: WorkflowEdgeDto): WorkflowEdgeViewModel {
    return {
      edgeId: dto.edge_id,
      sourceNode: dto.source_node,
      targetNode: dto.target_node,
      condition: dto.condition,
    };
  }

  private static mapFlowTypeLabel(flowType: string): string {
    const labelMap: Record<string, string> = {
      LEAVE: '請假流程',
      OVERTIME: '加班流程',
      EXPENSE: '費用流程',
      RECRUITMENT: '招募流程',
      PURCHASE: '採購流程',
    };
    return labelMap[flowType] || flowType;
  }

  private static mapNodeTypeLabel(nodeType: NodeType): string {
    const labelMap: Record<NodeType, string> = {
      START: '開始',
      APPROVAL: '審核',
      CONDITION: '條件',
      PARALLEL: '會簽',
      END: '結束',
    };
    return labelMap[nodeType];
  }

  // ========== Workflow Instance ==========

  static createInstanceViewModel(dto: WorkflowInstanceDto): WorkflowInstanceViewModel {
    const duration = dto.completed_at
      ? this.calculateDuration(dto.started_at, dto.completed_at)
      : undefined;

    return {
      instanceId: dto.instance_id,
      definitionId: dto.definition_id,
      flowName: dto.flow_name || '-',
      businessType: dto.business_type,
      businessTypeLabel: this.mapBusinessTypeLabel(dto.business_type),
      businessId: dto.business_id,
      applicantId: dto.applicant_id,
      applicantName: dto.applicant_name || '-',
      currentNode: dto.current_node,
      currentNodeName: dto.current_node_name || dto.current_node,
      status: dto.status,
      statusLabel: this.mapInstanceStatusLabel(dto.status),
      statusColor: this.mapInstanceStatusColor(dto.status),
      startedAt: dto.started_at,
      startedAtDisplay: this.formatDateTime(dto.started_at),
      completedAt: dto.completed_at,
      completedAtDisplay: dto.completed_at ? this.formatDateTime(dto.completed_at) : undefined,
      isRunning: dto.status === 'RUNNING',
      isCompleted: dto.status === 'COMPLETED',
      isRejected: dto.status === 'REJECTED',
      duration,
    };
  }

  static createInstanceList(dtos: WorkflowInstanceDto[]): WorkflowInstanceViewModel[] {
    return dtos.map((dto) => this.createInstanceViewModel(dto));
  }

  private static mapBusinessTypeLabel(type: BusinessType): string {
    const labelMap: Record<BusinessType, string> = {
      LEAVE: '請假申請',
      OVERTIME: '加班申請',
      EXPENSE: '費用申請',
      RECRUITMENT: '招募申請',
      PURCHASE: '採購申請',
    };
    return labelMap[type];
  }

  private static mapInstanceStatusLabel(status: InstanceStatus): string {
    const labelMap: Record<InstanceStatus, string> = {
      RUNNING: '審核中',
      COMPLETED: '已核准',
      REJECTED: '已駁回',
      CANCELLED: '已取消',
    };
    return labelMap[status];
  }

  private static mapInstanceStatusColor(status: InstanceStatus): string {
    const colorMap: Record<InstanceStatus, string> = {
      RUNNING: 'processing',
      COMPLETED: 'success',
      REJECTED: 'error',
      CANCELLED: 'default',
    };
    return colorMap[status];
  }

  private static calculateDuration(start: string, end: string): string {
    const startDate = new Date(start);
    const endDate = new Date(end);
    const diffMs = endDate.getTime() - startDate.getTime();
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffHours / 24);

    if (diffDays > 0) {
      return `${diffDays} 天`;
    }
    return `${diffHours} 小時`;
  }

  // ========== Approval Task ==========

  static createTaskViewModel(dto: ApprovalTaskDto): ApprovalTaskViewModel {
    const isPending = dto.status === 'PENDING';
    const urgencyColor = dto.is_overdue ? 'error' : isPending ? 'warning' : 'default';

    return {
      taskId: dto.task_id,
      instanceId: dto.instance_id,
      flowName: dto.flow_name || '-',
      businessType: dto.business_type,
      businessTypeLabel: this.mapBusinessTypeLabel(dto.business_type),
      businessId: dto.business_id,
      businessSummary: dto.business_summary || '-',
      nodeId: dto.node_id,
      nodeName: dto.node_name || '-',
      applicantId: dto.applicant_id,
      applicantName: dto.applicant_name || '-',
      assigneeId: dto.assignee_id,
      assigneeName: dto.assignee_name || '-',
      delegatedTo: dto.delegated_to,
      delegatedToName: dto.delegated_to_name,
      status: dto.status,
      statusLabel: this.mapTaskStatusLabel(dto.status),
      statusColor: this.mapTaskStatusColor(dto.status),
      comments: dto.comments,
      dueDate: dto.due_date,
      dueDateDisplay: dto.due_date ? this.formatDate(dto.due_date) : undefined,
      isOverdue: dto.is_overdue,
      overdueLabel: dto.is_overdue ? '已逾期' : '-',
      createdAt: dto.created_at,
      createdAtDisplay: this.formatDateTime(dto.created_at),
      completedAt: dto.completed_at,
      completedAtDisplay: dto.completed_at ? this.formatDateTime(dto.completed_at) : undefined,
      isPending,
      canApprove: isPending,
      canReject: isPending,
      urgencyColor,
    };
  }

  static createTaskList(dtos: ApprovalTaskDto[]): ApprovalTaskViewModel[] {
    return dtos.map((dto) => this.createTaskViewModel(dto));
  }

  private static mapTaskStatusLabel(status: TaskStatus): string {
    const labelMap: Record<TaskStatus, string> = {
      PENDING: '待審核',
      APPROVED: '已核准',
      REJECTED: '已駁回',
    };
    return labelMap[status];
  }

  private static mapTaskStatusColor(status: TaskStatus): string {
    const colorMap: Record<TaskStatus, string> = {
      PENDING: 'warning',
      APPROVED: 'success',
      REJECTED: 'error',
    };
    return colorMap[status];
  }

  static createTaskSummary(tasks: ApprovalTaskDto[]): TaskSummaryViewModel {
    const today = new Date().toISOString().split('T')[0];
    let overdueCount = 0;
    let dueTodayCount = 0;
    let normalCount = 0;

    tasks.forEach((task) => {
      if (task.status === 'PENDING') {
        if (task.is_overdue) {
          overdueCount++;
        } else if (task.due_date && task.due_date.startsWith(today)) {
          dueTodayCount++;
        } else {
          normalCount++;
        }
      }
    });

    return {
      totalPending: tasks.filter((t) => t.status === 'PENDING').length,
      overdueCount,
      dueTodayCount,
      normalCount,
    };
  }

  // ========== Delegation ==========

  static createDelegationViewModel(dto: DelegationDto): DelegationViewModel {
    const today = new Date();
    const endDate = new Date(dto.end_date);
    const canDelete = dto.is_active && endDate >= today;

    return {
      delegationId: dto.delegation_id,
      delegatorId: dto.delegator_id,
      delegatorName: dto.delegator_name || '-',
      delegateeId: dto.delegatee_id,
      delegateeName: dto.delegatee_name || '-',
      startDate: dto.start_date,
      startDateDisplay: this.formatDate(dto.start_date),
      endDate: dto.end_date,
      endDateDisplay: this.formatDate(dto.end_date),
      isActive: dto.is_active,
      statusLabel: dto.is_active ? '生效中' : '已結束',
      statusColor: dto.is_active ? 'success' : 'default',
      createdAt: dto.created_at,
      dateRangeDisplay: `${this.formatDate(dto.start_date)} ~ ${this.formatDate(dto.end_date)}`,
      canDelete,
    };
  }

  static createDelegationList(dtos: DelegationDto[]): DelegationViewModel[] {
    return dtos.map((dto) => this.createDelegationViewModel(dto));
  }

  // ========== Application Summary ==========

  static createApplicationSummary(instances: WorkflowInstanceDto[]): ApplicationSummaryViewModel {
    return {
      totalApplications: instances.length,
      runningCount: instances.filter((i) => i.status === 'RUNNING').length,
      completedCount: instances.filter((i) => i.status === 'COMPLETED').length,
      rejectedCount: instances.filter((i) => i.status === 'REJECTED').length,
    };
  }

  // ========== Utility Methods ==========

  private static formatDate(isoString: string): string {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private static formatDateTime(isoString: string): string {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  }
}
