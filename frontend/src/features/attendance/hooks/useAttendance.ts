import { useState, useEffect, useCallback } from 'react';
import * as AttendanceApi from '../api/AttendanceApi';
import { AttendanceViewModelFactory } from '../factory/AttendanceViewModelFactory';
import type { TodayAttendanceSummary } from '../model/AttendanceRecordViewModel';
import type { CheckType } from '../api/AttendanceTypes';

/**
 * useAttendance Hook
 * 管理考勤打卡的資料取得與狀態
 */
export const useAttendance = () => {
  const [summary, setSummary] = useState<TodayAttendanceSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [checkingIn, setCheckingIn] = useState(false);

  const fetchTodayAttendance = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await AttendanceApi.getTodayAttendance();
      const todaySummary = AttendanceViewModelFactory.createTodaySummary(
        response.records,
        response.has_checked_in,
        response.has_checked_out,
        response.total_work_hours
      );
      setSummary(todaySummary);
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : '無法取得考勤記錄';
      setError(errorMessage);
      setSummary(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTodayAttendance();
  }, [fetchTodayAttendance]);

  const handleCheckIn = useCallback(
    async (
      checkType: CheckType,
      location?: {
        latitude: number;
        longitude: number;
        address?: string;
      }
    ) => {
      setCheckingIn(true);
      try {
        await AttendanceApi.checkIn({
          check_type: checkType,
          latitude: location?.latitude,
          longitude: location?.longitude,
          address: location?.address,
        });
        
        // 打卡成功後自動刷新資料
        await fetchTodayAttendance();
      } catch (err) {
        throw err;
      } finally {
        setCheckingIn(false);
      }
    },
    [fetchTodayAttendance]
  );

  const refresh = useCallback(() => {
    fetchTodayAttendance();
  }, [fetchTodayAttendance]);

  return {
    summary,
    loading,
    error,
    checkingIn,
    handleCheckIn,
    refresh,
  };
};
