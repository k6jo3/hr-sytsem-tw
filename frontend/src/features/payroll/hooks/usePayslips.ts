import { useState, useCallback, useEffect } from 'react';
import { PayrollApi } from '../api/PayrollApi';
import { PayslipViewModelFactory } from '../factory/PayslipViewModelFactory';
import type { PayslipSummaryViewModel, PayslipDetailViewModel } from '../model/PayrollViewModel';

/**
 * 我的薪資單Hook
 */
export const usePayslips = (year?: number) => {
  const [payslips, setPayslips] = useState<PayslipSummaryViewModel[]>([]);
  const [selectedPayslip, setSelectedPayslip] = useState<PayslipDetailViewModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [detailLoading, setDetailLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [total, setTotal] = useState(0);

  const fetchPayslips = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await PayrollApi.getMyPayslips({ year });
      const viewModels = PayslipViewModelFactory.createSummaryListFromDTOs(response.payslips);

      setPayslips(viewModels);
      setTotal(response.total);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得薪資單列表');
      setPayslips([]);
    } finally {
      setLoading(false);
    }
  }, [year]);

  const fetchPayslipDetail = useCallback(async (id: string) => {
    setDetailLoading(true);
    try {
      const response = await PayrollApi.getPayslipDetail(id);
      const viewModel = PayslipViewModelFactory.createDetailFromDTO(response.payslip);
      setSelectedPayslip(viewModel);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得薪資單詳情');
    } finally {
      setDetailLoading(false);
    }
  }, []);

  const downloadPdf = useCallback(async (id: string) => {
    try {
      const blob = await PayrollApi.downloadPayslipPdf(id);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `payslip_${id}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法下載薪資單');
    }
  }, []);

  useEffect(() => {
    fetchPayslips();
  }, [fetchPayslips]);

  return {
    payslips,
    selectedPayslip,
    loading,
    detailLoading,
    error,
    total,
    fetchPayslipDetail,
    downloadPdf,
    refresh: fetchPayslips,
  };
};
