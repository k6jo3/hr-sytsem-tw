import { describe, expect, it, vi, beforeEach } from 'vitest';
import { renderHook, act, waitFor } from '@testing-library/react';
import { useLogin } from './useLogin';
import { AuthApi } from '../api';

// Mock AuthApi
vi.mock('../api', () => ({
  AuthApi: {
    login: vi.fn(),
  },
}));

describe('useLogin', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      // When
      const { result } = renderHook(() => useLogin());
      
      // Then
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBeNull();
      expect(result.current.user).toBeNull();
    });
  });

  describe('登入成功', () => {
    it('應該正確處理成功的登入', async () => {
      // Given
      const mockResponse = {
        access_token: 'mock-token',
        refresh_token: 'mock-refresh-token',
        user: {
          id: 'user-123',
          username: 'testuser',
          email: 'test@example.com',
          first_name: 'Test',
          last_name: 'User',
          role_list: ['EMPLOYEE'],
          status: 'ACTIVE' as const,
          created_at: '2024-01-01T00:00:00Z',
        },
      };

      vi.mocked(AuthApi.login).mockResolvedValue(mockResponse);

      const { result } = renderHook(() => useLogin());
      
      // When
      await act(async () => {
        await result.current.login({
          username: 'testuser',
          password: 'password123',
          remember: false,
        });
      });
      
      // Then
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBeNull();
      expect(result.current.user).toEqual(mockResponse.user);
      expect(localStorage.getItem('accessToken')).toBe('mock-token');
    });

    it('勾選記住我時應該儲存refresh token', async () => {
      // Given
      const mockResponse = {
        access_token: 'mock-token',
        refresh_token: 'mock-refresh-token',
        user: {
          id: 'user-123',
          username: 'testuser',
          email: 'test@example.com',
          first_name: 'Test',
          last_name: 'User',
          role_list: ['EMPLOYEE'],
          status: 'ACTIVE' as const,
          created_at: '2024-01-01T00:00:00Z',
        },
      };

      vi.mocked(AuthApi.login).mockResolvedValue(mockResponse);

      const { result } = renderHook(() => useLogin());
      
      // When
      await act(async () => {
        await result.current.login({
          username: 'testuser',
          password: 'password123',
          remember: true,
        });
      });
      
      // Then
      expect(localStorage.getItem('refreshToken')).toBe('mock-refresh-token');
    });
  });

  describe('登入失敗', () => {
    it('應該正確處理登入錯誤', async () => {
      // Given
      const errorMessage = '帳號或密碼錯誤';
      vi.mocked(AuthApi.login).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => useLogin());
      
      // When
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
      
      // Then
      expect(result.current.loading).toBe(false);
      expect(result.current.error).toBeTruthy();
      expect(result.current.user).toBeNull();
      expect(localStorage.getItem('accessToken')).toBeNull();
    });
  });

  describe('載入狀態', () => {
    it('登入過程中loading應該為true', async () => {
      // Given
      const mockResponse = {
        access_token: 'mock-token',
        refresh_token: 'mock-refresh-token',
        user: {
          id: 'user-123',
          username: 'testuser',
          email: 'test@example.com',
          first_name: 'Test',
          last_name: 'User',
          role_list: ['EMPLOYEE'],
          status: 'ACTIVE' as const,
          created_at: '2024-01-01T00:00:00Z',
        },
      };

      vi.mocked(AuthApi.login).mockImplementation(() => 
        new Promise((resolve) => setTimeout(() => resolve(mockResponse), 100))
      );

      const { result } = renderHook(() => useLogin());
      
      // When
      act(() => {
        result.current.login({
          username: 'testuser',
          password: 'password123',
          remember: false,
        });
      });
      
      // Then
      expect(result.current.loading).toBe(true);
      
      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });
    });
  });

  describe('登出', () => {
    it('應該清除使用者資訊和tokens', async () => {
      // Given
      localStorage.setItem('accessToken', 'mock-token');
      localStorage.setItem('refreshToken', 'mock-refresh-token');
      
      const { result } = renderHook(() => useLogin());
      
      // When
      act(() => {
        result.current.logout();
      });
      
      // Then
      expect(result.current.user).toBeNull();
      expect(localStorage.getItem('accessToken')).toBeNull();
      expect(localStorage.getItem('refreshToken')).toBeNull();
    });
  });
});
