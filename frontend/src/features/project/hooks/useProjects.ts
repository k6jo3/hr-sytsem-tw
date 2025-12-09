import { useState, useCallback, useEffect } from 'react';
import { ProjectApi } from '../api/ProjectApi';
import { ProjectViewModelFactory } from '../factory/ProjectViewModelFactory';
import type { ProjectViewModel } from '../model/ProjectViewModel';
import type { GetProjectListRequest, ProjectStatus, ProjectType } from '../api/ProjectTypes';

/**
 * 專案列表Hook
 */
export const useProjects = () => {
  const [projects, setProjects] = useState<ProjectViewModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // 篩選條件
  const [filters, setFilters] = useState<{
    keyword?: string;
    customerId?: string;
    status?: ProjectStatus;
    projectType?: ProjectType;
  }>({});

  const fetchProjects = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const request: GetProjectListRequest = {
        page,
        page_size: pageSize,
        ...filters,
      };

      const response = await ProjectApi.getProjectList(request);
      const viewModels = ProjectViewModelFactory.createListFromDTOs(response.projects);

      setProjects(viewModels);
      setTotal(response.total);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得專案列表');
      setProjects([]);
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, filters]);

  useEffect(() => {
    fetchProjects();
  }, [fetchProjects]);

  const handlePageChange = useCallback((newPage: number, newPageSize?: number) => {
    setPage(newPage);
    if (newPageSize) {
      setPageSize(newPageSize);
    }
  }, []);

  const handleFilterChange = useCallback((newFilters: typeof filters) => {
    setFilters(newFilters);
    setPage(1); // 重置到第一頁
  }, []);

  const refresh = useCallback(() => {
    fetchProjects();
  }, [fetchProjects]);

  return {
    projects,
    loading,
    error,
    total,
    page,
    pageSize,
    filters,
    handlePageChange,
    handleFilterChange,
    refresh,
  };
};
