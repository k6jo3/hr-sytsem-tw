/**
 * Role Management Hook (角色管理 Hook)
 * Domain Code: HR01
 */

import { useState, useEffect, useCallback } from 'react';
import { RoleApi } from '../api/RoleApi';
import { extractApiError } from '@shared/utils/errorUtils';
import type {
  RoleDto,
  PermissionDto,
  CreateRoleRequest,
  UpdateRoleRequest,
} from '../api/AuthTypes';

export interface UseRolesResult {
  roles: RoleDto[];
  permissions: PermissionDto[];
  selectedRole: RoleDto | null;
  loading: boolean;
  error: string | null;
  refresh: () => Promise<void>;
  selectRole: (role: RoleDto | null) => void;
  createRole: (data: CreateRoleRequest) => Promise<void>;
  updateRole: (roleId: string, data: UpdateRoleRequest) => Promise<void>;
  updateRolePermissions: (roleId: string, permissionIds: string[]) => Promise<void>;
  deleteRole: (roleId: string) => Promise<void>;
  toggleRoleStatus: (roleId: string, isActive: boolean) => Promise<void>;
}

/**
 * 角色管理 Hook
 */
export const useRoles = (): UseRolesResult => {
  const [roles, setRoles] = useState<RoleDto[]>([]);
  const [permissions, setPermissions] = useState<PermissionDto[]>([]);
  const [selectedRole, setSelectedRole] = useState<RoleDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 載入角色列表
  const fetchRoles = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await RoleApi.getRoles({ page_size: 100 });
      setRoles(response.roles);
    } catch (err) {
      const { message } = extractApiError(err, '載入角色列表失敗');
      setError(message);
    } finally {
      setLoading(false);
    }
  }, []);

  // 載入權限列表
  const fetchPermissions = useCallback(async () => {
    try {
      const response = await RoleApi.getPermissions();
      setPermissions(response.permissions);
    } catch (err) {
      console.error('[useRoles] 載入權限列表失敗', err);
    }
  }, []);

  // 初始載入
  useEffect(() => {
    fetchRoles();
    fetchPermissions();
  }, [fetchRoles, fetchPermissions]);

  // 重新整理
  const refresh = useCallback(async () => {
    await fetchRoles();
  }, [fetchRoles]);

  // 選擇角色
  const selectRole = useCallback((role: RoleDto | null) => {
    setSelectedRole(role);
  }, []);

  // 建立角色
  const createRole = useCallback(async (data: CreateRoleRequest) => {
    await RoleApi.createRole(data);
    await fetchRoles();
  }, [fetchRoles]);

  // 更新角色
  const updateRole = useCallback(async (roleId: string, data: UpdateRoleRequest) => {
    const updatedRole = await RoleApi.updateRole(roleId, data);
    await fetchRoles();
    // 如果更新的是當前選中的角色，更新選中狀態
    if (selectedRole?.id === roleId) {
      setSelectedRole(updatedRole);
    }
  }, [fetchRoles, selectedRole]);

  // 更新角色權限
  const updateRolePermissions = useCallback(async (roleId: string, permissionIds: string[]) => {
    await RoleApi.updateRolePermissions(roleId, permissionIds);
    // 重新載入角色以更新權限列表
    const updatedRole = await RoleApi.getRole(roleId);
    if (selectedRole?.id === roleId) {
      setSelectedRole(updatedRole);
    }
    await fetchRoles();
  }, [fetchRoles, selectedRole]);

  // 刪除角色
  const deleteRole = useCallback(async (roleId: string) => {
    await RoleApi.deleteRole(roleId);
    if (selectedRole?.id === roleId) {
      setSelectedRole(null);
    }
    await fetchRoles();
  }, [fetchRoles, selectedRole]);

  // 啟用/停用角色
  const toggleRoleStatus = useCallback(async (roleId: string, isActive: boolean) => {
    await RoleApi.toggleRoleStatus(roleId, isActive);
    await fetchRoles();
  }, [fetchRoles]);

  return {
    roles,
    permissions,
    selectedRole,
    loading,
    error,
    refresh,
    selectRole,
    createRole,
    updateRole,
    updateRolePermissions,
    deleteRole,
    toggleRoleStatus,
  };
};
