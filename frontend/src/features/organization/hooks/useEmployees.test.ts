import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { useEmployees } from './useEmployees';
import { OrganizationApi } from '../api/OrganizationApi';
import type { EmployeeDto } from '../api/OrganizationTypes';

// Mock OrganizationApi
vi.mock('../api/OrganizationApi', () => ({
  OrganizationApi: {
    getEmployeeList: vi.fn(),
  },
}));

describe('useEmployees', () => {
  const mockEmployees: EmployeeDto[] = [
    {
      id: '1',
      employee_number: 'EMP001',
      full_name: '王小明',
      email: 'xiaoming.wang@company.com',
      phone: '0912345678',
      department_id: 'dept-1',
      department_name: '人力資源部',
      position: '人資專員',
      status: 'ACTIVE',
      hire_date: '2023-01-15',
      created_at: '2023-01-15T08:00:00Z',
      updated_at: '2023-01-15T08:00:00Z',
    },
    {
      id: '2',
      employee_number: 'EMP002',
      full_name: '李小華',
      email: 'xiaohua.li@company.com',
      department_id: 'dept-2',
      department_name: '研發部',
      position: '資深工程師',
      status: 'ACTIVE',
      hire_date: '2022-06-01',
      created_at: '2022-06-01T08:00:00Z',
      updated_at: '2022-06-01T08:00:00Z',
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      vi.mocked(OrganizationApi.getEmployeeList).mockResolvedValue({
        employees: [],
        total: 0,
        page: 1,
        page_size: 10,
      });

      const { result } = renderHook(() => useEmployees());

      expect(result.current.loading).toBe(true);
      expect(result.current.employees).toEqual([]);
      expect(result.current.total).toBe(0);
      expect(result.current.error).toBeNull();
    });
  });

  describe('取得員工列表', () => {
    it('應該成功取得員工列表', async () => {
      vi.mocked(OrganizationApi.getEmployeeList).mockResolvedValue({
        employees: mockEmployees,
        total: 2,
        page: 1,
        page_size: 10,
      });

      const { result } = renderHook(() => useEmployees());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.employees).toHaveLength(2);
      expect(result.current.employees[0]!.fullName).toBe('王小明');
      expect(result.current.employees[1]!.fullName).toBe('李小華');
      expect(result.current.total).toBe(2);
      expect(result.current.error).toBeNull();
    });

    it('應該正確處理API錯誤', async () => {
      const errorMessage = '無法取得員工列表';
      vi.mocked(OrganizationApi.getEmployeeList).mockRejectedValue(
        new Error(errorMessage)
      );

      const { result } = renderHook(() => useEmployees());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.employees).toEqual([]);
      expect(result.current.error).toBe(errorMessage);
    });
  });

  describe('載入狀態', () => {
    it('取得資料過程中loading應該為true', async () => {
      let resolvePromise: (value: any) => void;
      const promise = new Promise((resolve) => {
        resolvePromise = resolve;
      });

      vi.mocked(OrganizationApi.getEmployeeList).mockReturnValue(promise as any);

      const { result } = renderHook(() => useEmployees());

      expect(result.current.loading).toBe(true);

      resolvePromise!({
        employees: mockEmployees,
        total: 2,
        page: 1,
        page_size: 10,
      });

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });
    });
  });

  describe('重新整理', () => {
    it('應該能重新取得員工列表', async () => {
      vi.mocked(OrganizationApi.getEmployeeList).mockResolvedValue({
        employees: mockEmployees,
        total: 2,
        page: 1,
        page_size: 10,
      });

      const { result } = renderHook(() => useEmployees());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(OrganizationApi.getEmployeeList).toHaveBeenCalledTimes(1);

      // 呼叫 refresh
      result.current.refresh();

      await waitFor(() => {
        expect(OrganizationApi.getEmployeeList).toHaveBeenCalledTimes(2);
      });
    });
  });

  describe('分頁與篩選', () => {
    it('應該支援分頁參數', async () => {
      vi.mocked(OrganizationApi.getEmployeeList).mockResolvedValue({
        employees: [],
        total: 0,
        page: 2,
        page_size: 20,
      });

      renderHook(() =>
        useEmployees({
          page: 2,
          page_size: 20,
        })
      );

      await waitFor(() => {
        expect(OrganizationApi.getEmployeeList).toHaveBeenCalledWith({
          page: 2,
          page_size: 20,
        });
      });
    });

    it('應該支援搜尋參數', async () => {
      vi.mocked(OrganizationApi.getEmployeeList).mockResolvedValue({
        employees: [],
        total: 0,
        page: 1,
        page_size: 10,
      });

      renderHook(() =>
        useEmployees({
          search: '王小明',
          department_id: 'dept-1',
          status: 'ACTIVE',
        })
      );

      await waitFor(() => {
        expect(OrganizationApi.getEmployeeList).toHaveBeenCalledWith({
          search: '王小明',
          department_id: 'dept-1',
          status: 'ACTIVE',
        });
      });
    });
  });
});
