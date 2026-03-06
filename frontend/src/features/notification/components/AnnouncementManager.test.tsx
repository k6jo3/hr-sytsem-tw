import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { AnnouncementManager } from './AnnouncementManager';
import type { AnnouncementViewModel } from '../model/NotificationViewModel';

// Mock hooks
const mockRefresh = vi.fn();
const mockCreateAnnouncement = vi.fn();
const mockUpdateAnnouncement = vi.fn();
const mockDeleteAnnouncement = vi.fn();

vi.mock('../hooks/useAnnouncements', () => ({
  useAnnouncements: vi.fn(),
}));

import { useAnnouncements } from '../hooks/useAnnouncements';

const mockAnnouncements: AnnouncementViewModel[] = [
  {
    announcementId: 'ann-001',
    title: '系統維護通知',
    content: '系統將於 3/10 進行維護',
    priority: 'HIGH',
    priorityLabel: '高',
    priorityColor: 'orange',
    targetRoles: ['ALL'],
    targetRolesDisplay: '全體',
    status: 'PUBLISHED',
    statusLabel: '已發布',
    statusColor: 'success',
    publishedAt: '2026-03-05T09:00:00Z',
    publishedAtDisplay: '2026-03-05 09:00',
    createdAt: '2026-03-04T15:00:00Z',
    createdAtDisplay: '2026-03-04 15:00',
  },
  {
    announcementId: 'ann-002',
    title: '年度考核通知',
    content: '請於月底前完成年度考核',
    priority: 'NORMAL',
    priorityLabel: '一般',
    priorityColor: 'blue',
    targetRoles: ['MANAGER', 'HR'],
    targetRolesDisplay: '主管, 人資',
    status: 'REVOKED',
    statusLabel: '已撤銷',
    statusColor: 'error',
    createdAt: '2026-02-20T10:00:00Z',
    createdAtDisplay: '2026-02-20 10:00',
  },
];

describe('AnnouncementManager', () => {
  describe('正常渲染', () => {
    it('應顯示公告列表', () => {
      vi.mocked(useAnnouncements).mockReturnValue({
        announcements: mockAnnouncements,
        loading: false,
        error: null,
        saving: false,
        refresh: mockRefresh,
        createAnnouncement: mockCreateAnnouncement,
        updateAnnouncement: mockUpdateAnnouncement,
        deleteAnnouncement: mockDeleteAnnouncement,
      });

      render(<AnnouncementManager />);

      expect(screen.getByText('系統維護通知')).toBeInTheDocument();
      expect(screen.getByText('已發布')).toBeInTheDocument();
      expect(screen.getByText('全體')).toBeInTheDocument();

      expect(screen.getByText('年度考核通知')).toBeInTheDocument();
      expect(screen.getByText('已撤銷')).toBeInTheDocument();
    });

    it('應顯示發布公告與重新整理按鈕', () => {
      vi.mocked(useAnnouncements).mockReturnValue({
        announcements: mockAnnouncements,
        loading: false,
        error: null,
        saving: false,
        refresh: mockRefresh,
        createAnnouncement: mockCreateAnnouncement,
        updateAnnouncement: mockUpdateAnnouncement,
        deleteAnnouncement: mockDeleteAnnouncement,
      });

      render(<AnnouncementManager />);

      expect(screen.getByText('發布公告')).toBeInTheDocument();
      expect(screen.getByText('重新整理')).toBeInTheDocument();
    });

    it('已撤銷的公告編輯按鈕應停用', () => {
      vi.mocked(useAnnouncements).mockReturnValue({
        announcements: mockAnnouncements,
        loading: false,
        error: null,
        saving: false,
        refresh: mockRefresh,
        createAnnouncement: mockCreateAnnouncement,
        updateAnnouncement: mockUpdateAnnouncement,
        deleteAnnouncement: mockDeleteAnnouncement,
      });

      render(<AnnouncementManager />);

      const editButtons = screen.getAllByText('編輯');
      // 第二個是已撤銷的，應該被 disabled
      expect(editButtons[1].closest('button')).toBeDisabled();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(useAnnouncements).mockReturnValue({
        announcements: [],
        loading: true,
        error: null,
        saving: false,
        refresh: mockRefresh,
        createAnnouncement: mockCreateAnnouncement,
        updateAnnouncement: mockUpdateAnnouncement,
        deleteAnnouncement: mockDeleteAnnouncement,
      });

      const { container } = render(<AnnouncementManager />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });

  describe('錯誤狀態', () => {
    it('錯誤時應顯示錯誤訊息', () => {
      vi.mocked(useAnnouncements).mockReturnValue({
        announcements: [],
        loading: false,
        error: '伺服器錯誤',
        saving: false,
        refresh: mockRefresh,
        createAnnouncement: mockCreateAnnouncement,
        updateAnnouncement: mockUpdateAnnouncement,
        deleteAnnouncement: mockDeleteAnnouncement,
      });

      render(<AnnouncementManager />);

      expect(screen.getByText('載入失敗')).toBeInTheDocument();
    });
  });
});
