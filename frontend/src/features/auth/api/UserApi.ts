/**
 * User Management API (使用者管理 API)
 * Domain Code: HR01
 */

import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { guardEnum } from '../../../shared/utils/adapterGuard';
import { MockAuthApi } from './MockAuthApi';
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

/**
 * 後端 role_code → 前端 SystemRole 映射
 */
const ROLE_CODE_MAP: Record<string, string> = {
  SYSTEM_ADMIN: 'ADMIN',
  HR_ADMIN: 'HR',
  MANAGER: 'MANAGER',
  EMPLOYEE: 'EMPLOYEE',
  PROJECT_MANAGER: 'PM',
};

/**
 * 將後端角色代碼轉換為前端 SystemRole
 */
function adaptRoles(roles: string[]): string[] {
  return roles.map(role => ROLE_CODE_MAP[role] ?? role);
}

/**
 * 將後端 camelCase 使用者項目轉換為前端 snake_case UserDto
 */
function adaptUserItem(raw: any): UserDto {
  const rawRoles = raw.roles ?? raw.role_list ?? [];
  return {
    id: raw.userId ?? raw.id,
    username: raw.username ?? '',
    email: raw.email ?? '',
    display_name: raw.displayName ?? raw.display_name ?? '',
    first_name: raw.firstName ?? raw.first_name,
    last_name: raw.lastName ?? raw.last_name,
    employee_id: raw.employeeId ?? raw.employee_id,
    tenant_id: raw.tenantId ?? raw.tenant_id,
    status: guardEnum('user.status', raw.status, ['ACTIVE', 'INACTIVE', 'PENDING', 'LOCKED'] as const, 'ACTIVE'),
    role_list: adaptRoles(rawRoles),
    role_ids: raw.roleIds ?? raw.role_ids ?? [],
    avatar_url: raw.avatarUrl ?? raw.avatar_url,
    must_change_password: raw.mustChangePassword ?? raw.must_change_password ?? false,
    last_login_at: raw.lastLoginAt ?? raw.last_login_at,
    password_changed_at: raw.passwordChangedAt ?? raw.password_changed_at,
    created_at: raw.createdAt ?? raw.created_at ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

/**
 * 將後端使用者列表回應轉換為前端 GetUsersResponse
 */
function adaptGetUsersResponse(raw: any): GetUsersResponse {
  const items = raw.items ?? raw.content ?? [];
  return {
    content: items.map(adaptUserItem),
    pagination: {
      page: raw.page ?? raw.number ?? 1,
      page_size: raw.size ?? raw.page_size ?? 20,
      total: raw.total ?? raw.totalElements ?? 0,
      total_pages: raw.totalPages ?? raw.total_pages ?? 0,
    },
  };
}

/**
 * 將前端 snake_case 請求轉換為後端 camelCase 格式
 */
function adaptCreateRequest(request: CreateUserRequest): Record<string, unknown> {
  return {
    username: request.username,
    email: request.email,
    password: request.password,
    displayName: request.display_name,
    firstName: request.first_name,
    lastName: request.last_name,
    employeeId: request.employee_id,
    roleIds: request.role_ids,
    mustChangePassword: request.must_change_password,
  };
}

function adaptUpdateRequest(request: UpdateUserRequest): Record<string, unknown> {
  return {
    email: request.email,
    displayName: request.display_name,
    firstName: request.first_name,
    lastName: request.last_name,
    roles: request.role_ids,
    employeeId: request.employee_id,
    mustChangePassword: undefined,
  };
}

export const UserApi = {
  // ========== Query ==========

  /**
   * 取得使用者列表
   */
  getUsers: async (params?: GetUsersRequest): Promise<GetUsersResponse> => {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.getUsers(params || {});
    const raw = await apiClient.get(BASE_URL, { params });
    return adaptGetUsersResponse(raw);
  },

  /**
   * 取得單一使用者
   */
  getUser: async (userId: string): Promise<UserDto> => {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.getUser(userId);
    const raw = await apiClient.get(`${BASE_URL}/${userId}`);
    return adaptUserItem(raw);
  },

  // ========== Command ==========

  /**
   * 建立使用者
   */
  createUser: async (request: CreateUserRequest): Promise<UserDto> => {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.createUser(request);
    const raw = await apiClient.post(BASE_URL, adaptCreateRequest(request));
    return adaptUserItem(raw);
  },

  /**
   * 更新使用者
   */
  updateUser: async (userId: string, request: UpdateUserRequest): Promise<UserDto> => {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.updateUser(userId, request);
    const raw = await apiClient.put(`${BASE_URL}/${userId}`, adaptUpdateRequest(request));
    return adaptUserItem(raw);
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
    return apiClient.post<SuccessResponse>(
      `/auth/users/${request.user_id}/password/reset`,
      {
        newPassword: request.new_password,
        forceChangeOnNextLogin: request.force_change,
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
