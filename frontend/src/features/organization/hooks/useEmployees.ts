import { useState, useEffect, useCallback, useMemo } from 'react';
import { OrganizationApi } from '../api/OrganizationApi';
import { EmployeeViewModelFactory } from '../factory/EmployeeViewModelFactory';
import { extractApiError } from '@shared/utils/errorUtils';
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

  // 穩定化 params 參考，避免每次 render 產生新物件觸發無限迴圈
  const stableParams = useMemo(
    () => params,
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [JSON.stringify(params)]
  );

  const fetchEmployees = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await OrganizationApi.getEmployeeList(stableParams);
      const viewModels = EmployeeViewModelFactory.createListFromDTOs(
        response.employees
      );
      setEmployees(viewModels);
      setTotal(response.total);
    } catch (err) {
      const { message } = extractApiError(err, '無法取得員工列表');
      setError(message);
      setEmployees([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  }, [stableParams]);

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
