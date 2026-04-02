import { useCallback, useState } from 'react';
import { ProjectApi } from '../api/ProjectApi';
import { ProjectViewModelFactory } from '../factory/ProjectViewModelFactory';
import type { ProjectDetailViewModel, TaskViewModel } from '../model/ProjectViewModel';

/**
 * Project Hook (專案管理 Hook)
 * 處理單一專案詳情、工項與成員相關業務
 */
export const useProject = (projectId?: string) => {
  const [project, setProject] = useState<ProjectDetailViewModel | null>(null);
  const [tasks, setTasks] = useState<TaskViewModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchProject = useCallback(async (id: string = projectId!) => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const response = await ProjectApi.getProjectDetail(id);
      const viewModel = ProjectViewModelFactory.createDetailFromDTO(response.project);
      setProject(viewModel);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得專案詳情');
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  const fetchTasks = useCallback(async (id: string = projectId!) => {
    if (!id) return;
    setLoading(true);
    try {
      const dtos = await ProjectApi.getProjectTasks(id);
      const viewModels = dtos.map(dto => ProjectViewModelFactory.createTaskViewModel(dto));
      setTasks(viewModels);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得工項資料');
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  const createTask = async (id: string, request: any) => {
    setLoading(true);
    try {
      await ProjectApi.createTask(id, request);
      await fetchTasks(id);
      return true;
    } catch (err: any) {
      setError(err.message || '建立工項失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const updateTaskProgress = async (taskId: string, progress: number, status?: any) => {
    // 修正：updateTaskProgress 需要 projectId，從 hook 參數取得
    if (!projectId) {
      setError('缺少 projectId，無法更新工項進度');
      return false;
    }
    setLoading(true);
    try {
      await ProjectApi.updateTaskProgress(projectId, taskId, { progress, status });
      await fetchTasks(projectId);
      return true;
    } catch (err: any) {
      setError(err.message || '更新進度失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    project,
    tasks,
    loading,
    error,
    fetchProject,
    fetchTasks,
    createTask,
    updateTaskProgress,
    refreshTasks: fetchTasks,
  };
};
