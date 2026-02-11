import { useCallback, useState } from 'react';
import { PayrollApi } from '../api/PayrollApi';
import { SalaryStructureViewModelFactory, type SalaryStructureViewModel } from '../factory/SalaryStructureViewModelFactory';

/**
 * useSalaryStructure Hook
 * 管理員工薪資結構的資料取得與操作
 */
export const useSalaryStructure = () => {
  const [structures, setStructures] = useState<SalaryStructureViewModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [total, setTotal] = useState(0);

  const fetchStructures = useCallback(async (params: any = {}) => {
    setLoading(true);
    setError(null);
    try {
      const response = await PayrollApi.getSalaryStructures(params);
      const items = response.items || response.content || [];
      const viewModels = SalaryStructureViewModelFactory.createListFromDTOs(items);
      setStructures(viewModels);
      setTotal(response.total || items.length);
    } catch (err: any) {
      setError(err.message || '載入薪資結構失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  const createStructure = async (data: any) => {
    setLoading(true);
    try {
      await PayrollApi.createSalaryStructure(data);
      await fetchStructures();
      return true;
    } catch (err: any) {
      setError(err.message || '建立薪資結構失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const updateStructure = async (id: string, data: any) => {
    setLoading(true);
    try {
      await PayrollApi.updateSalaryStructure(id, data);
      await fetchStructures();
      return true;
    } catch (err: any) {
      setError(err.message || '更新薪資結構失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    structures,
    loading,
    error,
    total,
    fetchStructures,
    createStructure,
    updateStructure,
    refresh: fetchStructures
  };
};
