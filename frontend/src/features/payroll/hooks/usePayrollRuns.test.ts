import { act, renderHook } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PayrollApi } from '../api/PayrollApi';
import { usePayrollRuns } from './usePayrollRuns';

// Mock PayrollApi
vi.mock('../api/PayrollApi', () => ({
  PayrollApi: {
    getPayrollRuns: vi.fn(),
    getPayrollRunDetail: vi.fn(),
    startPayrollRun: vi.fn(),
    calculatePayroll: vi.fn(),
    submitPayrollRun: vi.fn(),
    approvePayrollRun: vi.fn(),
    rejectPayrollRun: vi.fn(),
  },
}));

// Mock PayrollViewModelFactory
vi.mock('../factory/PayrollViewModelFactory', () => ({
  PayrollViewModelFactory: {
    createListFromDTOs: vi.fn((dtos) => dtos.map((dto: any) => ({
      runId: dto.runId,
      name: dto.name,
      status: dto.status,
      statusLabel: '待審核',
      progress: 100,
    }))),
    createFromDTO: vi.fn((dto) => ({
      runId: dto.runId,
      name: dto.name,
      status: dto.status,
      statusLabel: '待審核',
      progress: 100,
    })),
  },
}));

describe('usePayrollRuns', () => {
  const mockRuns = [
    {
      runId: 'run-1',
      organizationId: 'org-1',
      name: '2025年11月薪資',
      status: 'COMPLETED',
      payrollSystem: 'MONTHLY',
      start: '2025-11-01',
      end: '2025-11-30',
      payDate: '2025-12-05',
      totalDays: 30,
      totalEmployees: 50,
      processedEmployees: 50,
      successCount: 50,
      failureCount: 0,
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      const { result } = renderHook(() => usePayrollRuns());

      expect(result.current.runs).toEqual([]);
      expect(result.current.currentRun).toBeNull();
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBeNull();
      expect(result.current.total).toBe(0);
    });
  });

  describe('取得薪資批次列表', () => {
    it('應該成功取得薪資批次列表', async () => {
      vi.mocked(PayrollApi.getPayrollRuns).mockResolvedValue({
        items: mockRuns,
        total: 1,
      });

      const { result } = renderHook(() => usePayrollRuns());

      await act(async () => {
        await result.current.fetchRuns();
      });

      expect(result.current.runs).toHaveLength(1);
      expect(result.current.total).toBe(1);
      expect(result.current.error).toBeNull();
    });

    it('應該正確處理 API 錯誤', async () => {
      vi.mocked(PayrollApi.getPayrollRuns).mockRejectedValue(new Error('載入失敗'));

      const { result } = renderHook(() => usePayrollRuns());

      await act(async () => {
        await result.current.fetchRuns();
      });

      expect(result.current.error).toBe('載入失敗');
    });
  });

  describe('建立薪資批次', () => {
    it('應該成功建立薪資批次', async () => {
      vi.mocked(PayrollApi.startPayrollRun).mockResolvedValue(mockRuns[0]);
      vi.mocked(PayrollApi.getPayrollRuns).mockResolvedValue({
        items: mockRuns,
        total: 1,
      });

      const { result } = renderHook(() => usePayrollRuns());

      let success;
      await act(async () => {
        success = await result.current.startRun({
          name: '2025年11月薪資',
          payrollSystem: 'MONTHLY',
          start: '2025-11-01',
          end: '2025-11-30',
          payDate: '2025-12-05',
        });
      });

      expect(success).toBe(true);
    });
  });

  describe('執行薪資計算', () => {
    it('應該成功執行薪資計算', async () => {
      vi.mocked(PayrollApi.calculatePayroll).mockResolvedValue(mockRuns[0]);
      vi.mocked(PayrollApi.getPayrollRunDetail).mockResolvedValue({
        run: mockRuns[0],
      });

      const { result } = renderHook(() => usePayrollRuns());

      let success;
      await act(async () => {
        success = await result.current.calculateRun('run-1');
      });

      expect(success).toBe(true);
    });
  });

  describe('核准薪資批次', () => {
    it('應該成功核准薪資批次', async () => {
      vi.mocked(PayrollApi.approvePayrollRun).mockResolvedValue(mockRuns[0]);
      vi.mocked(PayrollApi.getPayrollRunDetail).mockResolvedValue({
        run: mockRuns[0],
      });

      const { result } = renderHook(() => usePayrollRuns());

      let success;
      await act(async () => {
        success = await result.current.approveRun('run-1');
      });

      expect(success).toBe(true);
    });
  });
});
