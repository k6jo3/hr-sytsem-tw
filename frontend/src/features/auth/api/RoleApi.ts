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

/**
 * 將後端 camelCase 角色項目轉換為前端 snake_case RoleDto
 */
function adaptRoleItem(raw: any): RoleDto {
  return {
    id: raw.roleId ?? raw.id,
    role_code: raw.roleCode ?? raw.role_code ?? '',
    role_name: raw.roleName ?? raw.role_name ?? '',
    description: raw.description,
    is_system: raw.isSystemRole ?? raw.is_system ?? false,
    is_active: raw.status === 'ACTIVE' || raw.is_active === true,
    permission_ids: raw.permissions ?? raw.permission_ids ?? [],
    user_count: raw.userCount ?? raw.user_count ?? 0,
    created_at: raw.createdAt ?? raw.created_at ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

/**
 * 將後端角色列表回應轉換為前端 GetRolesResponse
 * 後端可能回傳陣列或分頁物件
 */
function adaptGetRolesResponse(raw: any): GetRolesResponse {
  // 後端直接回傳陣列
  if (Array.isArray(raw)) {
    return {
      roles: raw.map(adaptRoleItem),
      pagination: {
        page: 1,
        page_size: raw.length,
        total: raw.length,
        total_pages: 1,
      },
    };
  }
  // 後端回傳分頁物件
  const items = raw.items ?? raw.content ?? raw.roles ?? [];
  return {
    roles: items.map(adaptRoleItem),
    pagination: {
      page: raw.page ?? raw.number ?? 1,
      page_size: raw.size ?? raw.page_size ?? 20,
      total: raw.total ?? raw.totalElements ?? 0,
      total_pages: raw.totalPages ?? raw.total_pages ?? 0,
    },
  };
}

/**
 * 將後端權限項目轉換為前端 PermissionDto
 */
function adaptPermissionItem(raw: any): PermissionDto {
  return {
    id: raw.permissionId ?? raw.id,
    permission_code: raw.permissionCode ?? raw.permission_code ?? '',
    permission_name: raw.permissionName ?? raw.permission_name ?? '',
    description: raw.description,
    module: raw.resource ?? raw.module ?? '',
    parent_id: raw.parentId ?? raw.parent_id,
    children: raw.children?.map(adaptPermissionItem),
    sort_order: raw.sortOrder ?? raw.sort_order ?? 0,
  };
}

/**
 * 將後端權限列表回應轉換為前端 GetPermissionsResponse
 */
function adaptGetPermissionsResponse(raw: any): GetPermissionsResponse {
  const items = Array.isArray(raw) ? raw : (raw.items ?? raw.permissions ?? []);
  return {
    permissions: items.map(adaptPermissionItem),
  };
}

export const RoleApi = {
  // ========== Role Query ==========

  /**
   * 取得角色列表
   */
  getRoles: async (params?: GetRolesRequest): Promise<GetRolesResponse> => {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.getRoles(params || {});
    const raw = await apiClient.get(ROLE_URL, { params });
    return adaptGetRolesResponse(raw);
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
    const raw = await apiClient.get(`${ROLE_URL}/${roleId}`);
    return adaptRoleItem(raw);
  },

  /**
   * 取得所有角色 (不分頁，用於下拉選單)
   */
  getAllRoles: async (): Promise<RoleDto[]> => {
    if (MockConfig.isEnabled('AUTH')) {
      const response = await MockAuthApi.getRoles({});
      return response.roles;
    }
    const raw = await apiClient.get(ROLE_URL, {
      params: { page_size: 1000 },
    });
    const response = adaptGetRolesResponse(raw);
    return response.roles;
  },

  // ========== Role Command ==========

  /**
   * 建立角色
   */
  createRole: async (request: CreateRoleRequest): Promise<RoleDto> => {
    const raw = await apiClient.post(ROLE_URL, request);
    return adaptRoleItem(raw);
  },

  /**
   * 更新角色
   */
  updateRole: async (roleId: string, request: UpdateRoleRequest): Promise<RoleDto> => {
    const raw = await apiClient.put(`${ROLE_URL}/${roleId}`, request);
    return adaptRoleItem(raw);
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
   * 修正：後端不支援 PATCH /roles/{roleId}，改為根據 isActive 判斷呼叫 activate 或 deactivate
   */
  toggleRoleStatus: async (roleId: string, isActive: boolean): Promise<SuccessResponse> => {
    if (isActive) {
      return apiClient.put<SuccessResponse>(`${ROLE_URL}/${roleId}/activate`, {});
    } else {
      return apiClient.put<SuccessResponse>(`${ROLE_URL}/${roleId}/deactivate`, {});
    }
  },

  // ========== Permission Query ==========

  /**
   * 取得權限列表 (樹狀結構)
   */
  getPermissions: async (): Promise<GetPermissionsResponse> => {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.getPermissions();
    const raw = await apiClient.get(PERMISSION_URL);
    return adaptGetPermissionsResponse(raw);
  },

  /**
   * 取得模組權限列表
   */
  getPermissionsByModule: async (module: string): Promise<PermissionDto[]> => {
    if (MockConfig.isEnabled('AUTH')) {
      const response = await MockAuthApi.getPermissions();
      return response.permissions.filter(p => p.module === module);
    }
    const raw = await apiClient.get(PERMISSION_URL, {
      params: { module },
    });
    const response = adaptGetPermissionsResponse(raw);
    return response.permissions;
  },
};
