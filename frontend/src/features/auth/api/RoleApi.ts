/**
 * Role Management API (角色管理 API)
 * Domain Code: HR01
 */

import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { MockAuthApi } from './MockAuthApi';
import type {
  RoleDto,
  CreateRoleRequest,
  UpdateRoleRequest,
  GetRolesRequest,
  GetRolesResponse,
  PermissionDto,
  GetPermissionsResponse,
  SuccessResponse,
} from './AuthTypes';

const ROLE_URL = '/roles';
const PERMISSION_URL = '/permissions';

export const RoleApi = {
  // ========== Role Query ==========

  /**
   * 取得角色列表
   */
  getRoles: async (params?: GetRolesRequest): Promise<GetRolesResponse> => {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.getRoles(params || {});
    return apiClient.get<GetRolesResponse>(ROLE_URL, { params });
  },

  /**
   * 取得單一角色
   */
  getRole: async (roleId: string): Promise<RoleDto> => {
    if (MockConfig.isEnabled('AUTH')) {
      const response = await MockAuthApi.getRoles({});
      const role = response.roles.find(r => r.id === roleId);
      if (!role) throw new Error('Role not found');
      return role;
    }
    return apiClient.get<RoleDto>(`${ROLE_URL}/${roleId}`);
  },

  /**
   * 取得所有角色 (不分頁，用於下拉選單)
   */
  getAllRoles: async (): Promise<RoleDto[]> => {
    if (MockConfig.isEnabled('AUTH')) {
      const response = await MockAuthApi.getRoles({});
      return response.roles;
    }
    const response = await apiClient.get<GetRolesResponse>(ROLE_URL, {
      params: { page_size: 1000 },
    });
    return response.roles;
  },

  // ========== Role Command ==========

  /**
   * 建立角色
   */
  createRole: async (request: CreateRoleRequest): Promise<RoleDto> => {
    return apiClient.post<RoleDto>(ROLE_URL, request);
  },

  /**
   * 更新角色
   */
  updateRole: async (roleId: string, request: UpdateRoleRequest): Promise<RoleDto> => {
    return apiClient.put<RoleDto>(`${ROLE_URL}/${roleId}`, request);
  },

  /**
   * 更新角色權限
   */
  updateRolePermissions: async (
    roleId: string,
    permissionIds: string[]
  ): Promise<SuccessResponse> => {
    return apiClient.put<SuccessResponse>(`${ROLE_URL}/${roleId}/permissions`, {
      permission_ids: permissionIds,
    });
  },

  /**
   * 刪除角色
   */
  deleteRole: async (roleId: string): Promise<SuccessResponse> => {
    return apiClient.delete<SuccessResponse>(`${ROLE_URL}/${roleId}`);
  },

  /**
   * 啟用/停用角色
   */
  toggleRoleStatus: async (roleId: string, isActive: boolean): Promise<SuccessResponse> => {
    return apiClient.patch<SuccessResponse>(`${ROLE_URL}/${roleId}`, { is_active: isActive });
  },

  // ========== Permission Query ==========

  /**
   * 取得權限列表 (樹狀結構)
   */
  getPermissions: async (): Promise<GetPermissionsResponse> => {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.getPermissions();
    return apiClient.get<GetPermissionsResponse>(PERMISSION_URL);
  },

  /**
   * 取得模組權限列表
   */
  getPermissionsByModule: async (module: string): Promise<PermissionDto[]> => {
    if (MockConfig.isEnabled('AUTH')) {
      const response = await MockAuthApi.getPermissions();
      return response.permissions.filter(p => p.module === module);
    }
    const response = await apiClient.get<GetPermissionsResponse>(PERMISSION_URL, {
      params: { module },
    });
    return response.permissions;
  },
};
