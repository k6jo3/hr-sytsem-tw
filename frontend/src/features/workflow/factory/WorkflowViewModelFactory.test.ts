import { describe, it, expect } from 'vitest';
import { WorkflowViewModelFactory } from './WorkflowViewModelFactory';
import type {
  ApprovalTaskDto,
  DelegationDto,
  WorkflowDefinitionDto,
  WorkflowInstanceDto,
} from '../api/WorkflowTypes';

describe('WorkflowViewModelFactory', () => {
  describe('createDefinitionViewModel', () => {
    it('應正確轉換啟用中的流程定義', () => {
      const dto: WorkflowDefinitionDto = {
        definition_id: 'def-001',
        flow_name: '請假審核流程',
        flow_type: 'LEAVE',
        nodes: [
          { node_id: 'start', node_type: 'START', node_name: '開始' },
          { node_id: 'n1', node_type: 'APPROVAL', node_name: '主管審核' },
          { node_id: 'end', node_type: 'END', node_name: '結束' },
        ],
        edges: [
          { edge_id: 'e1', source_node: 'start', target_node: 'n1' },
          { edge_id: 'e2', source_node: 'n1', target_node: 'end' },
        ],
        is_active: true,
        version: 2,
        created_at: '2026-01-15T10:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createDefinitionViewModel(dto);

      expect(vm.definitionId).toBe('def-001');
      expect(vm.flowName).toBe('請假審核流程');
      expect(vm.flowTypeLabel).toBe('請假流程');
      expect(vm.nodeCount).toBe(3);
      expect(vm.isActive).toBe(true);
      expect(vm.statusLabel).toBe('啟用中');
      expect(vm.statusColor).toBe('success');
      expect(vm.version).toBe(2);
      expect(vm.nodes).toHaveLength(3);
      expect(vm.nodes[1]!.nodeTypeLabel).toBe('審核');
      expect(vm.edges).toHaveLength(2);
    });

    it('應正確轉換停用的流程定義', () => {
      const dto: WorkflowDefinitionDto = {
        definition_id: 'def-002',
        flow_name: '費用審核流程',
        flow_type: 'EXPENSE',
        nodes: [],
        edges: [],
        is_active: false,
        version: 1,
        created_at: '2026-02-01T00:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createDefinitionViewModel(dto);

      expect(vm.statusLabel).toBe('已停用');
      expect(vm.statusColor).toBe('default');
      expect(vm.flowTypeLabel).toBe('費用流程');
      expect(vm.nodeCount).toBe(0);
    });
  });

  describe('createDefinitionList', () => {
    it('應批次轉換多筆流程定義', () => {
      const dtos: WorkflowDefinitionDto[] = [
        { definition_id: 'd1', flow_name: '流程A', flow_type: 'LEAVE', nodes: [], edges: [], is_active: true, version: 1, created_at: '' },
        { definition_id: 'd2', flow_name: '流程B', flow_type: 'OVERTIME', nodes: [], edges: [], is_active: false, version: 2, created_at: '' },
      ];

      const vms = WorkflowViewModelFactory.createDefinitionList(dtos);

      expect(vms).toHaveLength(2);
      expect(vms[0]!.definitionId).toBe('d1');
      expect(vms[1]!.flowTypeLabel).toBe('加班流程');
    });
  });

  describe('createInstanceViewModel', () => {
    it('應正確轉換執行中的流程實例', () => {
      const dto: WorkflowInstanceDto = {
        instance_id: 'inst-001',
        definition_id: 'def-001',
        flow_name: '請假審核流程',
        business_type: 'LEAVE',
        business_id: 'leave-001',
        applicant_id: 'emp-001',
        applicant_name: '王大明',
        current_node: 'n1',
        current_node_name: '主管審核',
        status: 'RUNNING',
        started_at: '2026-03-01T09:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createInstanceViewModel(dto);

      expect(vm.instanceId).toBe('inst-001');
      expect(vm.applicantName).toBe('王大明');
      expect(vm.businessTypeLabel).toBe('請假申請');
      expect(vm.statusLabel).toBe('審核中');
      expect(vm.statusColor).toBe('processing');
      expect(vm.isRunning).toBe(true);
      expect(vm.isCompleted).toBe(false);
      expect(vm.isRejected).toBe(false);
      expect(vm.duration).toBeUndefined();
    });

    it('應正確計算已完成流程的耗時', () => {
      const dto: WorkflowInstanceDto = {
        instance_id: 'inst-002',
        definition_id: 'def-001',
        business_type: 'LEAVE',
        business_id: 'leave-002',
        applicant_id: 'emp-002',
        current_node: 'end',
        status: 'COMPLETED',
        started_at: '2026-03-01T09:00:00Z',
        completed_at: '2026-03-03T15:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createInstanceViewModel(dto);

      expect(vm.statusLabel).toBe('已核准');
      expect(vm.statusColor).toBe('success');
      expect(vm.isCompleted).toBe(true);
      expect(vm.duration).toBe('2 天');
    });

    it('應正確轉換已駁回的流程', () => {
      const dto: WorkflowInstanceDto = {
        instance_id: 'inst-003',
        definition_id: 'def-001',
        business_type: 'OVERTIME',
        business_id: 'ot-001',
        applicant_id: 'emp-003',
        current_node: 'n1',
        status: 'REJECTED',
        started_at: '2026-03-01T09:00:00Z',
        completed_at: '2026-03-01T14:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createInstanceViewModel(dto);

      expect(vm.statusLabel).toBe('已駁回');
      expect(vm.statusColor).toBe('error');
      expect(vm.isRejected).toBe(true);
      expect(vm.businessTypeLabel).toBe('加班申請');
    });
  });

  describe('createTaskViewModel', () => {
    it('應正確轉換待審核任務', () => {
      const dto: ApprovalTaskDto = {
        task_id: 'task-001',
        instance_id: 'inst-001',
        flow_name: '請假審核流程',
        business_type: 'LEAVE',
        business_id: 'leave-001',
        business_summary: '特休假2天',
        node_id: 'n1',
        node_name: '主管審核',
        applicant_id: 'emp-001',
        applicant_name: '王大明',
        assignee_id: 'mgr-001',
        assignee_name: '李經理',
        status: 'PENDING',
        is_overdue: false,
        created_at: '2026-03-01T09:00:00Z',
        due_date: '2026-03-05T18:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createTaskViewModel(dto);

      expect(vm.taskId).toBe('task-001');
      expect(vm.businessTypeLabel).toBe('請假申請');
      expect(vm.applicantName).toBe('王大明');
      expect(vm.assigneeName).toBe('李經理');
      expect(vm.statusLabel).toBe('待審核');
      expect(vm.statusColor).toBe('warning');
      expect(vm.isPending).toBe(true);
      expect(vm.canApprove).toBe(true);
      expect(vm.canReject).toBe(true);
      expect(vm.isOverdue).toBe(false);
      expect(vm.urgencyColor).toBe('warning');
    });

    it('應正確標記逾期任務', () => {
      const dto: ApprovalTaskDto = {
        task_id: 'task-002',
        instance_id: 'inst-001',
        business_type: 'EXPENSE',
        business_id: 'exp-001',
        node_id: 'n1',
        applicant_id: 'emp-001',
        assignee_id: 'mgr-001',
        status: 'PENDING',
        is_overdue: true,
        created_at: '2026-02-20T09:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createTaskViewModel(dto);

      expect(vm.isOverdue).toBe(true);
      expect(vm.overdueLabel).toBe('已逾期');
      expect(vm.urgencyColor).toBe('error');
    });

    it('應正確轉換已核准任務（不可操作）', () => {
      const dto: ApprovalTaskDto = {
        task_id: 'task-003',
        instance_id: 'inst-001',
        business_type: 'LEAVE',
        business_id: 'leave-001',
        node_id: 'n1',
        applicant_id: 'emp-001',
        assignee_id: 'mgr-001',
        status: 'APPROVED',
        comments: '同意',
        is_overdue: false,
        created_at: '2026-03-01T09:00:00Z',
        completed_at: '2026-03-02T10:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createTaskViewModel(dto);

      expect(vm.statusLabel).toBe('已核准');
      expect(vm.isPending).toBe(false);
      expect(vm.canApprove).toBe(false);
      expect(vm.canReject).toBe(false);
      expect(vm.comments).toBe('同意');
    });
  });

  describe('createTaskSummary', () => {
    it('應正確計算任務統計', () => {
      const today = new Date().toISOString().split('T')[0];
      const tasks: ApprovalTaskDto[] = [
        { task_id: 't1', instance_id: 'i1', business_type: 'LEAVE', business_id: 'l1', node_id: 'n1', applicant_id: 'e1', assignee_id: 'm1', status: 'PENDING', is_overdue: true, created_at: '' },
        { task_id: 't2', instance_id: 'i2', business_type: 'LEAVE', business_id: 'l2', node_id: 'n1', applicant_id: 'e2', assignee_id: 'm1', status: 'PENDING', is_overdue: false, due_date: `${today}T18:00:00Z`, created_at: '' },
        { task_id: 't3', instance_id: 'i3', business_type: 'EXPENSE', business_id: 'x1', node_id: 'n1', applicant_id: 'e3', assignee_id: 'm1', status: 'PENDING', is_overdue: false, created_at: '' },
        { task_id: 't4', instance_id: 'i4', business_type: 'LEAVE', business_id: 'l3', node_id: 'n1', applicant_id: 'e4', assignee_id: 'm1', status: 'APPROVED', is_overdue: false, created_at: '' },
      ];

      const summary = WorkflowViewModelFactory.createTaskSummary(tasks);

      expect(summary.totalPending).toBe(3);
      expect(summary.overdueCount).toBe(1);
      expect(summary.dueTodayCount).toBe(1);
      expect(summary.normalCount).toBe(1);
    });
  });

  describe('createDelegationViewModel', () => {
    it('應正確轉換生效中的代理人設定', () => {
      const futureDate = new Date();
      futureDate.setMonth(futureDate.getMonth() + 1);

      const dto: DelegationDto = {
        delegation_id: 'del-001',
        delegator_id: 'emp-001',
        delegator_name: '王大明',
        delegatee_id: 'emp-002',
        delegatee_name: '李小美',
        start_date: '2026-03-01',
        end_date: futureDate.toISOString().split('T')[0]!,
        is_active: true,
        created_at: '2026-02-28T10:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createDelegationViewModel(dto);

      expect(vm.delegationId).toBe('del-001');
      expect(vm.delegatorName).toBe('王大明');
      expect(vm.delegateeName).toBe('李小美');
      expect(vm.statusLabel).toBe('生效中');
      expect(vm.statusColor).toBe('success');
      expect(vm.isActive).toBe(true);
      expect(vm.canDelete).toBe(true);
      expect(vm.dateRangeDisplay).toContain('~');
    });

    it('應正確轉換已結束的代理人設定（不可刪除）', () => {
      const dto: DelegationDto = {
        delegation_id: 'del-002',
        delegator_id: 'emp-001',
        delegatee_id: 'emp-003',
        start_date: '2025-01-01',
        end_date: '2025-01-31',
        is_active: false,
        created_at: '2024-12-31T10:00:00Z',
      };

      const vm = WorkflowViewModelFactory.createDelegationViewModel(dto);

      expect(vm.statusLabel).toBe('已結束');
      expect(vm.statusColor).toBe('default');
      expect(vm.canDelete).toBe(false);
    });
  });

  describe('createApplicationSummary', () => {
    it('應正確計算申請統計', () => {
      const instances: WorkflowInstanceDto[] = [
        { instance_id: 'i1', definition_id: 'd1', business_type: 'LEAVE', business_id: 'l1', applicant_id: 'e1', current_node: 'n1', status: 'RUNNING', started_at: '' },
        { instance_id: 'i2', definition_id: 'd1', business_type: 'LEAVE', business_id: 'l2', applicant_id: 'e1', current_node: 'end', status: 'COMPLETED', started_at: '' },
        { instance_id: 'i3', definition_id: 'd1', business_type: 'EXPENSE', business_id: 'x1', applicant_id: 'e1', current_node: 'n1', status: 'REJECTED', started_at: '' },
        { instance_id: 'i4', definition_id: 'd1', business_type: 'LEAVE', business_id: 'l3', applicant_id: 'e1', current_node: 'n1', status: 'RUNNING', started_at: '' },
      ];

      const summary = WorkflowViewModelFactory.createApplicationSummary(instances);

      expect(summary.totalApplications).toBe(4);
      expect(summary.runningCount).toBe(2);
      expect(summary.completedCount).toBe(1);
      expect(summary.rejectedCount).toBe(1);
    });
  });
});
