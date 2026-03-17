import { RootState } from '@/store';
import { useCallback, useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { AttendanceApi } from '../api/AttendanceApi';
import type { CheckType } from '../api/AttendanceTypes';
import { AttendanceViewModelFactory } from '../factory/AttendanceViewModelFactory';
import type { TodayAttendanceSummary } from '../model/AttendanceRecordViewModel';

/**
 * useAttendance Hook
 * 管理考勤打卡的資料取得與狀態
 */
export const useAttendance = () => {
  const [summary, setSummary] = useState<TodayAttendanceSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [checkingIn, setCheckingIn] = useState(false);
  const user = useSelector((state: RootState) => state.auth.user);

  const fetchTodayAttendance = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await AttendanceApi.getTodayAttendance();
      const todaySummary = AttendanceViewModelFactory.createTodaySummary(
        response.records,
        response.hasCheckedIn,
        response.hasCheckedOut,
        response.totalWorkHours
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
        if (!user?.employeeId) {
          throw new Error('未找到員工 ID');
        }

        if (checkType === 'CHECK_IN') {
          await AttendanceApi.checkIn({
            employeeId: user.employeeId,
            latitude: location?.latitude,
            longitude: location?.longitude,
            address: location?.address,
          });
        } else if (checkType === 'CHECK_OUT') {
          await AttendanceApi.checkOut({
            employeeId: user.employeeId,
            latitude: location?.latitude,
            longitude: location?.longitude,
            address: location?.address,
          });
        } else {
          throw new Error('不支援的打卡類型: ' + checkType);
        }
        
        // 打卡成功後自動刷新資料
        await fetchTodayAttendance();
      } catch (err) {
        throw err;
      } finally {
        setCheckingIn(false);
      }
    },
    [user, fetchTodayAttendance]
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
