import { apiClient } from '@shared/api';
import type { LoginRequest, LoginResponse } from './AuthTypes';

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
    return apiClient.post(`${this.BASE_PATH}/login`, request);
  }

  /**
   * 登出
   */
  static async logout(): Promise<void> {
    return apiClient.post(`${this.BASE_PATH}/logout`, {});
  }

  /**
   * 刷新 Token
   */
  static async refreshToken(refreshToken: string): Promise<LoginResponse> {
    return apiClient.post(`${this.BASE_PATH}/refresh`, { refreshToken });
  }

  /**
   * 取得當前使用者資訊
   */
  static async getCurrentUser(): Promise<LoginResponse['user']> {
    return apiClient.get(`${this.BASE_PATH}/me`);
  }
}
