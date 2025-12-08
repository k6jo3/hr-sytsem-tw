import { useState, useEffect, useCallback } from 'react';
import * as TimesheetApi from '../api/TimesheetApi';
import { TimesheetViewModelFactory } from '../factory/TimesheetViewModelFactory';
import type { WeeklyTimesheetSummary } from '../model/TimesheetViewModel';

export const useTimesheet = (weekStartDate: string) => {
  const [summary, setSummary] = useState<WeeklyTimesheetSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchWeeklyTimesheet = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await TimesheetApi.getWeeklyTimesheet({ week_start_date: weekStartDate });
      const weeklySummary = TimesheetViewModelFactory.createWeeklySummary(response.timesheet);
      setSummary(weeklySummary);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得工時記錄');
      setSummary(null);
    } finally {
      setLoading(false);
    }
  }, [weekStartDate]);

  useEffect(() => {
    fetchWeeklyTimesheet();
  }, [fetchWeeklyTimesheet]);

  const handleSubmit = useCallback(async () => {
    if (!summary) return;
    try {
      await TimesheetApi.submitTimesheet({
        entries: summary.entries.map(e => ({
          project_id: '',
          work_date: e.workDate,
          hours: e.hours,
          description: e.description,
        })),
      });
      await fetchWeeklyTimesheet();
    } catch (err) {
      throw err;
    }
  }, [summary, fetchWeeklyTimesheet]);

  return { summary, loading, error, handleSubmit, refresh: fetchWeeklyTimesheet };
};
