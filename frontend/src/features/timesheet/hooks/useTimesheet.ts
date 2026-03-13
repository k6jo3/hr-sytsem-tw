import { message } from 'antd';
import { useCallback, useEffect, useState } from 'react';
import { TimesheetApi } from '../api/TimesheetApi';
import type { SaveTimesheetEntryRequest } from '../api/TimesheetTypes';
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
    if (weekStartDate) {
      fetchWeeklyTimesheet();
    }
  }, [weekStartDate, fetchWeeklyTimesheet]);

  const handleSaveEntry = async (entry: SaveTimesheetEntryRequest) => {
    try {
      await TimesheetApi.saveEntry({
        ...entry,
        timesheet_id: summary?.id,
      });
      message.success('工時已儲存');
      await fetchWeeklyTimesheet();
    } catch (err) {
      message.error(err instanceof Error ? err.message : '儲存失敗');
      throw err;
    }
  };

  const handleDeleteEntry = async (entryId: string) => {
    try {
      await TimesheetApi.deleteEntry(summary?.id ?? '', entryId);
      message.success('已刪除工時記錄');
      await fetchWeeklyTimesheet();
    } catch (err) {
      message.error(err instanceof Error ? err.message : '刪除失敗');
    }
  };

  const handleSubmit = useCallback(async () => {
    if (!summary?.id) return;
    try {
      await TimesheetApi.submitTimesheet(summary.id);
      message.success('工時已送出審核');
      await fetchWeeklyTimesheet();
    } catch (err) {
      message.error(err instanceof Error ? err.message : '送出失敗');
    }
  }, [summary, fetchWeeklyTimesheet]);

  return { 
    summary, 
    loading, 
    error, 
    handleSaveEntry,
    handleDeleteEntry,
    handleSubmit, 
    refresh: fetchWeeklyTimesheet 
  };
};
