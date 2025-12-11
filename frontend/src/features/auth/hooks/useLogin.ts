import { useAppDispatch, useAppSelector } from '@store/hooks';
import { loginStart, loginSuccess, loginFailure, logout as logoutAction } from '@store/authSlice';
import { AuthApi } from '../api/AuthApi';
import { UserViewModelFactory } from '../factory/UserViewModelFactory';
import type { LoginFormData } from '../api/AuthTypes';

/**
 * 登入 Hook
 * 處理登入相關的業務邏輯，整合 Redux 狀態管理
 */
export const useLogin = () => {
  const dispatch = useAppDispatch();
  const { isLoading, error, user } = useAppSelector((state) => state.auth);

  /**
   * 登入
   */
  const login = async (data: LoginFormData) => {
    dispatch(loginStart());

    try {
      const response = await AuthApi.login({
        username: data.username,
        password: data.password,
        remember: data.remember,
      });

      // 如果勾選「記住我」，儲存 refresh token
      if (data.remember) {
        localStorage.setItem('refreshToken', response.refresh_token);
      }

      // 使用 Factory 轉換 DTO 為 ViewModel
      const userViewModel = UserViewModelFactory.createProfileFromDTO(response.user);

      // 更新 Redux state（會自動儲存 access token 到 localStorage）
      dispatch(loginSuccess({
        user: userViewModel,
        token: response.access_token,
      }));

      return userViewModel;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '登入失敗';
      dispatch(loginFailure(errorMessage));
      throw err;
    }
  };

  /**
   * 登出
   */
  const logout = () => {
    // 清除 refresh token
    localStorage.removeItem('refreshToken');

    // 更新 Redux state（會自動清除 access token）
    dispatch(logoutAction());
  };

  return {
    loading: isLoading,
    error: error ? new Error(error) : null,
    user,
    login,
    logout,
  };
};
