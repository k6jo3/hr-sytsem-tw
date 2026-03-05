import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { usePendingTasks, useMyApplications, useWorkflowDefinitions, useDelegations } from './useWorkflow';
import { WorkflowApi } from '../api';

vi.mock('../api', () => ({
  WorkflowApi: {
    getPendingTasks: vi.fn(),
    approveTask: vi.fn(),
    rejectTask: vi.fn(),
    getMyApplications: vi.fn(),
    getDefinitions: vi.fn(),
    getDelegations: vi.fn(),
    createDelegation: vi.fn(),
    deleteDelegation: vi.fn(),
  },
}));

const mockPendingTasksResponse = {
  data: [
    {
      task_id: 'task-001',
      instance_id: 'inst-001',
      flow_name: '請假審核',
      business_type: 'LEAVE' as const,
      business_id: 'leave-001',
      business_summary: '特休假2天',
      node_id: 'n1',
      node_name: '主管審核',
      applicant_id: 'emp-001',
      applicant_name: '王大明',
      assignee_id: 'mgr-001',
      assignee_name: '李經理',
      status: 'PENDING' as const,
      is_overdue: false,
      created_at: '2026-03-01T09:00:00Z',
    },
  ],
  total: 1,
  page: 1,
  page_size: 100,
};

describe('usePendingTasks', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應載入待辦任務並轉換為 ViewModel', async () => {
    vi.mocked(WorkflowApi.getPendingTasks).mockResolvedValue(mockPendingTasksResponse);

    const { result } = renderHook(() => usePendingTasks());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.tasks).toHaveLength(1);
    expect(result.current.tasks[0].taskId).toBe('task-001');
    expect(result.current.tasks[0].businessTypeLabel).toBe('請假申請');
    expect(result.current.summary).toBeTruthy();
    expect(result.current.summary?.totalPending).toBe(1);
  });

  it('應正確處理載入錯誤', async () => {
    vi.mocked(WorkflowApi.getPendingTasks).mockRejectedValue(new Error('網路錯誤'));

    const { result } = renderHook(() => usePendingTasks());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.error).toBe('網路錯誤');
    expect(result.current.tasks).toHaveLength(0);
  });

  it('應成功核准任務後重新載入', async () => {
    vi.mocked(WorkflowApi.getPendingTasks).mockResolvedValue(mockPendingTasksResponse);
    vi.mocked(WorkflowApi.approveTask).mockResolvedValue({ message: '核准成功' });

    const { result } = renderHook(() => usePendingTasks());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let approvalResult: { success: boolean; message: string };
    await act(async () => {
      approvalResult = await result.current.approveTask('task-001');
    });

    expect(approvalResult!.success).toBe(true);
    expect(approvalResult!.message).toBe('核准成功');
    expect(WorkflowApi.getPendingTasks).toHaveBeenCalledTimes(2);
  });

  it('應成功駁回任務後重新載入', async () => {
    vi.mocked(WorkflowApi.getPendingTasks).mockResolvedValue(mockPendingTasksResponse);
    vi.mocked(WorkflowApi.rejectTask).mockResolvedValue({ message: '駁回成功' });

    const { result } = renderHook(() => usePendingTasks());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let rejectResult: { success: boolean; message: string };
    await act(async () => {
      rejectResult = await result.current.rejectTask('task-001', { comments: '不符規定' });
    });

    expect(rejectResult!.success).toBe(true);
    expect(WorkflowApi.rejectTask).toHaveBeenCalledWith('task-001', { comments: '不符規定' });
  });
});

describe('useMyApplications', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應載入申請記錄並轉換為 ViewModel', async () => {
    vi.mocked(WorkflowApi.getMyApplications).mockResolvedValue({
      data: [
        {
          instance_id: 'inst-001',
          definition_id: 'def-001',
          flow_name: '請假審核流程',
          business_type: 'LEAVE' as const,
          business_id: 'leave-001',
          applicant_id: 'emp-001',
          applicant_name: '王大明',
          current_node: 'n1',
          current_node_name: '主管審核',
          status: 'RUNNING' as const,
          started_at: '2026-03-01T09:00:00Z',
        },
      ],
      total: 1,
      page: 1,
      page_size: 100,
    });

    const { result } = renderHook(() => useMyApplications());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.applications).toHaveLength(1);
    expect(result.current.applications[0].statusLabel).toBe('審核中');
    expect(result.current.applications[0].isRunning).toBe(true);
  });
});

describe('useWorkflowDefinitions', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應載入流程定義列表', async () => {
    vi.mocked(WorkflowApi.getDefinitions).mockResolvedValue({
      data: [
        {
          definition_id: 'def-001',
          flow_name: '請假審核流程',
          flow_type: 'LEAVE',
          nodes: [],
          edges: [],
          is_active: true,
          version: 1,
          created_at: '2026-01-01T00:00:00Z',
        },
      ],
      total: 1,
      page: 1,
      page_size: 100,
    });

    const { result } = renderHook(() => useWorkflowDefinitions());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.definitions).toHaveLength(1);
    expect(result.current.definitions[0].flowTypeLabel).toBe('請假流程');
  });
});

describe('useDelegations', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應載入代理人設定列表', async () => {
    vi.mocked(WorkflowApi.getDelegations).mockResolvedValue({
      data: [
        {
          delegation_id: 'del-001',
          delegator_id: 'emp-001',
          delegator_name: '王大明',
          delegatee_id: 'emp-002',
          delegatee_name: '李小美',
          start_date: '2026-03-01',
          end_date: '2026-03-15',
          is_active: true,
          created_at: '2026-02-28T10:00:00Z',
        },
      ],
    });

    const { result } = renderHook(() => useDelegations());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.delegations).toHaveLength(1);
    expect(result.current.delegations[0].delegateeName).toBe('李小美');
  });

  it('應成功新增代理人', async () => {
    vi.mocked(WorkflowApi.getDelegations).mockResolvedValue({ data: [] });
    vi.mocked(WorkflowApi.createDelegation).mockResolvedValue({
      delegation_id: 'del-new',
      message: '新增成功',
    });

    const { result } = renderHook(() => useDelegations());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let createResult: { success: boolean; message: string };
    await act(async () => {
      createResult = await result.current.createDelegation('emp-002', '2026-04-01', '2026-04-15');
    });

    expect(createResult!.success).toBe(true);
    expect(WorkflowApi.createDelegation).toHaveBeenCalledWith({
      delegatee_id: 'emp-002',
      start_date: '2026-04-01',
      end_date: '2026-04-15',
    });
  });

  it('應成功刪除代理人', async () => {
    vi.mocked(WorkflowApi.getDelegations).mockResolvedValue({ data: [] });
    vi.mocked(WorkflowApi.deleteDelegation).mockResolvedValue({ message: '刪除成功' });

    const { result } = renderHook(() => useDelegations());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let deleteResult: { success: boolean; message: string };
    await act(async () => {
      deleteResult = await result.current.deleteDelegation('del-001');
    });

    expect(deleteResult!.success).toBe(true);
    expect(WorkflowApi.deleteDelegation).toHaveBeenCalledWith('del-001');
  });
});
