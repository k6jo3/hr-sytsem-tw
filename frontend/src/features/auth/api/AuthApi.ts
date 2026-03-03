import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type { LoginRequest, LoginResponse, UserDto } from './AuthTypes';
import { MockAuthApi } from './MockAuthApi';

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
 * 將後端 camelCase 登入回應轉換為前端 snake_case LoginResponse
 */
function adaptLoginResponse(raw: any): LoginResponse {
  const user = raw.user ?? {};
  return {
    access_token: raw.accessToken,
    refresh_token: raw.refreshToken,
    expires_in: raw.expiresIn,
    user: adaptUserDto(user),
  };
}

/**
 * 將後端 camelCase UserInfo 轉換為前端 snake_case UserDto
 */
function adaptUserDto(raw: any): UserDto {
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
    status: raw.status ?? 'ACTIVE',
    role_list: adaptRoles(rawRoles),
    role_ids: raw.roleIds ?? raw.role_ids ?? [],
    avatar_url: raw.avatarUrl ?? raw.avatar_url,
    must_change_password: raw.mustChangePassword ?? raw.must_change_password ?? false,
    last_login_at: raw.lastLoginAt ?? raw.last_login_at,
    password_changed_at: raw.passwordChangedAt ?? raw.password_changed_at,
    created_at: raw.createdAt ?? raw.created_at ?? new Date().toISOString(),
    updated_at: raw.updatedAt ?? raw.updated_at ?? new Date().toISOString(),
  };
}

/**
 * Authentication API (認證 API)
 * Domain Code: HR01
 */
export class AuthApi {
  private static readonly BASE_PATH = '/auth';

  /**
   * 登入
   */
  static async login(request: LoginRequest): Promise<LoginResponse> {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.login(request);
    const raw = await apiClient.post(`${this.BASE_PATH}/login`, request);
    return adaptLoginResponse(raw);
  }

  /**
   * 登出
   */
  static async logout(): Promise<void> {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.logout();
    return apiClient.post(`${this.BASE_PATH}/logout`, {});
  }

  /**
   * 忘記密碼
   */
  static async forgotPassword(email: string): Promise<void> {
     // Mock impl skipped for brevity
    return apiClient.post(`${this.BASE_PATH}/forgot-password`, { email });
  }

  /**
   * 刷新 Token
   */
  static async refreshToken(refreshToken: string): Promise<LoginResponse> {
    // Mock impl skipped
    const raw = await apiClient.post(`${this.BASE_PATH}/refresh`, { refreshToken });
    return adaptLoginResponse(raw);
  }

  /**
   * 取得當前使用者資訊
   */
  static async getCurrentUser(): Promise<LoginResponse['user']> {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.getCurrentUser();
    const raw = await apiClient.get(`${this.BASE_PATH}/me`);
    return adaptUserDto(raw);
  }

  /**
   * 修改密碼
   */
  static async changePassword(data: any): Promise<void> {
    return apiClient.post(`${this.BASE_PATH}/change-password`, data);
  }
}
