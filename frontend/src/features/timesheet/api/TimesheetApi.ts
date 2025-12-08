import axios from 'axios';
import type {
  GetWeeklyTimesheetRequest,
  GetWeeklyTimesheetResponse,
  SubmitTimesheetRequest,
  SubmitTimesheetResponse,
} from './TimesheetTypes';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8087';

export const getWeeklyTimesheet = async (
  params: GetWeeklyTimesheetRequest
): Promise<GetWeeklyTimesheetResponse> => {
  const token = localStorage.getItem('accessToken');
  const response = await axios.get<GetWeeklyTimesheetResponse>(
    `${API_BASE_URL}/api/v1/timesheets/weekly`,
    { params, headers: { Authorization: `Bearer ${token}` } }
  );
  return response.data;
};

export const submitTimesheet = async (
  request: SubmitTimesheetRequest
): Promise<SubmitTimesheetResponse> => {
  const token = localStorage.getItem('accessToken');
  const response = await axios.post<SubmitTimesheetResponse>(
    `${API_BASE_URL}/api/v1/timesheets/submit`,
    request,
    { headers: { Authorization: `Bearer ${token}` } }
  );
  return response.data;
};
