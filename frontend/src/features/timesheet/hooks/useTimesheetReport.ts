import { useCallback, useEffect, useState } from 'react';
import { TimesheetApi } from '../api/TimesheetApi';
import type { TimesheetReportSummaryDto } from '../api/TimesheetTypes';

/**
 * Timesheet Report Hook (工時報表 Hook)
 * Domain Code: HR07
 */
export const useTimesheetReport = (startDate: string, endDate: string) => {
  const [summary, setSummary] = useState<TimesheetReportSummaryDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchSummary = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await TimesheetApi.getSummary({ start_date: startDate, end_date: endDate });
      setSummary(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : '載入報表失敗');
      console.error('Failed to fetch timesheet summary:', err);
    } finally {
      setLoading(false);
    }
  }, [startDate, endDate]);

  useEffect(() => {
    fetchSummary();
  }, [fetchSummary]);

  return {
    summary,
    loading,
    error,
    refresh: fetchSummary
  };
};
