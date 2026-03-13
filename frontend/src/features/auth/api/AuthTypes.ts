/**
 * Authentication & User Management DTOs (認證與使用者管理資料傳輸物件)
 * Domain Code: HR01
 */

// ========== Enums ==========

/**
 * 使用者狀態
 */
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'LOCKED' | 'PENDING' | 'DELETED';

/**
 * SSO 提供者
 */
export type SsoProvider = 'GOOGLE' | 'MICROSOFT';

// ========== Auth DTOs ==========

/**
 * 登入請求
 */
export interface LoginRequest {
  username: string;
  password: string;
  remember?: boolean;
  tenant_id?: string;
}

/**
 * 登入回應
 */
export interface LoginResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  user: UserDto;
}

/**
 * SSO 登入請求
 */
export interface SsoLoginRequest {
  provider: SsoProvider;
  redirect_uri: string;
}

/**
 * SSO 回調請求
 */
export interface SsoCallbackRequest {
  provider: SsoProvider;
  code: string;
  state: string;
}

/**
 * 變更密碼請求
 */
export interface ChangePasswordRequest {
  old_password: string;
  new_password: string;
  confirm_password: string;
}

/**
 * 重置密碼請求 (忘記密碼)
 */
export interface ForgotPasswordRequest {
  email: string;
}

/**
 * 重置密碼確認請求
 */
export interface ResetPasswordRequest {
  token: string;
  new_password: string;
  confirm_password: string;
}

// ========== User DTOs ==========

/**
 * 使用者 DTO
 */
export interface UserDto {
  id: string;
  username: string;
  email: string;
  display_name: string;
  first_name?: string;
  last_name?: string;
  employee_id?: string;
  tenant_id?: string;
  status: UserStatus;
  role_list: string[];
  role_ids: string[];
  avatar_url?: string;
  must_change_password: boolean;
  last_login_at?: string;
  password_changed_at?: string;
  created_at: string;
  updated_at: string;
}

/**
 * 建立使用者請求
 */
export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  display_name: string;
  first_name: string;
  last_name: string;
  employee_id?: string;
  role_ids: string[];
  must_change_password?: boolean;
}

/**
 * 更新使用者請求
 */
export interface UpdateUserRequest {
  email?: string;
  display_name?: string;
  first_name: string;
  last_name: string;
  employee_id?: string;
  role_ids?: string[];
}

/**
 * 查詢使用者列表請求
 */
export interface GetUsersRequest {
  keyword?: string;
  status?: UserStatus;
  role_id?: string;
  page?: number;
  page_size?: number;
  sort_by?: string;
  sort_order?: 'asc' | 'desc';
}

/**
 * 批次操作請求
 */
export interface BatchUserActionRequest {
  user_ids: string[];
  action: 'ACTIVATE' | 'DEACTIVATE' | 'DELETE';
}

/**
 * 管理員重置密碼請求
 */
export interface AdminResetPasswordRequest {
  user_id: string;
  new_password: string;
  force_change: boolean;
}

// ========== Role DTOs ==========

/**
 * 角色 DTO
 */
export interface RoleDto {
  id: string;
  role_code: string;
  role_name: string;
  description?: string;
  is_system: boolean;
  is_active: boolean;
  permission_ids: string[];
  user_count: number;
  created_at: string;
  updated_at: string;
}

/**
 * 建立角色請求
 */
export interface CreateRoleRequest {
  role_code: string;
  role_name: string;
  description?: string;
  permission_ids: string[];
}

/**
 * 更新角色請求
 */
export interface UpdateRoleRequest {
  role_name?: string;
  description?: string;
  permission_ids?: string[];
  is_active?: boolean;
}

/**
 * 查詢角色列表請求
 */
export interface GetRolesRequest {
  keyword?: string;
  is_active?: boolean;
  page?: number;
  page_size?: number;
}

// ========== Permission DTOs ==========

/**
 * 權限 DTO
 */
export interface PermissionDto {
  id: string;
  permission_code: string;
  permission_name: string;
  description?: string;
  module: string;
  parent_id?: string;
  children?: PermissionDto[];
  sort_order: number;
}

/**
 * 權限樹節點
 */
export interface PermissionTreeNode {
  key: string;
  title: string;
  children?: PermissionTreeNode[];
  isLeaf?: boolean;
}

// ========== Response Types ==========

/**
 * 分頁資訊
 */
export interface PaginationInfo {
  page: number;
  page_size: number;
  total: number;
  total_pages: number;
}

/**
 * 使用者列表回應
 */
export interface GetUsersResponse {
  content: UserDto[];
  pagination: PaginationInfo;
}

/**
 * 角色列表回應
 */
export interface GetRolesResponse {
  roles: RoleDto[];
  pagination: PaginationInfo;
}

/**
 * 權限列表回應
 */
export interface GetPermissionsResponse {
  permissions: PermissionDto[];
}

/**
 * 操作成功回應
 */
export interface SuccessResponse {
  message: string;
}

/**
 * 登入表單資料
 */
export interface LoginFormData {
  username: string;
  password: string;
  remember: boolean;
}
