import { useCallback, useState } from 'react';
import { PayrollApi } from '../api/PayrollApi';
import type { PayrollItemDefinitionDto } from '../api/PayrollTypes';

/**
 * usePayrollItems Hook
 * 管理薪資項目定義的增刪改查
 */
export const usePayrollItems = () => {
  const [items, setItems] = useState<PayrollItemDefinitionDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchItems = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await PayrollApi.getPayrollItemDefinitions();
      setItems(response || []);
    } catch (err: any) {
      setError(err.message || '載入薪資項目失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  const createItem = async (data: any) => {
    setLoading(true);
    try {
      await PayrollApi.createPayrollItemDefinition(data);
      await fetchItems();
      return true;
    } catch (err: any) {
      setError(err.message || '建立薪資項目失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const updateItem = async (id: string, data: any) => {
    setLoading(true);
    try {
      await PayrollApi.updatePayrollItemDefinition(id, data);
      await fetchItems();
      return true;
    } catch (err: any) {
      setError(err.message || '更新薪資項目失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const deleteItem = async (id: string) => {
    setLoading(true);
    try {
      await PayrollApi.deletePayrollItemDefinition(id);
      await fetchItems();
      return true;
    } catch (err: any) {
      setError(err.message || '刪除薪資項目失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    items,
    loading,
    error,
    fetchItems,
    createItem,
    updateItem,
    deleteItem,
    refresh: fetchItems
  };
};
