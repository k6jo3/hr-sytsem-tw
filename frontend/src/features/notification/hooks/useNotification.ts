/**
 * Notification Hooks (通知服務 Hooks)
 * Domain Code: HR12
 */

import { useCallback, useEffect, useState } from 'react';
import { NotificationApi } from '../api';
import type { UpdateNotificationPreferenceRequest } from '../api/NotificationTypes';
import { NotificationViewModelFactory } from '../factory/NotificationViewModelFactory';
import type {
  NotificationPreferenceViewModel,
  NotificationSummaryViewModel,
  NotificationViewModel,
} from '../model/NotificationViewModel';

/**
 * 我的通知 Hook
 */
export const useMyNotifications = () => {
  const [notifications, setNotifications] = useState<NotificationViewModel[]>([]);
  const [summary, setSummary] = useState<NotificationSummaryViewModel | null>(null);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchNotifications = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await NotificationApi.getMyNotifications({ page: 1, page_size: 100 });
      const viewModels = NotificationViewModelFactory.createNotificationList(response.data);
      const summaryData = NotificationViewModelFactory.createSummary(response.data);
      setNotifications(viewModels);
      setSummary(summaryData);
      setUnreadCount(summaryData.unreadCount);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '載入通知失敗';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  const markAsRead = useCallback(
    async (notificationId: string) => {
      try {
        await NotificationApi.markAsRead(notificationId);
        await fetchNotifications();
        return { success: true, message: '已標記為已讀' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '操作失敗';
        return { success: false, message: errorMessage };
      }
    },
    [fetchNotifications]
  );

  const markAllAsRead = useCallback(async () => {
    try {
      const result = await NotificationApi.markAllAsRead();
      await fetchNotifications();
      return { success: true, message: `已將 ${result.count} 則通知標記為已讀` };
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '操作失敗';
      return { success: false, message: errorMessage };
    }
  }, [fetchNotifications]);

  const refreshUnreadCount = useCallback(async () => {
    try {
      const response = await NotificationApi.getUnreadCount();
      setUnreadCount(response.count);
    } catch (err) {
      console.error('Failed to fetch unread count:', err);
    }
  }, []);

  useEffect(() => {
    fetchNotifications();
  }, [fetchNotifications]);

  return {
    notifications,
    summary,
    unreadCount,
    loading,
    error,
    refresh: fetchNotifications,
    markAsRead,
    markAllAsRead,
    refreshUnreadCount,
  };
};

/**
 * 通知偏好設定 Hook
 */
export const useNotificationPreference = () => {
  const [preference, setPreference] = useState<NotificationPreferenceViewModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const fetchPreference = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await NotificationApi.getPreference();
      const viewModel = NotificationViewModelFactory.createPreferenceViewModel(response.preference);
      setPreference(viewModel);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '載入通知設定失敗';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  const updatePreference = useCallback(
    async (request: UpdateNotificationPreferenceRequest) => {
      setSaving(true);
      try {
        await NotificationApi.updatePreference(request);
        await fetchPreference();
        return { success: true, message: '設定已儲存' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '儲存設定失敗';
        return { success: false, message: errorMessage };
      } finally {
        setSaving(false);
      }
    },
    [fetchPreference]
  );

  useEffect(() => {
    fetchPreference();
  }, [fetchPreference]);

  return {
    preference,
    loading,
    error,
    saving,
    refresh: fetchPreference,
    updatePreference,
  };
};

/**
 * 通用 Notification Hook (保持向後兼容)
 */
export const useNotification = () => {
  const [loading, _setLoading] = useState(false);
  const [error, _setError] = useState<string | null>(null);

  return {
    loading,
    error,
  };
};
