import { useCallback, useEffect, useState } from 'react';
import { RecruitmentApi } from '../api';
import type { CandidateStatus } from '../api/RecruitmentTypes';
import { RecruitmentViewModelFactory } from '../factory/RecruitmentViewModelFactory';
import type { RecruitmentKanbanViewModel } from '../model/RecruitmentViewModel';

export const useCandidatesKanban = () => {
  const [kanban, setKanban] = useState<RecruitmentKanbanViewModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [updating, setUpdating] = useState(false);

  const fetchCandidates = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await RecruitmentApi.getCandidates({ page: 1, page_size: 100 });
      const kanbanViewModel = RecruitmentViewModelFactory.createKanbanViewModel(response.data);
      setKanban(kanbanViewModel);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '載入應徵者失敗';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  const updateCandidateStatus = useCallback(
    async (candidateId: string, newStatus: CandidateStatus) => {
      setUpdating(true);
      try {
        await RecruitmentApi.updateCandidateStatus(candidateId, { status: newStatus });
        await fetchCandidates();
        return { success: true, message: '狀態更新成功' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '更新狀態失敗';
        return { success: false, message: errorMessage };
      } finally {
        setUpdating(false);
      }
    },
    [fetchCandidates]
  );

  const hireCandidate = useCallback(
    async (candidateId: string) => {
      setUpdating(true);
      try {
        await RecruitmentApi.hireCandidate(candidateId);
        await fetchCandidates();
        return { success: true, message: '錄取成功' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '錄取失敗';
        return { success: false, message: errorMessage };
      } finally {
        setUpdating(false);
      }
    },
    [fetchCandidates]
  );

  useEffect(() => {
    fetchCandidates();
  }, [fetchCandidates]);

  return {
    kanban,
    loading,
    error,
    updating,
    refresh: fetchCandidates,
    updateCandidateStatus,
    hireCandidate,
  };
};
