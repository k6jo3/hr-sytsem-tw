import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type { LoginRequest, LoginResponse } from './AuthTypes';
import { MockAuthApi } from './MockAuthApi';

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
    return apiClient.post(`${this.BASE_PATH}/login`, request);
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
    return apiClient.post(`${this.BASE_PATH}/refresh`, { refreshToken });
  }

  /**
   * 取得當前使用者資訊
   */
  static async getCurrentUser(): Promise<LoginResponse['user']> {
    if (MockConfig.isEnabled('AUTH')) return MockAuthApi.getCurrentUser();
    return apiClient.get(`${this.BASE_PATH}/me`);
  }

  /**
   * 修改密碼
   */
  static async changePassword(data: any): Promise<void> {
    return apiClient.post(`${this.BASE_PATH}/change-password`, data);
  }
}
