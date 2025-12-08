import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useAttendance } from './useAttendance';
import * as AttendanceApi from '../api/AttendanceApi';
import type { AttendanceRecordDto } from '../api/AttendanceTypes';

// Mock AttendanceApi
vi.mock('../api/AttendanceApi', () => ({
  getTodayAttendance: vi.fn(),
  checkIn: vi.fn(),
}));

describe('useAttendance', () => {
  const mockRecord: AttendanceRecordDto = {
    id: '1',
    employee_id: 'emp-1',
    employee_name: '王小明',
    check_type: 'CHECK_IN',
    check_time: '2024-12-08T09:00:00Z',
    status: 'NORMAL',
    created_at: '2024-12-08T09:00:00Z',
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [],
        has_checked_in: false,
        has_checked_out: false,
      });

      const { result } = renderHook(() => useAttendance());

      expect(result.current.loading).toBe(true);
      expect(result.current.summary).toBeNull();
      expect(result.current.error).toBeNull();
      expect(result.current.checkingIn).toBe(false);
    });
  });

  describe('取得今日考勤', () => {
    it('應該成功取得今日考勤資料', async () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [mockRecord],
        has_checked_in: true,
        has_checked_out: false,
        total_work_hours: 8.5,
      });

      const { result } = renderHook(() => useAttendance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.summary).not.toBeNull();
      expect(result.current.summary?.hasCheckedIn).toBe(true);
      expect(result.current.summary?.hasCheckedOut).toBe(false);
      expect(result.current.summary?.totalWorkHours).toBe(8.5);
      expect(result.current.summary?.records).toHaveLength(1);
      expect(result.current.error).toBeNull();
    });

    it('應該正確處理API錯誤', async () => {
      const errorMessage = '無法取得考勤記錄';
      vi.mocked(AttendanceApi.getTodayAttendance).mockRejectedValue(
        new Error(errorMessage)
      );

      const { result } = renderHook(() => useAttendance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.summary).toBeNull();
      expect(result.current.error).toBe(errorMessage);
    });
  });

  describe('打卡功能', () => {
    it('應該成功執行上班打卡', async () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [],
        has_checked_in: false,
        has_checked_out: false,
      });

      vi.mocked(AttendanceApi.checkIn).mockResolvedValue({
        record: mockRecord,
        message: '打卡成功',
      });

      const { result } = renderHook(() => useAttendance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      // 執行打卡
      await act(async () => {
        await result.current.handleCheckIn('CHECK_IN');
      });

      expect(AttendanceApi.checkIn).toHaveBeenCalledWith({
        check_type: 'CHECK_IN',
      });
      expect(result.current.checkingIn).toBe(false);
    });

    it('打卡過程中checkingIn應該為true', async () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [],
        has_checked_in: false,
        has_checked_out: false,
      });

      let resolveCheckIn: (value: any) => void;
      const checkInPromise = new Promise((resolve) => {
        resolveCheckIn = resolve;
      });

      vi.mocked(AttendanceApi.checkIn).mockReturnValue(checkInPromise as any);

      const { result } = renderHook(() => useAttendance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      // 開始打卡
      act(() => {
        result.current.handleCheckIn('CHECK_IN');
      });

      // 打卡中
      expect(result.current.checkingIn).toBe(true);

      // 完成打卡
      resolveCheckIn!({
        record: mockRecord,
        message: '打卡成功',
      });

      await waitFor(() => {
        expect(result.current.checkingIn).toBe(false);
      });
    });

    it('應該正確處理打卡錯誤', async () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [],
        has_checked_in: false,
        has_checked_out: false,
      });

      const errorMessage = '打卡失敗';
      vi.mocked(AttendanceApi.checkIn).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useAttendance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      await act(async () => {
        try {
          await result.current.handleCheckIn('CHECK_IN');
        } catch (err) {
          expect(err).toBeInstanceOf(Error);
        }
      });

      expect(result.current.checkingIn).toBe(false);
    });

    it('應該支援帶地理位置的打卡', async () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [],
        has_checked_in: false,
        has_checked_out: false,
      });

      vi.mocked(AttendanceApi.checkIn).mockResolvedValue({
        record: mockRecord,
        message: '打卡成功',
      });

      const { result } = renderHook(() => useAttendance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      await act(async () => {
        await result.current.handleCheckIn('CHECK_IN', {
          latitude: 25.033,
          longitude: 121.5654,
          address: '台北市信義區',
        });
      });

      expect(AttendanceApi.checkIn).toHaveBeenCalledWith({
        check_type: 'CHECK_IN',
        latitude: 25.033,
        longitude: 121.5654,
        address: '台北市信義區',
      });
    });
  });

  describe('重新整理', () => {
    it('應該能重新取得今日考勤資料', async () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [mockRecord],
        has_checked_in: true,
        has_checked_out: false,
      });

      const { result } = renderHook(() => useAttendance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(AttendanceApi.getTodayAttendance).toHaveBeenCalledTimes(1);

      // 呼叫 refresh
      act(() => {
        result.current.refresh();
      });

      await waitFor(() => {
        expect(AttendanceApi.getTodayAttendance).toHaveBeenCalledTimes(2);
      });
    });
  });

  describe('打卡成功後自動刷新', () => {
    it('打卡成功後應該自動刷新資料', async () => {
      vi.mocked(AttendanceApi.getTodayAttendance)
        .mockResolvedValueOnce({
          records: [],
          has_checked_in: false,
          has_checked_out: false,
        })
        .mockResolvedValueOnce({
          records: [mockRecord],
          has_checked_in: true,
          has_checked_out: false,
        });

      vi.mocked(AttendanceApi.checkIn).mockResolvedValue({
        record: mockRecord,
        message: '打卡成功',
      });

      const { result } = renderHook(() => useAttendance());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      // 執行打卡
      await act(async () => {
        await result.current.handleCheckIn('CHECK_IN');
      });

      // 應該自動刷新並更新狀態
      await waitFor(() => {
        expect(result.current.summary?.hasCheckedIn).toBe(true);
      });

      expect(AttendanceApi.getTodayAttendance).toHaveBeenCalledTimes(2);
    });
  });
});
