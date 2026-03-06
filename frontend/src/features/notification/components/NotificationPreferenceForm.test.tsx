import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { NotificationPreferenceForm } from './NotificationPreferenceForm';

// Mock hooks
const mockUpdatePreference = vi.fn();

vi.mock('../hooks', () => ({
  useNotificationPreference: vi.fn(),
}));

import { useNotificationPreference } from '../hooks';

const mockPreference = {
  emailEnabled: true,
  pushEnabled: false,
  inAppEnabled: true,
  teamsEnabled: false,
  lineEnabled: false,
  hasQuietHours: true,
  quietHoursStart: '22:00',
  quietHoursEnd: '08:00',
  quietHoursDisplay: '22:00 ~ 08:00',
  updatedAtDisplay: '2026-03-05 10:00',
};

describe('NotificationPreferenceForm', () => {
  describe('正常渲染', () => {
    it('應顯示渠道設定項', () => {
      vi.mocked(useNotificationPreference).mockReturnValue({
        preference: mockPreference as any,
        loading: false,
        error: null,
        saving: false,
        updatePreference: mockUpdatePreference,
      });

      render(<NotificationPreferenceForm />);

      expect(screen.getByText('系統內通知')).toBeInTheDocument();
      expect(screen.getByText('電子郵件')).toBeInTheDocument();
      expect(screen.getByText('推播通知')).toBeInTheDocument();
      expect(screen.getByText('Teams / LINE')).toBeInTheDocument();
    });

    it('應顯示靜音時段設定', () => {
      vi.mocked(useNotificationPreference).mockReturnValue({
        preference: mockPreference as any,
        loading: false,
        error: null,
        saving: false,
        updatePreference: mockUpdatePreference,
      });

      render(<NotificationPreferenceForm />);

      expect(screen.getByText('靜音時段')).toBeInTheDocument();
      expect(screen.getByText('啟用靜音時段')).toBeInTheDocument();
    });

    it('應顯示儲存按鈕', () => {
      vi.mocked(useNotificationPreference).mockReturnValue({
        preference: mockPreference as any,
        loading: false,
        error: null,
        saving: false,
        updatePreference: mockUpdatePreference,
      });

      render(<NotificationPreferenceForm />);

      expect(screen.getByText('儲存設定')).toBeInTheDocument();
    });

    it('有靜音時段時應顯示目前設定', () => {
      vi.mocked(useNotificationPreference).mockReturnValue({
        preference: mockPreference as any,
        loading: false,
        error: null,
        saving: false,
        updatePreference: mockUpdatePreference,
      });

      render(<NotificationPreferenceForm />);

      expect(screen.getByText('目前靜音時段：22:00 ~ 08:00')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(useNotificationPreference).mockReturnValue({
        preference: null,
        loading: true,
        error: null,
        saving: false,
        updatePreference: mockUpdatePreference,
      });

      const { container } = render(<NotificationPreferenceForm />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });

  describe('錯誤狀態', () => {
    it('錯誤時應顯示錯誤訊息', () => {
      vi.mocked(useNotificationPreference).mockReturnValue({
        preference: null,
        loading: false,
        error: '載入偏好設定失敗',
        saving: false,
        updatePreference: mockUpdatePreference,
      });

      render(<NotificationPreferenceForm />);

      expect(screen.getByText('載入失敗')).toBeInTheDocument();
    });
  });
});
