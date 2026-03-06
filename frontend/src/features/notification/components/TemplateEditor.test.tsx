import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { TemplateEditor } from './TemplateEditor';
import type { NotificationTemplateViewModel } from '../model/NotificationViewModel';

// Mock hooks
const mockRefresh = vi.fn();
const mockCreateTemplate = vi.fn();
const mockUpdateTemplate = vi.fn();
const mockDeleteTemplate = vi.fn();

vi.mock('../hooks/useNotificationTemplates', () => ({
  useNotificationTemplates: vi.fn(),
}));

import { useNotificationTemplates } from '../hooks/useNotificationTemplates';

const mockTemplates: NotificationTemplateViewModel[] = [
  {
    templateId: 'tmpl-001',
    templateCode: 'LEAVE_APPROVED',
    templateName: '請假核准通知',
    subject: '您的請假申請已核准',
    body: '{{employee_name}} 您好，您的請假已核准',
    defaultChannels: ['IN_APP', 'EMAIL'],
    defaultChannelsDisplay: '系統內, 電郵',
    isActive: true,
    statusLabel: '啟用中',
    statusColor: 'success',
    createdAt: '2026-01-15T10:00:00Z',
    createdAtDisplay: '2026-01-15',
  },
  {
    templateId: 'tmpl-002',
    templateCode: 'OT_REMINDER',
    templateName: '加班提醒',
    subject: '加班時數提醒',
    body: '提醒您本月加班已達 {{hours}} 小時',
    defaultChannels: ['PUSH'],
    defaultChannelsDisplay: '推播',
    isActive: false,
    statusLabel: '已停用',
    statusColor: 'default',
    createdAt: '2026-02-01T08:00:00Z',
    createdAtDisplay: '2026-02-01',
  },
];

describe('TemplateEditor', () => {
  describe('正常渲染', () => {
    it('應顯示範本列表', () => {
      vi.mocked(useNotificationTemplates).mockReturnValue({
        templates: mockTemplates,
        loading: false,
        error: null,
        saving: false,
        refresh: mockRefresh,
        createTemplate: mockCreateTemplate,
        updateTemplate: mockUpdateTemplate,
        deleteTemplate: mockDeleteTemplate,
      });

      render(<TemplateEditor />);

      expect(screen.getByText('LEAVE_APPROVED')).toBeInTheDocument();
      expect(screen.getByText('請假核准通知')).toBeInTheDocument();
      expect(screen.getByText('啟用中')).toBeInTheDocument();

      expect(screen.getByText('OT_REMINDER')).toBeInTheDocument();
      expect(screen.getByText('加班提醒')).toBeInTheDocument();
      expect(screen.getByText('已停用')).toBeInTheDocument();
    });

    it('應顯示新增範本與重新整理按鈕', () => {
      vi.mocked(useNotificationTemplates).mockReturnValue({
        templates: mockTemplates,
        loading: false,
        error: null,
        saving: false,
        refresh: mockRefresh,
        createTemplate: mockCreateTemplate,
        updateTemplate: mockUpdateTemplate,
        deleteTemplate: mockDeleteTemplate,
      });

      render(<TemplateEditor />);

      expect(screen.getByText('新增範本')).toBeInTheDocument();
      expect(screen.getByText('重新整理')).toBeInTheDocument();
    });

    it('每列應顯示編輯與刪除按鈕', () => {
      vi.mocked(useNotificationTemplates).mockReturnValue({
        templates: mockTemplates,
        loading: false,
        error: null,
        saving: false,
        refresh: mockRefresh,
        createTemplate: mockCreateTemplate,
        updateTemplate: mockUpdateTemplate,
        deleteTemplate: mockDeleteTemplate,
      });

      render(<TemplateEditor />);

      const editButtons = screen.getAllByText('編輯');
      expect(editButtons.length).toBe(2);
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      vi.mocked(useNotificationTemplates).mockReturnValue({
        templates: [],
        loading: true,
        error: null,
        saving: false,
        refresh: mockRefresh,
        createTemplate: mockCreateTemplate,
        updateTemplate: mockUpdateTemplate,
        deleteTemplate: mockDeleteTemplate,
      });

      const { container } = render(<TemplateEditor />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });

  describe('錯誤狀態', () => {
    it('錯誤時應顯示錯誤訊息', () => {
      vi.mocked(useNotificationTemplates).mockReturnValue({
        templates: [],
        loading: false,
        error: '伺服器錯誤',
        saving: false,
        refresh: mockRefresh,
        createTemplate: mockCreateTemplate,
        updateTemplate: mockUpdateTemplate,
        deleteTemplate: mockDeleteTemplate,
      });

      render(<TemplateEditor />);

      expect(screen.getByText('載入失敗')).toBeInTheDocument();
    });
  });
});
