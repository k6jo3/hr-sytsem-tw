import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useMyNotifications, useNotificationPreference } from './useNotification';
import { NotificationApi } from '../api';

vi.mock('../api', () => ({
  NotificationApi: {
    getMyNotifications: vi.fn(),
    getUnreadCount: vi.fn(),
    markAsRead: vi.fn(),
    markAllAsRead: vi.fn(),
    getPreference: vi.fn(),
    updatePreference: vi.fn(),
    getTemplates: vi.fn(),
    createTemplate: vi.fn(),
    updateTemplate: vi.fn(),
    deleteTemplate: vi.fn(),
    getAnnouncements: vi.fn(),
    createAnnouncement: vi.fn(),
    updateAnnouncement: vi.fn(),
    deleteAnnouncement: vi.fn(),
    sendNotification: vi.fn(),
  },
}));

const mockNotificationsResponse = {
  data: [
    {
      notification_id: 'n-001',
      recipient_id: 'emp-001',
      title: '請假核准通知',
      content: '您的請假已核准',
      notification_type: 'APPROVAL_RESULT' as const,
      channels: ['IN_APP' as const],
      priority: 'HIGH' as const,
      status: 'SENT' as const,
      created_at: '2026-03-05T10:00:00Z',
    },
    {
      notification_id: 'n-002',
      recipient_id: 'emp-001',
      title: '薪資單已發放',
      content: '2月薪資單已可查閱',
      notification_type: 'REMINDER' as const,
      channels: ['IN_APP' as const, 'EMAIL' as const],
      priority: 'NORMAL' as const,
      status: 'READ' as const,
      created_at: '2026-03-01T09:00:00Z',
    },
  ],
  total: 2,
  page: 1,
  page_size: 100,
};

describe('useMyNotifications', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應載入通知列表並轉換為 ViewModel', async () => {
    vi.mocked(NotificationApi.getMyNotifications).mockResolvedValue(mockNotificationsResponse);

    const { result } = renderHook(() => useMyNotifications());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.notifications).toHaveLength(2);
    expect(result.current.notifications[0].notificationId).toBe('n-001');
    expect(result.current.notifications[0].isUnread).toBe(true);
    expect(result.current.notifications[1].isRead).toBe(true);
    expect(result.current.summary?.totalCount).toBe(2);
    expect(result.current.summary?.unreadCount).toBe(1);
    expect(result.current.error).toBeNull();
  });

  it('應處理載入錯誤', async () => {
    vi.mocked(NotificationApi.getMyNotifications).mockRejectedValue(new Error('網路錯誤'));

    const { result } = renderHook(() => useMyNotifications());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.error).toBe('網路錯誤');
    expect(result.current.notifications).toHaveLength(0);
  });

  it('應成功標記通知為已讀', async () => {
    vi.mocked(NotificationApi.getMyNotifications).mockResolvedValue(mockNotificationsResponse);
    vi.mocked(NotificationApi.markAsRead).mockResolvedValue({ message: '已標記' });

    const { result } = renderHook(() => useMyNotifications());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let markResult: { success: boolean; message: string };
    await act(async () => {
      markResult = await result.current.markAsRead('n-001');
    });

    expect(markResult!.success).toBe(true);
    expect(NotificationApi.markAsRead).toHaveBeenCalledWith('n-001');
  });

  it('應成功標記全部為已讀', async () => {
    vi.mocked(NotificationApi.getMyNotifications).mockResolvedValue(mockNotificationsResponse);
    vi.mocked(NotificationApi.markAllAsRead).mockResolvedValue({ message: '全部已讀', count: 1 });

    const { result } = renderHook(() => useMyNotifications());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let markResult: { success: boolean; message: string };
    await act(async () => {
      markResult = await result.current.markAllAsRead();
    });

    expect(markResult!.success).toBe(true);
    expect(NotificationApi.markAllAsRead).toHaveBeenCalled();
  });
});

describe('useNotificationPreference', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應載入偏好設定', async () => {
    vi.mocked(NotificationApi.getPreference).mockResolvedValue({
      preference: {
        preference_id: 'pref-001',
        employee_id: 'emp-001',
        email_enabled: true,
        push_enabled: false,
        in_app_enabled: true,
        quiet_hours_start: '22:00',
        quiet_hours_end: '08:00',
        updated_at: '2026-03-01T00:00:00Z',
      },
    });

    const { result } = renderHook(() => useNotificationPreference());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.preference).not.toBeNull();
    expect(result.current.preference?.emailEnabled).toBe(true);
    expect(result.current.preference?.pushEnabled).toBe(false);
    expect(result.current.preference?.hasQuietHours).toBe(true);
    expect(result.current.preference?.quietHoursDisplay).toBe('22:00 - 08:00');
  });

  it('應成功更新偏好設定', async () => {
    vi.mocked(NotificationApi.getPreference).mockResolvedValue({
      preference: {
        preference_id: 'pref-001',
        employee_id: 'emp-001',
        email_enabled: true,
        push_enabled: false,
        in_app_enabled: true,
        updated_at: '2026-03-01T00:00:00Z',
      },
    });
    vi.mocked(NotificationApi.updatePreference).mockResolvedValue({ message: '已更新' });

    const { result } = renderHook(() => useNotificationPreference());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let updateResult: { success: boolean; message: string };
    await act(async () => {
      updateResult = await result.current.updatePreference({ push_enabled: true });
    });

    expect(updateResult!.success).toBe(true);
    expect(NotificationApi.updatePreference).toHaveBeenCalledWith({ push_enabled: true });
  });
});
