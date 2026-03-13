import { act, renderHook } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PayrollApi } from '../api/PayrollApi';
import { useSalaryStructure } from './useSalaryStructure';

// Mock PayrollApi
vi.mock('../api/PayrollApi', () => ({
  PayrollApi: {
    getSalaryStructures: vi.fn(),
    createSalaryStructure: vi.fn(),
    updateSalaryStructure: vi.fn(),
  },
}));

// Mock SalaryStructureViewModelFactory
vi.mock('../factory/SalaryStructureViewModelFactory', () => ({
  SalaryStructureViewModelFactory: {
    createListFromDTOs: vi.fn((dtos) => dtos.map((dto: any) => ({
      id: dto.id,
      employeeId: dto.employeeId,
      payrollSystem: dto.payrollSystem,
      payrollSystemLabel: dto.payrollSystem === 'MONTHLY' ? '月薪制' : '時薪制',
      amountDisplay: `$${dto.monthlySalary?.toLocaleString() || dto.hourlyRate}`,
      active: dto.active,
    }))),
  },
}));

describe('useSalaryStructure', () => {
  const mockStructures = [
    {
      id: 'ss-1',
      employeeId: 'emp-1',
      payrollSystem: 'MONTHLY',
      monthlySalary: 50000,
      effectiveDate: '2025-01-01',
      active: true,
      items: [],
    },
    {
      id: 'ss-2',
      employeeId: 'emp-2',
      payrollSystem: 'HOURLY',
      hourlyRate: 250,
      effectiveDate: '2025-01-01',
      active: true,
      items: [],
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      const { result } = renderHook(() => useSalaryStructure());

      expect(result.current.structures).toEqual([]);
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBeNull();
      expect(result.current.total).toBe(0);
    });
  });

  describe('取得薪資結構列表', () => {
    it('應該成功取得薪資結構列表', async () => {
      vi.mocked(PayrollApi.getSalaryStructures).mockResolvedValue({
        items: mockStructures,
        total: 2,
      });

      const { result } = renderHook(() => useSalaryStructure());

      await act(async () => {
        await result.current.fetchStructures();
      });

      expect(result.current.structures).toHaveLength(2);
      expect(result.current.total).toBe(2);
      expect(result.current.error).toBeNull();
      expect(PayrollApi.getSalaryStructures).toHaveBeenCalled();
    });

    it('應該正確處理 API 錯誤', async () => {
      const errorMessage = '載入薪資結構失敗';
      vi.mocked(PayrollApi.getSalaryStructures).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useSalaryStructure());

      await act(async () => {
        await result.current.fetchStructures();
      });

      expect(result.current.error).toBe(errorMessage);
      expect(result.current.structures).toEqual([]);
    });

    it('應該支援查詢參數', async () => {
      vi.mocked(PayrollApi.getSalaryStructures).mockResolvedValue({
        items: mockStructures,
        total: 2,
      });

      const { result } = renderHook(() => useSalaryStructure());

      await act(async () => {
        await result.current.fetchStructures({ employeeId: 'emp-1' });
      });

      expect(PayrollApi.getSalaryStructures).toHaveBeenCalledWith({ employeeId: 'emp-1' });
    });

    it('應該處理回應中沒有 items 的情況', async () => {
      vi.mocked(PayrollApi.getSalaryStructures).mockResolvedValue({
        content: mockStructures,
        total: 2,
      });

      const { result } = renderHook(() => useSalaryStructure());

      await act(async () => {
        await result.current.fetchStructures();
      });

      expect(result.current.structures).toHaveLength(2);
    });
  });

  describe('建立薪資結構', () => {
    it('應該成功建立薪資結構', async () => {
      vi.mocked(PayrollApi.createSalaryStructure).mockResolvedValue(mockStructures[0] as any);
      vi.mocked(PayrollApi.getSalaryStructures).mockResolvedValue({
        items: mockStructures,
        total: 2,
      });

      const { result } = renderHook(() => useSalaryStructure());

      let success;
      await act(async () => {
        success = await result.current.createStructure({
          employeeId: 'emp-1',
          payrollSystem: 'MONTHLY',
          monthlySalary: 50000,
          effectiveDate: '2025-01-01',
        });
      });

      expect(success).toBe(true);
      expect(PayrollApi.createSalaryStructure).toHaveBeenCalled();
      expect(PayrollApi.getSalaryStructures).toHaveBeenCalled();
    });

    it('應該正確處理建立失敗', async () => {
      const errorMessage = '建立薪資結構失敗';
      vi.mocked(PayrollApi.createSalaryStructure).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useSalaryStructure());

      let success;
      await act(async () => {
        success = await result.current.createStructure({
          employeeId: 'emp-1',
          payrollSystem: 'MONTHLY',
          monthlySalary: 50000,
        });
      });

      expect(success).toBe(false);
      expect(result.current.error).toBe(errorMessage);
    });
  });

  describe('更新薪資結構', () => {
    it('應該成功更新薪資結構', async () => {
      vi.mocked(PayrollApi.updateSalaryStructure).mockResolvedValue(mockStructures[0] as any);
      vi.mocked(PayrollApi.getSalaryStructures).mockResolvedValue({
        items: mockStructures,
        total: 2,
      });

      const { result } = renderHook(() => useSalaryStructure());

      let success;
      await act(async () => {
        success = await result.current.updateStructure('ss-1', {
          monthlySalary: 55000,
        });
      });

      expect(success).toBe(true);
      expect(PayrollApi.updateSalaryStructure).toHaveBeenCalledWith('ss-1', {
        monthlySalary: 55000,
      });
      expect(PayrollApi.getSalaryStructures).toHaveBeenCalled();
    });

    it('應該正確處理更新失敗', async () => {
      const errorMessage = '更新薪資結構失敗';
      vi.mocked(PayrollApi.updateSalaryStructure).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useSalaryStructure());

      let success;
      await act(async () => {
        success = await result.current.updateStructure('ss-1', {
          monthlySalary: 55000,
        });
      });

      expect(success).toBe(false);
      expect(result.current.error).toBe(errorMessage);
    });
  });

  describe('重新整理', () => {
    it('應該能重新取得薪資結構列表', async () => {
      vi.mocked(PayrollApi.getSalaryStructures).mockResolvedValue({
        items: mockStructures,
        total: 2,
      });

      const { result } = renderHook(() => useSalaryStructure());

      await act(async () => {
        await result.current.fetchStructures();
      });

      expect(PayrollApi.getSalaryStructures).toHaveBeenCalledTimes(1);

      await act(async () => {
        await result.current.refresh();
      });

      expect(PayrollApi.getSalaryStructures).toHaveBeenCalledTimes(2);
    });
  });

  describe('載入狀態', () => {
    it('操作過程中 loading 應該為 true', async () => {
      vi.mocked(PayrollApi.getSalaryStructures).mockImplementation(
        () => new Promise((resolve) => setTimeout(() => resolve({
          items: mockStructures,
          total: 2,
        }), 50))
      );

      const { result } = renderHook(() => useSalaryStructure());

      act(() => {
        result.current.fetchStructures();
      });

      expect(result.current.loading).toBe(true);

      await act(async () => {
        await new Promise(resolve => setTimeout(resolve, 100));
      });

      expect(result.current.loading).toBe(false);
    });
  });
});
