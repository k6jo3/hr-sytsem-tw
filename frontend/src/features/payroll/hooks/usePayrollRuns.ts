import { useCallback, useState } from 'react';
import { PayrollApi } from '../api/PayrollApi';
import type { StartPayrollRunRequest } from '../api/PayrollTypes';
import { PayrollViewModelFactory } from '../factory/PayrollViewModelFactory';
import type { PayrollRunViewModel } from '../model/PayrollViewModel';

/**
 * usePayrollRuns Hook
 * 管理薪資批次的資料取得與生命週期
 */
export const usePayrollRuns = () => {
  const [runs, setRuns] = useState<PayrollRunViewModel[]>([]);
  const [currentRun, setCurrentRun] = useState<PayrollRunViewModel | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [total, setTotal] = useState(0);

  const fetchRuns = useCallback(async (params: any = {}) => {
    setLoading(true);
    setError(null);
    try {
      const response = await PayrollApi.getPayrollRuns(params);
      const items = response.items || response.content || [];
      const viewModels = PayrollViewModelFactory.createListFromDTOs(items);
      setRuns(viewModels);
      setTotal(response.total || items.length);
    } catch (err: any) {
      setError(err.message || '載入薪資批次失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchRunDetail = useCallback(async (runId: string) => {
    setLoading(true);
    try {
      const response = await PayrollApi.getPayrollRunDetail(runId);
      setCurrentRun(PayrollViewModelFactory.createFromDTO(response.run));
    } catch (err: any) {
      setError(err.message || '載入批次詳情失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  const startRun = async (data: StartPayrollRunRequest) => {
    setLoading(true);
    try {
      await PayrollApi.startPayrollRun(data);
      await fetchRuns();
      return true;
    } catch (err: any) {
      setError(err.message || '啟動薪資計算失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const calculateRun = async (runId: string) => {
    setLoading(true);
    try {
      await PayrollApi.calculatePayroll(runId);
      await fetchRunDetail(runId);
      return true;
    } catch (err: any) {
      setError(err.message || '計算失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const submitRun = async (runId: string) => {
    setLoading(true);
    try {
      await PayrollApi.submitPayrollRun(runId);
      await fetchRunDetail(runId);
      return true;
    } catch (err: any) {
      setError(err.message || '送審失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const approveRun = async (runId: string) => {
    setLoading(true);
    try {
      await PayrollApi.approvePayrollRun(runId);
      await fetchRunDetail(runId);
      return true;
    } catch (err: any) {
      setError(err.message || '核准失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const generateBankFile = async (runId: string) => {
    setLoading(true);
    try {
      await PayrollApi.generateBankTransferFile(runId);
      await fetchRuns();
      return true;
    } catch (err: any) {
      setError(err.message || '產生薪轉檔失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const getBankFileUrl = async (runId: string) => {
    try {
      return await PayrollApi.getBankTransferDownloadUrl(runId);
    } catch (err: any) {
      setError(err.message || '獲取下載連結失敗');
      return null;
    }
  };

  return {
    runs,
    currentRun,
    loading,
    error,
    total,
    fetchRuns,
    fetchRunDetail,
    startRun,
    calculateRun,
    submitRun,
    approveRun,
    generateBankFile,
    getBankFileUrl,
    refresh: fetchRuns
  };
};
