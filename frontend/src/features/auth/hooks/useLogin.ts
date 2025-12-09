import { useState } from 'react';
import { AuthApi } from '../api/AuthApi';
import type { LoginFormData } from '../api/AuthTypes';
import type { UserDto } from '../api/AuthTypes';

/**
 * 登入 Hook
 * 處理登入相關的業務邏輯
 */
export const useLogin = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const [user, setUser] = useState<UserDto | null>(null);

  /**
   * 登入
   */
  const login = async (data: LoginFormData) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await AuthApi.login({
        username: data.username,
        password: data.password,
        remember: data.remember,
      });

      // 儲存 access token
      localStorage.setItem('accessToken', response.access_token);

      // 如果勾選「記住我」，儲存 refresh token
      if (data.remember) {
        localStorage.setItem('refreshToken', response.refresh_token);
      }

      // 設定使用者資料
      setUser(response.user);
      
      return response.user;
    } catch (err) {
      const error = err as Error;
      setError(error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  /**
   * 登出
   */
  const logout = () => {
    // 清除 tokens
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    
    // 清除使用者資料
    setUser(null);
    setError(null);
  };

  return {
    loading,
    error,
    user,
    login,
    logout,
  };
};
