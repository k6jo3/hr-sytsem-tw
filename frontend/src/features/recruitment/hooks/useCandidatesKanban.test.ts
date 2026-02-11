import { act, renderHook, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { RecruitmentApi } from '../api';
import { useCandidatesKanban } from './useCandidatesKanban';

// Mock RecruitmentApi
vi.mock('../api', () => ({
  RecruitmentApi: {
    getCandidates: vi.fn(),
    updateCandidateStatus: vi.fn(),
    hireCandidate: vi.fn(),
  },
}));

// Mock RecruitmentViewModelFactory
vi.mock('../factory/RecruitmentViewModelFactory', () => ({
  RecruitmentViewModelFactory: {
    createKanbanViewModel: vi.fn((candidates) => ({
      columns: [
        {
          id: 'NEW',
          title: '新投遞',
          count: candidates.filter((c: any) => c.status === 'NEW').length,
          candidates: candidates.filter((c: any) => c.status === 'NEW'),
        },
        {
          id: 'SCREENING',
          title: '履歷篩選',
          count: candidates.filter((c: any) => c.status === 'SCREENING').length,
          candidates: candidates.filter((c: any) => c.status === 'SCREENING'),
        },
        {
          id: 'INTERVIEWING',
          title: '面試中',
          count: candidates.filter((c: any) => c.status === 'INTERVIEWING').length,
          candidates: candidates.filter((c: any) => c.status === 'INTERVIEWING'),
        },
      ],
      candidates,
    })),
  },
}));

describe('useCandidatesKanban', () => {
  const mockCandidates = [
    {
      candidate_id: '1',
      opening_id: 'job-1',
      job_title: '前端工程師',
      full_name: '張三',
      email: 'zhang@example.com',
      source: 'JOB_BANK',
      application_date: '2025-12-08',
      status: 'NEW',
      created_at: '2025-12-08T10:00:00Z',
      updated_at: '2025-12-08T10:00:00Z',
    },
    {
      candidate_id: '2',
      opening_id: 'job-1',
      job_title: '前端工程師',
      full_name: '李四',
      email: 'li@example.com',
      source: 'REFERRAL',
      application_date: '2025-12-07',
      status: 'SCREENING',
      created_at: '2025-12-07T10:00:00Z',
      updated_at: '2025-12-07T10:00:00Z',
    },
    {
      candidate_id: '3',
      opening_id: 'job-1',
      job_title: '前端工程師',
      full_name: '王五',
      email: 'wang@example.com',
      source: 'WEBSITE',
      application_date: '2025-12-06',
      status: 'INTERVIEWING',
      created_at: '2025-12-06T10:00:00Z',
      updated_at: '2025-12-06T10:00:00Z',
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });

      const { result } = renderHook(() => useCandidatesKanban());

      expect(result.current.loading).toBe(true);
      expect(result.current.kanban).toBeNull();
      expect(result.current.error).toBeNull();
      expect(result.current.updating).toBe(false);
    });
  });

  describe('取得看板資料', () => {
    it('應該成功取得看板資料', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });

      const { result } = renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.kanban).not.toBeNull();
      expect(result.current.kanban?.columns).toHaveLength(3);
      expect(result.current.kanban?.candidates).toHaveLength(3);
      expect(result.current.error).toBeNull();
    });

    it('應該正確分組候選人到各欄位', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });

      const { result } = renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      const newColumn = result.current.kanban?.columns.find((c) => c.id === 'NEW');
      expect(newColumn?.count).toBe(1);

      const screeningColumn = result.current.kanban?.columns.find((c) => c.id === 'SCREENING');
      expect(screeningColumn?.count).toBe(1);

      const interviewingColumn = result.current.kanban?.columns.find((c) => c.id === 'INTERVIEWING');
      expect(interviewingColumn?.count).toBe(1);
    });

    it('應該正確處理 API 錯誤', async () => {
      const errorMessage = '載入應徵者失敗';
      vi.mocked(RecruitmentApi.getCandidates).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.kanban).toBeNull();
      expect(result.current.error).toBe(errorMessage);
    });

    it('應該使用正確的分頁參數', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });

      renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(RecruitmentApi.getCandidates).toHaveBeenCalledWith({
          page: 1,
          page_size: 100,
        });
      });
    });
  });

  describe('更新候選人狀態', () => {
    it('應該成功更新候選人狀態', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });
      vi.mocked(RecruitmentApi.updateCandidateStatus).mockResolvedValue(undefined);

      const { result } = renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      let response;
      await act(async () => {
        response = await result.current.updateCandidateStatus('1', 'SCREENING');
      });

      expect(response.success).toBe(true);
      expect(response.message).toBe('狀態更新成功');
      expect(RecruitmentApi.updateCandidateStatus).toHaveBeenCalledWith('1', {
        status: 'SCREENING',
      });
      expect(RecruitmentApi.getCandidates).toHaveBeenCalledTimes(2); // Initial + refresh
    });

    it('應該正確處理更新失敗', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });
      const errorMessage = '更新狀態失敗';
      vi.mocked(RecruitmentApi.updateCandidateStatus).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      let response;
      await act(async () => {
        response = await result.current.updateCandidateStatus('1', 'SCREENING');
      });

      expect(response.success).toBe(false);
      expect(response.message).toBe(errorMessage);
    });

    it('更新過程中 updating 應該為 true', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });
      vi.mocked(RecruitmentApi.updateCandidateStatus).mockImplementation(
        () => new Promise((resolve) => setTimeout(resolve, 50))
      );

      const { result } = renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      act(() => {
        result.current.updateCandidateStatus('1', 'SCREENING');
      });

      expect(result.current.updating).toBe(true);

      await waitFor(() => {
        expect(result.current.updating).toBe(false);
      });
    });
  });

  describe('錄取候選人', () => {
    it('應該成功錄取候選人', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });
      vi.mocked(RecruitmentApi.hireCandidate).mockResolvedValue(undefined);

      const { result } = renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      let response;
      await act(async () => {
        response = await result.current.hireCandidate('3');
      });

      expect(response.success).toBe(true);
      expect(response.message).toBe('錄取成功');
      expect(RecruitmentApi.hireCandidate).toHaveBeenCalledWith('3');
      expect(RecruitmentApi.getCandidates).toHaveBeenCalledTimes(2); // Initial + refresh
    });

    it('應該正確處理錄取失敗', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });
      const errorMessage = '錄取失敗';
      vi.mocked(RecruitmentApi.hireCandidate).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      let response;
      await act(async () => {
        response = await result.current.hireCandidate('3');
      });

      expect(response.success).toBe(false);
      expect(response.message).toBe(errorMessage);
    });
  });

  describe('重新整理', () => {
    it('應該能重新取得看板資料', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockResolvedValue({
        data: mockCandidates,
        total: 3,
      });

      const { result } = renderHook(() => useCandidatesKanban());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(RecruitmentApi.getCandidates).toHaveBeenCalledTimes(1);

      await act(async () => {
        await result.current.refresh();
      });

      expect(RecruitmentApi.getCandidates).toHaveBeenCalledTimes(2);
    });
  });

  describe('載入狀態', () => {
    it('取得資料過程中 loading 應該為 true', async () => {
      vi.mocked(RecruitmentApi.getCandidates).mockImplementation(
        () =>
          new Promise((resolve) =>
            setTimeout(
              () =>
                resolve({
                  data: mockCandidates,
                  total: 3,
                }),
              50
            )
          )
      );

      const { result } = renderHook(() => useCandidatesKanban());

      expect(result.current.loading).toBe(true);

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });
    });
  });
});
