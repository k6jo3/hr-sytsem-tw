/**
 * User & Role ViewModels (使用者與角色視圖模型)
 * Domain Code: HR01
 * Frontend domain models for display
 */

import type { UserStatus } from '../api/AuthTypes';

// ========== User ViewModels ==========

/**
 * 使用者個人資料 ViewModel
 */
export interface UserProfile {
  id: string;
  username: string;
  email: string;
  fullName: string;
  employeeId?: string;
  roles: string[];
  displayRoles: string;
  isAdmin: boolean;
  statusLabel: string;
  statusColor: string;
  displayStatus: string;
  avatarUrl?: string;
}

/**
 * 使用者管理列表 ViewModel
 */
export interface UserViewModel {
  userId: string;
  username: string;
  email: string;
  displayName: string;
  firstName?: string;
  lastName?: string;
  employeeId?: string;
  status: UserStatus;
  statusLabel: string;
  statusColor: string;
  roles: string[];
  roleIds: string[];
  displayRoles: string;
  isAdmin: boolean;
  isLocked: boolean;
  mustChangePassword: boolean;
  avatarUrl?: string;
  lastLoginAt?: string;
  lastLoginAtDisplay?: string;
  createdAt: string;
  createdAtDisplay: string;
  canEdit: boolean;
  canDelete: boolean;
  canResetPassword: boolean;
}

/**
 * 使用者列表項目 ViewModel (List Item)
 * Used in UserTable
 */
export interface UserListViewModel {
  id: string;
  username: string;
  email: string;
  displayName: string;
  fullName: string;
  firstName?: string;
  lastName?: string;
  employeeId?: string;
  status: UserStatus;
  statusLabel: string;
  statusColor: string;
  roles: Array<{ id: string; name: string }>;
  roleLabels: string[];
  avatarUrl?: string;
  lastLoginAt?: string;
  lastLoginDisplay: string;
  mustChangePassword: boolean;
  createdAt: string;
}

// ========== Role ViewModels ==========

/**
 * 角色列表 ViewModel
 */
export interface RoleViewModel {
  roleId: string;
  roleCode: string;
  roleName: string;
  description?: string;
  isSystem: boolean;
  isActive: boolean;
  statusLabel: string;
  statusColor: string;
  permissionIds: string[];
  permissionCount: number;
  userCount: number;
  userCountDisplay: string;
  createdAt: string;
  createdAtDisplay: string;
  canEdit: boolean;
  canDelete: boolean;
}

// ========== Permission ViewModels ==========

/**
 * 權限樹節點 ViewModel
 */
export interface PermissionViewModel {
  permissionId: string;
  permissionCode: string;
  permissionName: string;
  description?: string;
  module: string;
  moduleLabel: string;
  parentId?: string;
  children?: PermissionViewModel[];
  sortOrder: number;
  isLeaf: boolean;
}

/**
 * 權限樹 Ant Design TreeData 格式
 */
export interface PermissionTreeData {
  key: string;
  title: string;
  children?: PermissionTreeData[];
  isLeaf?: boolean;
  disabled?: boolean;
}
