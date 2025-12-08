import { useState, useEffect, useCallback } from 'react';
import * as OrganizationApi from '../api/OrganizationApi';
import { EmployeeViewModelFactory } from '../factory/EmployeeViewModelFactory';
import type { EmployeeViewModel } from '../model/EmployeeViewModel';
import type { GetEmployeeListRequest } from '../api/OrganizationTypes';

/**
 * useEmployees Hook
 * 管理員工列表的資料取得與狀態
 */
export const useEmployees = (params?: GetEmployeeListRequest) => {
  const [employees, setEmployees] = useState<EmployeeViewModel[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchEmployees = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await OrganizationApi.getEmployeeList(params);
      const viewModels = EmployeeViewModelFactory.createListFromDTOs(
        response.employees
      );
      setEmployees(viewModels);
      setTotal(response.total);
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : '無法取得員工列表';
      setError(errorMessage);
      setEmployees([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  }, [params]);

  useEffect(() => {
    fetchEmployees();
  }, [fetchEmployees]);

  const refresh = useCallback(() => {
    fetchEmployees();
  }, [fetchEmployees]);

  return {
    employees,
    total,
    loading,
    error,
    refresh,
  };
};
