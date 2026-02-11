import { act, renderHook } from '@testing-library/react';
import { vi } from 'vitest';
import { ProjectApi } from '../api/ProjectApi';
import { useCustomers } from './useCustomers';

vi.mock('../api/ProjectApi');

describe('useCustomers', () => {
  const mockCustomers = [
    {
      id: 'cust-1',
      customer_code: 'C001',
      customer_name: 'Customer 1',
      status: 'ACTIVE',
      created_at: '2025-01-01T00:00:00Z',
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應該有正確的初始狀態', () => {
    const { result } = renderHook(() => useCustomers());
    expect(result.current.customers).toEqual([]);
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  it('應該成功取得客戶列表', async () => {
    (ProjectApi.getCustomerList as any).mockResolvedValue({
      customers: mockCustomers,
      total: 1,
    });

    const { result } = renderHook(() => useCustomers());

    await act(async () => {
      await result.current.fetchCustomers();
    });

    expect(result.current.customers).toHaveLength(1);
    expect(result.current.customers[0].id).toBe('cust-1');
    expect(result.current.total).toBe(1);
    expect(result.current.loading).toBe(false);
  });

  it('應該正確處理 API 錯誤', async () => {
    (ProjectApi.getCustomerList as any).mockRejectedValue(new Error('API Error'));

    const { result } = renderHook(() => useCustomers());

    await act(async () => {
      await result.current.fetchCustomers();
    });

    expect(result.current.error).toBe('API Error');
    expect(result.current.loading).toBe(false);
  });

  it('應該正確處理分頁參數', async () => {
    (ProjectApi.getCustomerList as any).mockResolvedValue({
      customers: [],
      total: 0,
    });

    const { result } = renderHook(() => useCustomers());

    await act(async () => {
      await result.current.fetchCustomers({ page: 2, size: 10 });
    });

    expect(ProjectApi.getCustomerList).toHaveBeenCalledWith({ page: 2, size: 10 });
  });
});
