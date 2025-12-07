import type { UserViewModel } from '@features/auth/model/UserProfile';
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

/**
 * 認證狀態介面
 */
export interface AuthState {
  /** 當前登入使用者 */
  user: UserViewModel | null;
  /** JWT Token */
  token: string | null;
  /** 是否已認證 */
  isAuthenticated: boolean;
  /** 載入中狀態 */
  isLoading: boolean;
  /** 錯誤訊息 */
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  token: localStorage.getItem('accessToken'),
  isAuthenticated: !!localStorage.getItem('accessToken'),
  isLoading: false,
  error: null,
};

/**
 * 認證狀態 Slice
 */
export const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    /** 開始登入請求 */
    loginStart: (state) => {
      state.isLoading = true;
      state.error = null;
    },
    /** 登入成功 */
    loginSuccess: (state, action: PayloadAction<{ user: UserViewModel; token: string }>) => {
      state.isLoading = false;
      state.isAuthenticated = true;
      state.user = action.payload.user;
      state.token = action.payload.token;
      localStorage.setItem('accessToken', action.payload.token);
    },
    /** 登入失敗 */
    loginFailure: (state, action: PayloadAction<string>) => {
      state.isLoading = false;
      state.error = action.payload;
    },
    /** 登出 */
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
      localStorage.removeItem('accessToken');
    },
    /** 設定使用者資訊 */
    setUser: (state, action: PayloadAction<UserViewModel>) => {
      state.user = action.payload;
    },
    /** 清除錯誤 */
    clearError: (state) => {
      state.error = null;
    },
  },
});

export const { loginStart, loginSuccess, loginFailure, logout, setUser, clearError } =
  authSlice.actions;

export default authSlice.reducer;
