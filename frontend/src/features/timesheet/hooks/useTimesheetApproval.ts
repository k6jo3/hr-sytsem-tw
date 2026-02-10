import { message } from 'antd';
import { useCallback, useState } from 'react';
import { TimesheetApi } from '../api/TimesheetApi';
import { TimesheetViewModelFactory } from '../factory/TimesheetViewModelFactory';
import type { WeeklyTimesheetSummary } from '../model/TimesheetViewModel';

export const useTimesheetApproval = () => {
  const [pendingTimesheets, setPendingTimesheets] = useState<WeeklyTimesheetSummary[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchPendingApprovals = useCallback(async (projectId?: string, page = 1, pageSize = 10) => {
    setLoading(true);
    setError(null);
    try {
      const response = await TimesheetApi.getPendingApprovals({
        project_id: projectId,
        page,
        page_size: pageSize
      });
      const summaries = response.timesheets.map(ts => TimesheetViewModelFactory.createWeeklySummary(ts));
      setPendingTimesheets(summaries);
      setTotal(response.total);
    } catch (err) {
      setError(err instanceof Error ? err.message : '無法取得待審核列表');
    } finally {
      setLoading(false);
    }
  }, []);

  const handleApprove = async (timesheetId: string) => {
    try {
      await TimesheetApi.approve(timesheetId);
      message.success('工時已核准');
      await fetchPendingApprovals();
    } catch (err) {
      message.error(err instanceof Error ? err.message : '核准失敗');
    }
  };

  const handleReject = async (timesheetId: string, reason: string) => {
    try {
      await TimesheetApi.reject(timesheetId, { rejection_reason: reason });
      message.success('工時已駁回');
      await fetchPendingApprovals();
    } catch (err) {
      message.error(err instanceof Error ? err.message : '駁回失敗');
    }
  };

  const handleBatchApprove = async (ids: string[]) => {
    try {
      await TimesheetApi.batchApprove({ timesheet_ids: ids });
      message.success(`已核准 ${ids.length} 筆工時記錄`);
      await fetchPendingApprovals();
    } catch (err) {
      message.error(err instanceof Error ? err.message : '批次核准失敗');
    }
  };

  return {
    pendingTimesheets,
    total,
    loading,
    error,
    fetchPendingApprovals,
    handleApprove,
    handleReject,
    handleBatchApprove
  };
};
