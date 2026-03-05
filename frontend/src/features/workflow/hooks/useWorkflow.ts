/**
 * Workflow Hooks (簽核流程 Hooks)
 * Domain Code: HR11
 */

import { useCallback, useEffect, useState } from 'react';
import { WorkflowApi } from '../api';
import type { ApproveTaskRequest, RejectTaskRequest } from '../api/WorkflowTypes';
import { WorkflowViewModelFactory } from '../factory/WorkflowViewModelFactory';
import type {
    ApprovalTaskViewModel,
    DelegationViewModel,
    TaskSummaryViewModel,
    WorkflowDefinitionViewModel,
    WorkflowInstanceViewModel,
} from '../model/WorkflowViewModel';

/**
 * 待辦任務 Hook
 */
export const usePendingTasks = () => {
  const [tasks, setTasks] = useState<ApprovalTaskViewModel[]>([]);
  const [summary, setSummary] = useState<TaskSummaryViewModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchTasks = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await WorkflowApi.getPendingTasks({ page: 1, page_size: 100 });
      const taskViewModels = WorkflowViewModelFactory.createTaskList(response.data);
      const taskSummary = WorkflowViewModelFactory.createTaskSummary(response.data);
      setTasks(taskViewModels);
      setSummary(taskSummary);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '載入待辦任務失敗';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  const approveTask = useCallback(
    async (taskId: string, request?: Partial<ApproveTaskRequest>) => {
      try {
        await WorkflowApi.approveTask(taskId, request);
        await fetchTasks();
        return { success: true, message: '核准成功' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '核准失敗';
        return { success: false, message: errorMessage };
      }
    },
    [fetchTasks]
  );

  const rejectTask = useCallback(
    async (taskId: string, request: Partial<RejectTaskRequest>) => {
      try {
        await WorkflowApi.rejectTask(taskId, request);
        await fetchTasks();
        return { success: true, message: '駁回成功' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '駁回失敗';
        return { success: false, message: errorMessage };
      }
    },
    [fetchTasks]
  );

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  return {
    tasks,
    summary,
    loading,
    error,
    refresh: fetchTasks,
    approveTask,
    rejectTask,
  };
};

/**
 * 我的申請 Hook
 */
export const useMyApplications = () => {
  const [applications, setApplications] = useState<WorkflowInstanceViewModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchApplications = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await WorkflowApi.getMyApplications({ page: 1, page_size: 100 });
      const viewModels = WorkflowViewModelFactory.createInstanceList(response.data);
      setApplications(viewModels);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '載入申請記錄失敗';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchApplications();
  }, [fetchApplications]);

  return {
    applications,
    loading,
    error,
    refresh: fetchApplications,
  };
};

/**
 * 流程定義管理 Hook
 */
export const useWorkflowDefinitions = () => {
  const [definitions, setDefinitions] = useState<WorkflowDefinitionViewModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDefinitions = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await WorkflowApi.getDefinitions({ page: 1, page_size: 100 });
      const viewModels = WorkflowViewModelFactory.createDefinitionList(response.data);
      setDefinitions(viewModels);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '載入流程定義失敗';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDefinitions();
  }, [fetchDefinitions]);

  return {
    definitions,
    loading,
    error,
    refresh: fetchDefinitions,
  };
};

/**
 * 代理人設定 Hook
 */
export const useDelegations = () => {
  const [delegations, setDelegations] = useState<DelegationViewModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchDelegations = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await WorkflowApi.getDelegations();
      const viewModels = WorkflowViewModelFactory.createDelegationList(response.data);
      setDelegations(viewModels);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '載入代理人設定失敗';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  const createDelegation = useCallback(
    async (delegateeId: string, startDate: string, endDate: string) => {
      setSubmitting(true);
      try {
        await WorkflowApi.createDelegation({
          delegatee_id: delegateeId,
          start_date: startDate,
          end_date: endDate,
        });
        await fetchDelegations();
        return { success: true, message: '新增代理人成功' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '新增代理人失敗';
        return { success: false, message: errorMessage };
      } finally {
        setSubmitting(false);
      }
    },
    [fetchDelegations]
  );

  const deleteDelegation = useCallback(
    async (delegationId: string) => {
      setSubmitting(true);
      try {
        await WorkflowApi.deleteDelegation(delegationId);
        await fetchDelegations();
        return { success: true, message: '刪除代理人成功' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '刪除代理人失敗';
        return { success: false, message: errorMessage };
      } finally {
        setSubmitting(false);
      }
    },
    [fetchDelegations]
  );

  useEffect(() => {
    fetchDelegations();
  }, [fetchDelegations]);

  return {
    delegations,
    loading,
    error,
    submitting,
    refresh: fetchDelegations,
    createDelegation,
    deleteDelegation,
  };
};

/**
 * 通用 Workflow Hook (保持向後兼容)
 */
export const useWorkflow = () => {
  const [loading] = useState(false);
  const [error] = useState<string | null>(null);

  return {
    loading,
    error,
  };
};
