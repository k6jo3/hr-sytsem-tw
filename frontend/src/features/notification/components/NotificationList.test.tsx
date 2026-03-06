import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { NotificationList } from './NotificationList';
import type { NotificationViewModel, NotificationSummaryViewModel } from '../model/NotificationViewModel';

// Mock hooks
const mockRefresh = vi.fn();
const mockMarkAsRead = vi.fn();
const mockMarkAllAsRead = vi.fn();
const mockRefreshUnreadCount = vi.fn();

vi.mock('../hooks', () => ({
  useMyNotifications: vi.fn(),
}));

import { useMyNotifications } from '../hooks';

const mockNotifications: NotificationViewModel[] = [
  {
    notificationId: 'notif-001',
    title: '請假審核通知',
    content: '您的請假申請已核准',
    notificationType: 'APPROVAL_RESULT',
    notificationTypeLabel: '審核結果',
    priority: 'NORMAL',
    priorityLabel: '一般',
    priorityColor: 'blue',
    isRead: false,
    isUnread: true,
    readAt: undefined,
    timeAgo: '5 分鐘前',
    createdAt: '2026-03-05T09:00:00Z',
    createdAtDisplay: '2026-03-05 09:00',
  },
  {
    notificationId: 'notif-002',
    title: '系統維護通知',
    content: '系統將於今晚進行維護',
    notificationType: 'ANNOUNCEMENT',
    notificationTypeLabel: '公告',
    priority: 'HIGH',
    priorityLabel: '高',
    priorityColor: 'orange',
    isRead: true,
    isUnread: false,
    readAt: '2026-03-05T10:00:00Z',
    timeAgo: '1 小時前',
    createdAt: '2026-03-05T08:00:00Z',
    createdAtDisplay: '2026-03-05 08:00',
  },
];

const mockSummary: NotificationSummaryViewModel = {
  totalCount: 10,
  unreadCount: 3,
  todayCount: 5,
  approvalRequestCount: 2,
  reminderCount: 1,
};

describe('NotificationList', () => {
  describe('正常渲染', () => {
    it('應顯示通知列表與統計卡片', () => {
      vi.mocked(useMyNotifications).mockReturnValue({
        notifications: mockNotifications,
        summary: mockSummary,
        unreadCount: 3,
        loading: false,
        error: null,
        refresh: mockRefresh,
        markAsRead: mockMarkAsRead,
        markAllAsRead: mockMarkAllAsRead,
        refreshUnreadCount: mockRefreshUnreadCount,
      });

      render(<NotificationList />);

      // 統計卡片
      expect(screen.getByText('全部通知')).toBeInTheDocument();
      expect(screen.getByText('未讀通知')).toBeInTheDocument();
      expect(screen.getByText('審核請求')).toBeInTheDocument();
      expect(screen.getByText('今日通知')).toBeInTheDocument();

      // 通知內容
      expect(screen.getByText('請假審核通知')).toBeInTheDocument();
      expect(screen.getByText('系統維護通知')).toBeInTheDocument();
    });

    it('有未讀通知時應顯示全部標為已讀按鈕', () => {
      vi.mocked(useMyNotifications).mockReturnValue({
        notifications: mockNotifications,
        summary: mockSummary,
        unreadCount: 3,
        loading: false,
        error: null,
        refresh: mockRefresh,
        markAsRead: mockMarkAsRead,
        markAllAsRead: mockMarkAllAsRead,
        refreshUnreadCount: mockRefreshUnreadCount,
      });

      render(<NotificationList />);

      expect(screen.getByText('全部標為已讀')).toBeInTheDocument();
    });

    it('未讀通知應顯示標記已讀按鈕', () => {
      vi.mocked(useMyNotifications).mockReturnValue({
        notifications: mockNotifications,
        summary: mockSummary,
        unreadCount: 3,
        loading: false,
        error: null,
        refresh: mockRefresh,
        markAsRead: mockMarkAsRead,
        markAllAsRead: mockMarkAllAsRead,
        refreshUnreadCount: mockRefreshUnreadCount,
      });

      render(<NotificationList />);

      expect(screen.getByText('標記已讀')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(useMyNotifications).mockReturnValue({
        notifications: [],
        summary: null,
        unreadCount: 0,
        loading: true,
        error: null,
        refresh: mockRefresh,
        markAsRead: mockMarkAsRead,
        markAllAsRead: mockMarkAllAsRead,
        refreshUnreadCount: mockRefreshUnreadCount,
      });

      const { container } = render(<NotificationList />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });

  describe('空狀態', () => {
    it('無通知時應顯示空狀態', () => {
      vi.mocked(useMyNotifications).mockReturnValue({
        notifications: [],
        summary: mockSummary,
        unreadCount: 0,
        loading: false,
        error: null,
        refresh: mockRefresh,
        markAsRead: mockMarkAsRead,
        markAllAsRead: mockMarkAllAsRead,
        refreshUnreadCount: mockRefreshUnreadCount,
      });

      render(<NotificationList />);

      expect(screen.getByText('沒有通知')).toBeInTheDocument();
    });
  });

  describe('錯誤狀態', () => {
    it('錯誤時應顯示錯誤訊息', () => {
      vi.mocked(useMyNotifications).mockReturnValue({
        notifications: [],
        summary: null,
        unreadCount: 0,
        loading: false,
        error: '網路錯誤',
        refresh: mockRefresh,
        markAsRead: mockMarkAsRead,
        markAllAsRead: mockMarkAllAsRead,
        refreshUnreadCount: mockRefreshUnreadCount,
      });

      render(<NotificationList />);

      expect(screen.getByText('載入失敗')).toBeInTheDocument();
    });
  });
});
