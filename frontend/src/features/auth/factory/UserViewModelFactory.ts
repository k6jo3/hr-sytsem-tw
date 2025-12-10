/**
 * User & Role ViewModel Factory
 * Domain Code: HR01
 * 將 API DTO 轉換為前端 ViewModel
 */

import dayjs from 'dayjs';
import type { UserDto, RoleDto, PermissionDto, UserStatus } from '../api/AuthTypes';
import type {
  UserProfile,
  UserViewModel,
  RoleViewModel,
  PermissionViewModel,
  PermissionTreeData,
} from '../model/UserProfile';

// ========== Status Mappings ==========

const STATUS_LABELS: Record<UserStatus, string> = {
  ACTIVE: '啟用',
  INACTIVE: '停用',
  LOCKED: '鎖定',
  DELETED: '已刪除',
};

const STATUS_COLORS: Record<UserStatus, string> = {
  ACTIVE: 'success',
  INACTIVE: 'default',
  LOCKED: 'error',
  DELETED: 'default',
};

const ROLE_LABELS: Record<string, string> = {
  ADMIN: '系統管理員',
  HR_ADMIN: 'HR管理員',
  HR_MANAGER: 'HR主管',
  MANAGER: '部門主管',
  EMPLOYEE: '一般員工',
};

const MODULE_LABELS: Record<string, string> = {
  IAM: '身份認證管理',
  ORG: '組織員工管理',
  ATT: '考勤管理',
  PAY: '薪資管理',
  INS: '保險管理',
  PRJ: '專案管理',
  TMS: '工時管理',
  PFM: '績效管理',
  RCT: '招募管理',
  TRN: '訓練管理',
  WFL: '簽核流程',
  NTF: '通知服務',
  DOC: '文件管理',
  RPT: '報表分析',
};

// ========== Helper Functions ==========

const formatDateTime = (dateString?: string): string => {
  if (!dateString) return '-';
  return dayjs(dateString).format('YYYY-MM-DD HH:mm');
};

const formatDate = (dateString: string): string => {
  return dayjs(dateString).format('YYYY-MM-DD');
};

const mapRolesToDisplay = (roles: string[]): string => {
  if (roles.length === 0) return '無角色';
  return roles.map((role) => ROLE_LABELS[role] ?? role).join(', ');
};

// ========== Factory Class ==========

export class UserViewModelFactory {
  // ========== User Profile (for current user) ==========

  /**
   * Convert UserDto to UserProfile ViewModel (for logged-in user)
   */
  static createProfileFromDTO(dto: UserDto): UserProfile {
    return {
      id: dto.id,
      username: dto.username,
      email: dto.email,
      fullName: dto.display_name || `${dto.first_name ?? ''} ${dto.last_name ?? ''}`.trim(),
      roles: dto.role_list,
      displayRoles: mapRolesToDisplay(dto.role_list),
      isAdmin: dto.role_list.includes('ADMIN') || dto.role_list.includes('HR_ADMIN'),
      statusLabel: STATUS_LABELS[dto.status] ?? '未知',
      statusColor: STATUS_COLORS[dto.status] ?? 'default',
      displayStatus: STATUS_LABELS[dto.status] ?? '未知',
      avatarUrl: dto.avatar_url,
    };
  }

  // Legacy method for backward compatibility
  static createFromDTO(dto: UserDto): UserProfile {
    return this.createProfileFromDTO(dto);
  }

  // ========== User Management ==========

  /**
   * Convert UserDto to UserViewModel (for user management)
   */
  static createUserViewModelFromDTO(dto: UserDto): UserViewModel {
    const isAdmin = dto.role_list.includes('ADMIN') || dto.role_list.includes('HR_ADMIN');

    return {
      userId: dto.id,
      username: dto.username,
      email: dto.email,
      displayName: dto.display_name || `${dto.first_name ?? ''} ${dto.last_name ?? ''}`.trim(),
      firstName: dto.first_name,
      lastName: dto.last_name,
      employeeId: dto.employee_id,
      status: dto.status,
      statusLabel: STATUS_LABELS[dto.status] ?? '未知',
      statusColor: STATUS_COLORS[dto.status] ?? 'default',
      roles: dto.role_list,
      roleIds: dto.role_ids,
      displayRoles: mapRolesToDisplay(dto.role_list),
      isAdmin,
      isLocked: dto.status === 'LOCKED',
      mustChangePassword: dto.must_change_password,
      avatarUrl: dto.avatar_url,
      lastLoginAt: dto.last_login_at,
      lastLoginAtDisplay: formatDateTime(dto.last_login_at),
      createdAt: dto.created_at,
      createdAtDisplay: formatDate(dto.created_at),
      canEdit: true,
      canDelete: !isAdmin, // System admins cannot be deleted
      canResetPassword: true,
    };
  }

  /**
   * Batch convert User DTOs
   */
  static createUserListFromDTOs(dtos: UserDto[]): UserViewModel[] {
    return dtos.map((dto) => this.createUserViewModelFromDTO(dto));
  }

  // Legacy method
  static createListFromDTO(dtos: UserDto[]): UserProfile[] {
    return dtos.map((dto) => this.createFromDTO(dto));
  }

  static createListFromDTOs(dtos: UserDto[]): UserProfile[] {
    return this.createListFromDTO(dtos);
  }

  // ========== Role Management ==========

  /**
   * Convert RoleDto to RoleViewModel
   */
  static createRoleViewModelFromDTO(dto: RoleDto): RoleViewModel {
    return {
      roleId: dto.id,
      roleCode: dto.role_code,
      roleName: dto.role_name,
      description: dto.description,
      isSystem: dto.is_system,
      isActive: dto.is_active,
      statusLabel: dto.is_active ? '啟用' : '停用',
      statusColor: dto.is_active ? 'success' : 'default',
      permissionIds: dto.permission_ids,
      permissionCount: dto.permission_ids.length,
      userCount: dto.user_count,
      userCountDisplay: `${dto.user_count} 人`,
      createdAt: dto.created_at,
      createdAtDisplay: formatDate(dto.created_at),
      canEdit: !dto.is_system, // System roles cannot be edited
      canDelete: !dto.is_system && dto.user_count === 0, // Can only delete if no users
    };
  }

  /**
   * Batch convert Role DTOs
   */
  static createRoleListFromDTOs(dtos: RoleDto[]): RoleViewModel[] {
    return dtos.map((dto) => this.createRoleViewModelFromDTO(dto));
  }

  // ========== Permission Management ==========

  /**
   * Convert PermissionDto to PermissionViewModel
   */
  static createPermissionViewModelFromDTO(dto: PermissionDto): PermissionViewModel {
    return {
      permissionId: dto.id,
      permissionCode: dto.permission_code,
      permissionName: dto.permission_name,
      description: dto.description,
      module: dto.module,
      moduleLabel: MODULE_LABELS[dto.module] ?? dto.module,
      parentId: dto.parent_id,
      children: dto.children?.map((child) => this.createPermissionViewModelFromDTO(child)),
      sortOrder: dto.sort_order,
      isLeaf: !dto.children || dto.children.length === 0,
    };
  }

  /**
   * Convert PermissionDto list to Tree Data for Ant Design Tree
   */
  static createPermissionTreeData(dtos: PermissionDto[]): PermissionTreeData[] {
    const convertToTreeNode = (dto: PermissionDto): PermissionTreeData => ({
      key: dto.id,
      title: dto.permission_name,
      children: dto.children?.map(convertToTreeNode),
      isLeaf: !dto.children || dto.children.length === 0,
    });

    return dtos.map(convertToTreeNode);
  }

  /**
   * Batch convert Permission DTOs
   */
  static createPermissionListFromDTOs(dtos: PermissionDto[]): PermissionViewModel[] {
    return dtos.map((dto) => this.createPermissionViewModelFromDTO(dto));
  }

  // ========== Helper Methods ==========

  private static mapStatusLabel(status: UserStatus): string {
    return STATUS_LABELS[status] ?? '未知';
  }

  private static mapStatusColor(status: UserStatus): string {
    return STATUS_COLORS[status] ?? 'default';
  }

  private static mapRoles(roles: string[]): string {
    return mapRolesToDisplay(roles);
  }
}

export default UserViewModelFactory;
