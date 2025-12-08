import { useState } from 'react';
import { AttendanceApi } from '../api';
import type { CheckInRequest } from '../api/AttendanceTypes';

/**
 * Attendance Hook (考勤管理 Hook)
 * 處理考勤相關的業務邏輯
 */
export const useAttendance = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  /**
   * 打卡
   */
  const checkIn = async (request: CheckInRequest) => {
    setLoading(true);
    setError(null);
    try {
      const result = await AttendanceApi.checkIn(request);
      return result;
    } catch (err) {
      setError(err as Error);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  /**
   * 取得考勤記錄
   */
  const getAttendanceList = async (params?: { startDate?: string; endDate?: string }) => {
    setLoading(true);
    setError(null);
    try {
      const result = await AttendanceApi.getAttendanceList(params);
      return result;
    } catch (err) {
      setError(err as Error);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return {
    loading,
    error,
    checkIn,
    getAttendanceList,
  };
};
