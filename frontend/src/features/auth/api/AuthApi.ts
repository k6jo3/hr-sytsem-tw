import { apiClient } from '@shared/api';
import type {
    LoginRequest,
    LoginResponse,
    LogoutRequest,
    RefreshTokenRequest,
    RefreshTokenResponse,
    UserDto,
} from './AuthTypes';

/**
 * 認證 API 服務
 * 封裝所有與認證相關的 API 呼叫
 */
export const AuthApi = {
  /**
   * 使用者登入
   * @param request - 登入請求資料
   * @returns 登入回應 (含 Token 與使用者資訊)
   */
  login: async (request: LoginRequest): Promise<LoginResponse> => {
    return apiClient.post<LoginResponse>('/auth/login', request);
  },

  /**
   * 使用者登出
   * @param request - 登出請求 (含 Refresh Token)
   */
  logout: async (request: LogoutRequest): Promise<void> => {
    return apiClient.post<void>('/auth/logout', request);
  },

  /**
   * 取得當前使用者資訊
   * @returns 使用者 DTO
   */
  getCurrentUser: async (): Promise<UserDto> => {
    return apiClient.get<UserDto>('/auth/me');
  },

  /**
   * 刷新 Token
   * @param request - 刷新請求 (含 Refresh Token)
   * @returns 新的 Token
   */
  refreshToken: async (request: RefreshTokenRequest): Promise<RefreshTokenResponse> => {
    return apiClient.post<RefreshTokenResponse>('/auth/refresh', request);
  },
};

export default AuthApi;
