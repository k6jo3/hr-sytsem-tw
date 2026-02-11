import { useCallback, useState } from 'react';
import { InsuranceApi } from '../api/InsuranceApi';
import type { GetEnrollmentsRequest } from '../api/InsuranceTypes';
import { InsuranceViewModelFactory } from '../factory/InsuranceViewModelFactory';
import type { EnrollmentViewModel } from '../model/InsuranceViewModel';

/**
 * useInsuranceEnrollments Hook
 * 管理員工加退保記錄
 */
export const useInsuranceEnrollments = () => {
  const [enrollments, setEnrollments] = useState<EnrollmentViewModel[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchEnrollments = useCallback(async (params: GetEnrollmentsRequest = {}) => {
    setLoading(true);
    setError(null);
    try {
      const response = await InsuranceApi.getEnrollments(params);
      const vms = InsuranceViewModelFactory.createEnrollmentList(response.enrollments);
      setEnrollments(vms);
      setTotal(response.total);
    } catch (err: any) {
      setError(err.message || '載入投保記錄失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  const enrollEmployee = async (data: any) => {
    setLoading(true);
    try {
      await InsuranceApi.createEnrollment(data);
      await fetchEnrollments();
      return true;
    } catch (err: any) {
      setError(err.message || '加保失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const withdrawEmployee = async (id: string, date: string, reason: string) => {
    setLoading(true);
    try {
      await InsuranceApi.withdrawEnrollment(id, { withdraw_date: date, reason });
      await fetchEnrollments();
      return true;
    } catch (err: any) {
      setError(err.message || '退保失敗');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return {
    enrollments,
    total,
    loading,
    error,
    fetchEnrollments,
    enrollEmployee,
    withdrawEmployee,
    refresh: fetchEnrollments
  };
};
