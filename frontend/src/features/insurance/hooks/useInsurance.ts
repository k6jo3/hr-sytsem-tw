import { useCallback, useEffect, useState } from 'react';
import { InsuranceApi } from '../api/InsuranceApi';
import type { GetEnrollmentsRequest } from '../api/InsuranceTypes';
import { InsuranceViewModelFactory } from '../factory/InsuranceViewModelFactory';
import type { EnrollmentViewModel } from '../model/InsuranceViewModel';

/**
 * Insurance Hook (保險管理 Hook)
 * 處理保險管理相關的業務邏輯
 */
export const useInsurance = () => {
  const [enrollments, setEnrollments] = useState<EnrollmentViewModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [filters, setFilters] = useState<GetEnrollmentsRequest>({});

  const fetchEnrollments = useCallback(async (p = page, ps = pageSize, f = filters) => {
    setLoading(true);
    setError(null);
    try {
      const response = await InsuranceApi.getEnrollments({
        ...f,
        page: p,
        page_size: ps,
      });
      const viewModels = InsuranceViewModelFactory.createListFromDTOs(response.enrollments);
      setEnrollments(viewModels);
      setTotal(response.total);
      setPage(response.page);
      setPageSize(response.page_size);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得投保記錄');
    } finally {
      setLoading(false);
    }
  }, [filters, page, pageSize]);

  const handlePageChange = (p: number, ps?: number) => {
    setPage(p);
    if (ps) setPageSize(ps);
    fetchEnrollments(p, ps || pageSize);
  };

  const handleFilterChange = (newFilters: GetEnrollmentsRequest) => {
    setFilters(newFilters);
    setPage(1);
    fetchEnrollments(1, pageSize, newFilters);
  };

  useEffect(() => {
    fetchEnrollments();
  }, [fetchEnrollments]);

  return {
    enrollments,
    loading,
    error,
    total,
    page,
    pageSize,
    filters,
    handlePageChange,
    handleFilterChange,
    refresh: fetchEnrollments,
  };
};
