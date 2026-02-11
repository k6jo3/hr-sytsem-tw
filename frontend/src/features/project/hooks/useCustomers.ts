import { message } from 'antd';
import { useCallback, useState } from 'react';
import { ProjectApi } from '../api/ProjectApi';
import { GetCustomerListRequest } from '../api/ProjectTypes';
import { CustomerViewModelFactory } from '../factory/CustomerViewModelFactory';
import { CustomerViewModel } from '../model/CustomerViewModel';

/**
 * 客戶管理 Hook
 */
export const useCustomers = () => {
  const [customers, setCustomers] = useState<CustomerViewModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [total, setTotal] = useState(0);

  const fetchCustomers = useCallback(async (params?: GetCustomerListRequest) => {
    setLoading(true);
    setError(null);
    try {
      const response = await ProjectApi.getCustomerList(params);
      setCustomers(CustomerViewModelFactory.createListFromDTOs(response.customers));
      setTotal(response.total);
    } catch (err: any) {
      setError(err.message || '取得客戶列表失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  const createCustomer = async (request: any) => {
    setLoading(true);
    try {
      await ProjectApi.createCustomer(request);
      message.success('已成功建立客戶');
      await fetchCustomers();
      return true;
    } catch (err: any) {
      setError(err.message || '建立客戶失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const updateCustomer = async (id: string, request: any) => {
    setLoading(true);
    try {
      await ProjectApi.updateCustomer(id, request);
      message.success('已成功更新客戶');
      await fetchCustomers();
      return true;
    } catch (err: any) {
      setError(err.message || '更新客戶失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    customers,
    loading,
    error,
    total,
    fetchCustomers,
    createCustomer,
    updateCustomer,
    refresh: fetchCustomers,
  };
};
