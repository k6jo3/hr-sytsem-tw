import { useState } from 'react';
import { OrganizationApi } from '../api';

/**
 * Employees Hook (員工管理 Hook)
 * 處理員工相關的業務邏輯
 */
export const useEmployees = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const getEmployees = async (params?: { department?: string; status?: string }) => {
    setLoading(true);
    setError(null);
    try {
      const result = await OrganizationApi.getEmployees(params);
      return result;
    } catch (err) {
      setError(err as Error);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const getEmployeeById = async (id: string) => {
    setLoading(true);
    setError(null);
    try {
      const result = await OrganizationApi.getEmployeeById(id);
      return result;
    } catch (err) {
      setError(err as Error);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return {
    loading,
    error,
    getEmployees,
    getEmployeeById,
  };
};
