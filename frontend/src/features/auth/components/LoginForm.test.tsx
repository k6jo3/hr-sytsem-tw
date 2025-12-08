import { describe, expect, it, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { LoginForm } from './LoginForm';

describe('LoginForm', () => {
  describe('表單渲染', () => {
    it('應該顯示所有必要的表單欄位', () => {
      // Given
      const handleSubmit = vi.fn();
      
      // When
      render(<LoginForm onSubmit={handleSubmit} />);
      
      // Then
      expect(screen.getByLabelText(/帳號/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/密碼/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /登入/i })).toBeInTheDocument();
    });

    it('應該顯示記住我選項', () => {
      // Given
      const handleSubmit = vi.fn();
      
      // When
      render(<LoginForm onSubmit={handleSubmit} />);
      
      // Then
      expect(screen.getByLabelText(/記住我/i)).toBeInTheDocument();
    });
  });

  describe('表單驗證', () => {
    it('空白帳號時應該顯示錯誤訊息', async () => {
      // Given
      const handleSubmit = vi.fn();
      render(<LoginForm onSubmit={handleSubmit} />);
      
      // When
      const submitButton = screen.getByRole('button', { name: /登入/i });
      fireEvent.click(submitButton);
      
      // Then
      await waitFor(() => {
        expect(screen.getByText(/請輸入帳號/i)).toBeInTheDocument();
      });
      expect(handleSubmit).not.toHaveBeenCalled();
    });

    it('空白密碼時應該顯示錯誤訊息', async () => {
      // Given
      const handleSubmit = vi.fn();
      render(<LoginForm onSubmit={handleSubmit} />);
      
      // When
      const usernameInput = screen.getByLabelText(/帳號/i);
      fireEvent.change(usernameInput, { target: { value: 'testuser' } });
      
      const submitButton = screen.getByRole('button', { name: /登入/i });
      fireEvent.click(submitButton);
      
      // Then
      await waitFor(() => {
        expect(screen.getByText(/請輸入密碼/i)).toBeInTheDocument();
      });
      expect(handleSubmit).not.toHaveBeenCalled();
    });

    it('密碼長度不足時應該顯示錯誤訊息', async () => {
      // Given
      const handleSubmit = vi.fn();
      render(<LoginForm onSubmit={handleSubmit} />);
      
      // When
      const usernameInput = screen.getByLabelText(/帳號/i);
      const passwordInput = screen.getByLabelText(/密碼/i);
      
      fireEvent.change(usernameInput, { target: { value: 'testuser' } });
      fireEvent.change(passwordInput, { target: { value: '123' } });
      
      const submitButton = screen.getByRole('button', { name: /登入/i });
      fireEvent.click(submitButton);
      
      // Then
      await waitFor(() => {
        expect(screen.getByText(/密碼長度至少6個字元/i)).toBeInTheDocument();
      });
      expect(handleSubmit).not.toHaveBeenCalled();
    });
  });

  describe('表單提交', () => {
    it('有效資料應該呼叫onSubmit', async () => {
      // Given
      const handleSubmit = vi.fn();
      render(<LoginForm onSubmit={handleSubmit} />);
      
      // When
      const usernameInput = screen.getByLabelText(/帳號/i);
      const passwordInput = screen.getByLabelText(/密碼/i);
      
      fireEvent.change(usernameInput, { target: { value: 'testuser' } });
      fireEvent.change(passwordInput, { target: { value: 'password123' } });
      
      const submitButton = screen.getByRole('button', { name: /登入/i });
      fireEvent.click(submitButton);
      
      // Then
      await waitFor(() => {
        expect(handleSubmit).toHaveBeenCalledWith({
          username: 'testuser',
          password: 'password123',
          remember: false,
        });
      });
    });

    it('勾選記住我時應該傳遞正確的值', async () => {
      // Given
      const handleSubmit = vi.fn();
      render(<LoginForm onSubmit={handleSubmit} />);
      
      // When
      const usernameInput = screen.getByLabelText(/帳號/i);
      const passwordInput = screen.getByLabelText(/密碼/i);
      const rememberCheckbox = screen.getByLabelText(/記住我/i);
      
      fireEvent.change(usernameInput, { target: { value: 'testuser' } });
      fireEvent.change(passwordInput, { target: { value: 'password123' } });
      fireEvent.click(rememberCheckbox);
      
      const submitButton = screen.getByRole('button', { name: /登入/i });
      fireEvent.click(submitButton);
      
      // Then
      await waitFor(() => {
        expect(handleSubmit).toHaveBeenCalledWith({
          username: 'testuser',
          password: 'password123',
          remember: true,
        });
      });
    });
  });

  describe('載入狀態', () => {
    it('載入中時應該禁用提交按鈕', () => {
      // Given
      const handleSubmit = vi.fn();
      
      // When
      render(<LoginForm onSubmit={handleSubmit} loading={true} />);
      
      // Then
      const submitButton = screen.getByRole('button', { name: /登入中/i });
      expect(submitButton).toBeDisabled();
    });

    it('載入中時應該顯示載入圖示', () => {
      // Given
      const handleSubmit = vi.fn();
      
      // When
      render(<LoginForm onSubmit={handleSubmit} loading={true} />);
      
      // Then
      expect(screen.getByRole('button', { name: /登入中/i })).toBeInTheDocument();
    });
  });

  describe('錯誤處理', () => {
    it('有錯誤訊息時應該顯示錯誤', () => {
      // Given
      const handleSubmit = vi.fn();
      const errorMessage = '帳號或密碼錯誤';
      
      // When
      render(<LoginForm onSubmit={handleSubmit} error={errorMessage} />);
      
      // Then
      expect(screen.getByText(errorMessage)).toBeInTheDocument();
    });
  });
});
