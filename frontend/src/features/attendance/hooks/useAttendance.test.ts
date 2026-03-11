import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import React from 'react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@store/authSlice';
import { useAttendance } from './useAttendance';
import { AttendanceApi } from '../api/AttendanceApi';
import type { AttendanceRecordDto } from '../api/AttendanceTypes';

// Mock AttendanceApi class
vi.mock('../api/AttendanceApi', () => ({
  AttendanceApi: {
    getTodayAttendance: vi.fn(),
    checkIn: vi.fn(),
    checkOut: vi.fn(),
  },
}));

/**
 * 建立帶 Redux store 的 wrapper（含 mock user）
 */
const createWrapper = () => {
  const store = configureStore({
    reducer: { auth: authReducer },
    preloadedState: {
      auth: {
        user: { id: 'user-1', employeeId: 'emp-1', username: 'test', fullName: '王小明' } as any,
        token: 'mock-token',
        isAuthenticated: true,
        isLoading: false,
        error: null,
      },
    },
  });
  return ({ children }: { children: React.ReactNode }) =>
    React.createElement(Provider, { store } as any, children);
};

describe('useAttendance', () => {
  const mockRecord: AttendanceRecordDto = {
    id: '1',
    employeeId: 'emp-1',
    employeeName: '王小明',
    checkType: 'CHECK_IN',
    checkTime: '2024-12-08T09:00:00Z',
    status: 'NORMAL',
    createdAt: '2024-12-08T09:00:00Z',
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [],
        hasCheckedIn: false,
        hasCheckedOut: false,
      });

      const { result } = renderHook(() => useAttendance(), { wrapper: createWrapper() });

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
        hasCheckedIn: true,
        hasCheckedOut: false,
        totalWorkHours: 8.5,
      });

      const { result } = renderHook(() => useAttendance(), { wrapper: createWrapper() });

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

      const { result } = renderHook(() => useAttendance(), { wrapper: createWrapper() });

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
        hasCheckedIn: false,
        hasCheckedOut: false,
      });

      vi.mocked(AttendanceApi.checkIn).mockResolvedValue({
        record: mockRecord,
        message: '打卡成功',
      } as any);

      const { result } = renderHook(() => useAttendance(), { wrapper: createWrapper() });

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      await act(async () => {
        await result.current.handleCheckIn('CHECK_IN');
      });

      expect(AttendanceApi.checkIn).toHaveBeenCalledWith({
        employeeId: 'emp-1',
        latitude: undefined,
        longitude: undefined,
        address: undefined,
      });
      expect(result.current.checkingIn).toBe(false);
    });

    it('打卡過程中checkingIn應該為true', async () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [],
        hasCheckedIn: false,
        hasCheckedOut: false,
      });

      let resolveCheckIn: (value: any) => void;
      const checkInPromise = new Promise((resolve) => {
        resolveCheckIn = resolve;
      });

      vi.mocked(AttendanceApi.checkIn).mockReturnValue(checkInPromise as any);

      const { result } = renderHook(() => useAttendance(), { wrapper: createWrapper() });

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      act(() => {
        result.current.handleCheckIn('CHECK_IN');
      });

      expect(result.current.checkingIn).toBe(true);

      // 完成打卡（包含後續 refresh 呼叫）
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [mockRecord],
        hasCheckedIn: true,
        hasCheckedOut: false,
      });

      await act(async () => {
        resolveCheckIn!({
          record: mockRecord,
          message: '打卡成功',
        });
      });

      await waitFor(() => {
        expect(result.current.checkingIn).toBe(false);
      });
    });

    it('應該正確處理打卡錯誤', async () => {
      vi.mocked(AttendanceApi.getTodayAttendance).mockResolvedValue({
        records: [],
        hasCheckedIn: false,
        hasCheckedOut: false,
      });

      const errorMessage = '打卡失敗';
      vi.mocked(AttendanceApi.checkIn).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useAttendance(), { wrapper: createWrapper() });

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
        hasCheckedIn: false,
        hasCheckedOut: false,
      });

      vi.mocked(AttendanceApi.checkIn).mockResolvedValue({
        record: mockRecord,
        message: '打卡成功',
      } as any);

      const { result } = renderHook(() => useAttendance(), { wrapper: createWrapper() });

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
        employeeId: 'emp-1',
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
        hasCheckedIn: true,
        hasCheckedOut: false,
      });

      const { result } = renderHook(() => useAttendance(), { wrapper: createWrapper() });

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(AttendanceApi.getTodayAttendance).toHaveBeenCalledTimes(1);

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
          hasCheckedIn: false,
          hasCheckedOut: false,
        })
        .mockResolvedValueOnce({
          records: [mockRecord],
          hasCheckedIn: true,
          hasCheckedOut: false,
        });

      vi.mocked(AttendanceApi.checkIn).mockResolvedValue({
        record: mockRecord,
        message: '打卡成功',
      } as any);

      const { result } = renderHook(() => useAttendance(), { wrapper: createWrapper() });

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      await act(async () => {
        await result.current.handleCheckIn('CHECK_IN');
      });

      await waitFor(() => {
        expect(result.current.summary?.hasCheckedIn).toBe(true);
      });

      expect(AttendanceApi.getTodayAttendance).toHaveBeenCalledTimes(2);
    });
  });
});
