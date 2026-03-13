import { describe, expect, it, vi, beforeEach } from 'vitest';
import { renderHook, act, waitFor } from '@testing-library/react';
import React from 'react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@store/authSlice';
import { useLogin } from './useLogin';
import { AuthApi } from '../api/AuthApi';

// Mock AuthApi
vi.mock('../api/AuthApi', () => ({
  AuthApi: {
    login: vi.fn(),
  },
}));

// Mock UserViewModelFactory
vi.mock('../factory/UserViewModelFactory', () => ({
  UserViewModelFactory: {
    createProfileFromDTO: vi.fn((dto: any) => ({
      id: dto.id,
      username: dto.username,
      fullName: `${dto.first_name} ${dto.last_name}`,
      email: dto.email,
      roles: dto.role_list || [],
      isAdmin: (dto.role_list || []).includes('ADMIN'),
      employeeId: dto.id,
    })),
    createUserViewModelFromDTO: vi.fn((dto: any) => ({
      userId: dto.id,
      username: dto.username,
      displayName: `${dto.first_name} ${dto.last_name}`,
      fullName: `${dto.first_name} ${dto.last_name}`,
      email: dto.email,
      roles: dto.role_list || [],
      roleIds: dto.role_ids || [],
      isAdmin: (dto.role_list || []).includes('ADMIN'),
      employeeId: dto.id,
      status: dto.status || 'ACTIVE',
      mustChangePassword: dto.must_change_password || false,
      createdAt: dto.created_at || '',
      updatedAt: dto.updated_at || '',
    })),
  },
}));

/**
 * 建立帶 Redux store 的 wrapper
 */
const createWrapper = () => {
  const store = configureStore({
    reducer: { auth: authReducer },
  });
  return ({ children }: { children: React.ReactNode }) =>
    React.createElement(Provider, { store, children });
};

describe('useLogin', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      const { result } = renderHook(() => useLogin(), { wrapper: createWrapper() });

      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBeNull();
      expect(result.current.user).toBeNull();
    });
  });

  describe('登入成功', () => {
    it('應該正確處理成功的登入', async () => {
      const mockResponse = {
        access_token: 'mock-token',
        refresh_token: 'mock-refresh-token',
        expires_in: 3600,
        user: {
          id: 'user-123',
          username: 'testuser',
          email: 'test@example.com',
          first_name: 'Test',
          last_name: 'User',
          role_list: ['EMPLOYEE'],
          role_ids: [],
          display_name: 'Test User',
          must_change_password: false,
          status: 'ACTIVE' as const,
          created_at: '2024-01-01T00:00:00Z',
          updated_at: '2024-01-01T00:00:00Z',
        },
      };

      vi.mocked(AuthApi.login).mockResolvedValue(mockResponse);

      const { result } = renderHook(() => useLogin(), { wrapper: createWrapper() });

      await act(async () => {
        await result.current.login({
          username: 'testuser',
          password: 'password123',
          remember: false,
        });
      });

      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBeNull();
      expect(result.current.user).not.toBeNull();
      expect(localStorage.getItem('accessToken')).toBe('mock-token');
    });

    it('勾選記住我時應該儲存refresh token', async () => {
      const mockResponse = {
        access_token: 'mock-token',
        refresh_token: 'mock-refresh-token',
        expires_in: 3600,
        user: {
          id: 'user-123',
          username: 'testuser',
          email: 'test@example.com',
          first_name: 'Test',
          last_name: 'User',
          role_list: ['EMPLOYEE'],
          role_ids: [],
          display_name: 'Test User',
          must_change_password: false,
          status: 'ACTIVE' as const,
          created_at: '2024-01-01T00:00:00Z',
          updated_at: '2024-01-01T00:00:00Z',
        },
      };

      vi.mocked(AuthApi.login).mockResolvedValue(mockResponse);

      const { result } = renderHook(() => useLogin(), { wrapper: createWrapper() });

      await act(async () => {
        await result.current.login({
          username: 'testuser',
          password: 'password123',
          remember: true,
        });
      });

      expect(localStorage.getItem('refreshToken')).toBe('mock-refresh-token');
    });
  });

  describe('登入失敗', () => {
    it('應該正確處理登入錯誤', async () => {
      const errorMessage = '帳號或密碼錯誤';
      vi.mocked(AuthApi.login).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useLogin(), { wrapper: createWrapper() });

      await act(async () => {
        try {
          await result.current.login({
            username: 'wronguser',
            password: 'wrongpass',
            remember: false,
          });
        } catch (error) {
          // Expected error
        }
      });

      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBeTruthy();
      expect(result.current.user).toBeNull();
      expect(localStorage.getItem('accessToken')).toBeNull();
    });
  });

  describe('載入狀態', () => {
    it('登入過程中loading應該為true', async () => {
      const mockResponse = {
        access_token: 'mock-token',
        refresh_token: 'mock-refresh-token',
        expires_in: 3600,
        user: {
          id: 'user-123',
          username: 'testuser',
          email: 'test@example.com',
          first_name: 'Test',
          last_name: 'User',
          role_list: ['EMPLOYEE'],
          role_ids: [],
          display_name: 'Test User',
          must_change_password: false,
          status: 'ACTIVE' as const,
          created_at: '2024-01-01T00:00:00Z',
          updated_at: '2024-01-01T00:00:00Z',
        },
      };

      vi.mocked(AuthApi.login).mockImplementation(() =>
        new Promise((resolve) => setTimeout(() => resolve(mockResponse), 100))
      );

      const { result } = renderHook(() => useLogin(), { wrapper: createWrapper() });

      act(() => {
        result.current.login({
          username: 'testuser',
          password: 'password123',
          remember: false,
        });
      });

      expect(result.current.loading).toBe(true);

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });
    });
  });

  describe('登出', () => {
    it('應該清除使用者資訊和tokens', async () => {
      localStorage.setItem('accessToken', 'mock-token');
      localStorage.setItem('refreshToken', 'mock-refresh-token');

      const { result } = renderHook(() => useLogin(), { wrapper: createWrapper() });

      act(() => {
        result.current.logout();
      });

      expect(result.current.user).toBeNull();
      expect(localStorage.getItem('accessToken')).toBeNull();
      expect(localStorage.getItem('refreshToken')).toBeNull();
    });
  });
});
