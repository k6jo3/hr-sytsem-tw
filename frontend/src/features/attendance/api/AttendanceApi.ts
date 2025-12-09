import axios from 'axios';
import type {
  CheckInRequest,
  CheckInResponse,
  GetTodayAttendanceRequest,
  GetTodayAttendanceResponse,
  GetAttendanceHistoryRequest,
  GetAttendanceHistoryResponse,
} from './AttendanceTypes';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8083';

/**
 * Attendance API
 * 考勤相關的 API 呼叫
 */

/**
 * 打卡
 */
export const checkIn = async (request: CheckInRequest): Promise<CheckInResponse> => {
  const token = localStorage.getItem('accessToken');
  const response = await axios.post<CheckInResponse>(
    `${API_BASE_URL}/api/v1/attendance/check-in`,
    request,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

/**
 * 取得今日考勤記錄
 */
export const getTodayAttendance = async (
  params?: GetTodayAttendanceRequest
): Promise<GetTodayAttendanceResponse> => {
  const token = localStorage.getItem('accessToken');
  const response = await axios.get<GetTodayAttendanceResponse>(
    `${API_BASE_URL}/api/v1/attendance/today`,
    {
      params,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

/**
 * 取得考勤歷史記錄
 */
export const getAttendanceHistory = async (
  params?: GetAttendanceHistoryRequest
): Promise<GetAttendanceHistoryResponse> => {
  const token = localStorage.getItem('accessToken');
  const response = await axios.get<GetAttendanceHistoryResponse>(
    `${API_BASE_URL}/api/v1/attendance/history`,
    {
      params,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};
