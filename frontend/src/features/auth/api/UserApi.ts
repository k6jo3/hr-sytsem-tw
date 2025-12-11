/**
 * User Management API (使用者管理 API)
 * Domain Code: HR01
 */

import { apiClient } from '@shared/api';
import type {
  UserDto,
  CreateUserRequest,
  UpdateUserRequest,
  GetUsersRequest,
  GetUsersResponse,
  BatchUserActionRequest,
  AdminResetPasswordRequest,
  SuccessResponse,
} from './AuthTypes';

const BASE_URL = '/users';

export const UserApi = {
  // ========== Query ==========

  /**
   * 取得使用者列表
   */
  getUsers: async (params?: GetUsersRequest): Promise<GetUsersResponse> => {
    return apiClient.get<GetUsersResponse>(BASE_URL, { params });
  },

  /**
   * 取得單一使用者
   */
  getUser: async (userId: string): Promise<UserDto> => {
    return apiClient.get<UserDto>(`${BASE_URL}/${userId}`);
  },

  // ========== Command ==========

  /**
   * 建立使用者
   */
  createUser: async (request: CreateUserRequest): Promise<UserDto> => {
    return apiClient.post<UserDto>(BASE_URL, request);
  },

  /**
   * 更新使用者
   */
  updateUser: async (userId: string, request: UpdateUserRequest): Promise<UserDto> => {
    return apiClient.put<UserDto>(`${BASE_URL}/${userId}`, request);
  },

  /**
   * 停用使用者
   */
  deactivateUser: async (userId: string): Promise<SuccessResponse> => {
    return apiClient.put<SuccessResponse>(`${BASE_URL}/${userId}/deactivate`, {});
  },

  /**
   * 啟用使用者
   */
  activateUser: async (userId: string): Promise<SuccessResponse> => {
    return apiClient.put<SuccessResponse>(`${BASE_URL}/${userId}/activate`, {});
  },

  /**
   * 刪除使用者
   */
  deleteUser: async (userId: string): Promise<SuccessResponse> => {
    return apiClient.delete<SuccessResponse>(`${BASE_URL}/${userId}`);
  },

  /**
   * 批次操作使用者
   */
  batchAction: async (request: BatchUserActionRequest): Promise<SuccessResponse> => {
    return apiClient.post<SuccessResponse>(`${BASE_URL}/batch`, request);
  },

  /**
   * 管理員重置密碼
   */
  resetPassword: async (request: AdminResetPasswordRequest): Promise<SuccessResponse> => {
    return apiClient.put<SuccessResponse>(
      `${BASE_URL}/${request.user_id}/reset-password`,
      {
        new_password: request.new_password,
        force_change: request.force_change,
      }
    );
  },

  /**
   * 解鎖使用者
   */
  unlockUser: async (userId: string): Promise<SuccessResponse> => {
    return apiClient.put<SuccessResponse>(`${BASE_URL}/${userId}/unlock`, {});
  },
};
