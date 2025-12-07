import { loginFailure, loginStart, loginSuccess } from '@store/authSlice';
import { useAppDispatch, useAppSelector } from '@store/hooks';
import { useCallback, useState } from 'react';
import { AuthApi } from '../api/AuthApi';
import type { LoginRequest } from '../api/AuthTypes';
import { UserViewModelFactory } from '../factory/UserViewModelFactory';

/**
 * 登入 Hook 回傳介面
 */
interface UseLoginResult {
  /** 執行登入 */
  login: (request: LoginRequest) => Promise<boolean>;
  /** 載入中狀態 */
  isLoading: boolean;
  /** 錯誤訊息 */
  error: string | null;
  /** 清除錯誤 */
  clearError: () => void;
}

/**
 * 登入邏輯 Hook
 * 封裝登入流程，處理 API 呼叫與狀態管理
 *
 * @example
 * ```tsx
 * const { login, isLoading, error } = useLogin();
 *
 * const handleSubmit = async (values: LoginRequest) => {
 *   const success = await login(values);
 *   if (success) {
 *     navigate('/dashboard');
 *   }
 * };
 * ```
 */
export function useLogin(): UseLoginResult {
  const dispatch = useAppDispatch();
  const { isLoading } = useAppSelector((state) => state.auth);
  const [error, setError] = useState<string | null>(null);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  const login = useCallback(
    async (request: LoginRequest): Promise<boolean> => {
      try {
        dispatch(loginStart());
        setError(null);

        // 呼叫登入 API
        const response = await AuthApi.login(request);

        // 使用 Factory 轉換使用者資料
        const userViewModel = UserViewModelFactory.createFromDTO(response.user);

        // 更新 Redux 狀態
        dispatch(
          loginSuccess({
            user: userViewModel,
            token: response.accessToken,
          })
        );

        // 儲存 Refresh Token
        localStorage.setItem('refreshToken', response.refreshToken);

        return true;
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : '登入失敗，請稍後再試';
        dispatch(loginFailure(errorMessage));
        setError(errorMessage);
        return false;
      }
    },
    [dispatch]
  );

  return {
    login,
    isLoading,
    error,
    clearError,
  };
}

export default useLogin;
