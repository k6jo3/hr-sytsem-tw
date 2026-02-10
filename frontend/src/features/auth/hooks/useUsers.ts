/**
 * User Management Hook (使用者管理 Hook)
 * Domain Code: HR01
 */

import { useCallback, useEffect, useState } from 'react';
import type {
  CreateUserRequest,
  GetUsersRequest,
  RoleDto,
  UpdateUserRequest,
  UserDto,
} from '../api/AuthTypes';
import { RoleApi } from '../api/RoleApi';
import { UserApi } from '../api/UserApi';
import { UserViewModelFactory } from '../factory/UserViewModelFactory';
import type { UserListViewModel } from '../model/UserProfile';


export interface UseUsersParams {
  page?: number;
  page_size?: number;
  keyword?: string;
  status?: 'ACTIVE' | 'INACTIVE' | 'LOCKED';
  role_id?: string;
}

export interface UseUsersResult {
  users: UserListViewModel[];
  roles: RoleDto[];
  total: number;
  loading: boolean;
  error: string | null;
  refresh: () => Promise<void>;
  createUser: (data: CreateUserRequest) => Promise<void>;
  updateUser: (userId: string, data: UpdateUserRequest) => Promise<void>;
  deactivateUser: (userId: string) => Promise<void>;
  activateUser: (userId: string) => Promise<void>;
  unlockUser: (userId: string) => Promise<void>;
  deleteUser: (userId: string) => Promise<void>;
  resetPassword: (userId: string, newPassword: string, forceChange: boolean) => Promise<void>;
  batchDeactivate: (userIds: string[]) => Promise<void>;
}

/**
 * 使用者管理 Hook
 */
export const useUsers = (params: UseUsersParams = {}): UseUsersResult => {
  const [users, setUsers] = useState<UserListViewModel[]>([]);
  const [roles, setRoles] = useState<RoleDto[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 載入使用者列表
  const fetchUsers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const request: GetUsersRequest = {
        page: params.page || 1,
        page_size: params.page_size || 10,
        keyword: params.keyword,
        status: params.status,
        role_id: params.role_id,
      };
      const response = await UserApi.getUsers(request);
      const viewModels = response.content.map((user: UserDto) =>
        UserViewModelFactory.createUserListItem(user)
      );
      setUsers(viewModels);
      setTotal(response.pagination.total);
    } catch (err) {
      setError(err instanceof Error ? err.message : '載入使用者列表失敗');
    } finally {
      setLoading(false);
    }
  }, [params.page, params.page_size, params.keyword, params.status, params.role_id]);

  // 載入角色列表 (供下拉選單使用)
  const fetchRoles = useCallback(async () => {
    try {
      const roleList = await RoleApi.getAllRoles();
      setRoles(roleList);
    } catch (err) {
      console.error('載入角色列表失敗', err);
    }
  }, []);

  // 初始載入
  useEffect(() => {
    fetchUsers();
    fetchRoles();
  }, [fetchUsers, fetchRoles]);

  // 重新整理
  const refresh = useCallback(async () => {
    await fetchUsers();
  }, [fetchUsers]);

  // 建立使用者
  const createUser = useCallback(async (data: CreateUserRequest) => {
    await UserApi.createUser(data);
    await fetchUsers();
  }, [fetchUsers]);

  // 更新使用者
  const updateUser = useCallback(async (userId: string, data: UpdateUserRequest) => {
    await UserApi.updateUser(userId, data);
    await fetchUsers();
  }, [fetchUsers]);

  // 停用使用者
  const deactivateUser = useCallback(async (userId: string) => {
    await UserApi.deactivateUser(userId);
    await fetchUsers();
  }, [fetchUsers]);

  // 啟用使用者
  const activateUser = useCallback(async (userId: string) => {
    await UserApi.activateUser(userId);
    await fetchUsers();
  }, [fetchUsers]);

  // 解鎖使用者
  const unlockUser = useCallback(async (userId: string) => {
    await UserApi.unlockUser(userId);
    await fetchUsers();
  }, [fetchUsers]);

  // 刪除使用者
  const deleteUser = useCallback(async (userId: string) => {
    await UserApi.deleteUser(userId);
    await fetchUsers();
  }, [fetchUsers]);

  // 重置密碼
  const resetPassword = useCallback(async (
    userId: string,
    newPassword: string,
    forceChange: boolean
  ) => {
    await UserApi.resetPassword({
      user_id: userId,
      new_password: newPassword,
      force_change: forceChange,
    });
  }, []);

  // 批次停用
  const batchDeactivate = useCallback(async (userIds: string[]) => {
    await UserApi.batchAction({
      user_ids: userIds,
      action: 'DEACTIVATE',
    });
    await fetchUsers();
  }, [fetchUsers]);

  return {
    users,
    roles,
    total,
    loading,
    error,
    refresh,
    createUser,
    updateUser,
    deactivateUser,
    activateUser,
    unlockUser,
    deleteUser,
    resetPassword,
    batchDeactivate,
  };
};
