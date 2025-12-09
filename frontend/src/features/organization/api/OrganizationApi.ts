import axios from 'axios';
import type {
  GetEmployeeListRequest,
  GetEmployeeListResponse,
  GetEmployeeDetailResponse,
} from './OrganizationTypes';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8082';

/**
 * Organization API
 * 組織員工相關的 API 呼叫
 */

/**
 * 取得員工列表
 */
export const getEmployeeList = async (
  params?: GetEmployeeListRequest
): Promise<GetEmployeeListResponse> => {
  const token = localStorage.getItem('accessToken');
  const response = await axios.get<GetEmployeeListResponse>(
    `${API_BASE_URL}/api/v1/employees`,
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
 * 取得員工詳細資料
 */
export const getEmployeeDetail = async (
  id: string
): Promise<GetEmployeeDetailResponse> => {
  const token = localStorage.getItem('accessToken');
  const response = await axios.get<GetEmployeeDetailResponse>(
    `${API_BASE_URL}/api/v1/employees/${id}`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

/**
 * 新增員工
 */
export const createEmployee = async (data: any): Promise<any> => {
  const token = localStorage.getItem('accessToken');
  const response = await axios.post(`${API_BASE_URL}/api/v1/employees`, data, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.data;
};

/**
 * 更新員工資料
 */
export const updateEmployee = async (id: string, data: any): Promise<any> => {
  const token = localStorage.getItem('accessToken');
  const response = await axios.put(
    `${API_BASE_URL}/api/v1/employees/${id}`,
    data,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return response.data;
};

/**
 * 刪除員工
 */
export const deleteEmployee = async (id: string): Promise<void> => {
  const token = localStorage.getItem('accessToken');
  await axios.delete(`${API_BASE_URL}/api/v1/employees/${id}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
};
