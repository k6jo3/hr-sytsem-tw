import { describe, it, expect } from 'vitest';
import { NotificationViewModelFactory } from './NotificationViewModelFactory';
import type {
  AnnouncementDto,
  NotificationDto,
  NotificationPreferenceDto,
  NotificationTemplateDto,
} from '../api/NotificationTypes';

describe('NotificationViewModelFactory', () => {
  describe('createNotificationViewModel', () => {
    it('應正確轉換未讀通知', () => {
      const dto: NotificationDto = {
        notification_id: 'n-001',
        recipient_id: 'emp-001',
        title: '請假申請已核准',
        content: '您的 3/5 特休申請已由主管核准',
        notification_type: 'APPROVAL_RESULT',
        channels: ['IN_APP', 'EMAIL'],
        priority: 'HIGH',
        status: 'SENT',
        sent_at: '2026-03-05T10:00:00Z',
        created_at: '2026-03-05T10:00:00Z',
      };

      const vm = NotificationViewModelFactory.createNotificationViewModel(dto);

      expect(vm.notificationId).toBe('n-001');
      expect(vm.title).toBe('請假申請已核准');
      expect(vm.notificationTypeLabel).toBe('審核結果');
      expect(vm.channelsDisplay).toBe('系統, 郵件');
      expect(vm.priorityLabel).toBe('高');
      expect(vm.priorityColor).toBe('orange');
      expect(vm.isUnread).toBe(true);
      expect(vm.isRead).toBe(false);
      expect(vm.hasRelatedBusiness).toBe(false);
    });

    it('應正確轉換已讀通知', () => {
      const dto: NotificationDto = {
        notification_id: 'n-002',
        recipient_id: 'emp-001',
        title: '薪資單已發放',
        content: '2月薪資單已可查閱',
        notification_type: 'REMINDER',
        channels: ['IN_APP'],
        priority: 'NORMAL',
        status: 'READ',
        read_at: '2026-03-01T12:00:00Z',
        created_at: '2026-03-01T09:00:00Z',
      };

      const vm = NotificationViewModelFactory.createNotificationViewModel(dto);

      expect(vm.isRead).toBe(true);
      expect(vm.isUnread).toBe(false);
      expect(vm.statusLabel).toBe('已讀');
      expect(vm.priorityColor).toBe('blue');
    });

    it('應正確處理有業務關聯的通知', () => {
      const dto: NotificationDto = {
        notification_id: 'n-003',
        recipient_id: 'emp-001',
        title: '審核請求',
        content: '有一筆請假申請需要審核',
        notification_type: 'APPROVAL_REQUEST',
        channels: ['IN_APP', 'PUSH'],
        priority: 'URGENT',
        status: 'SENT',
        related_business_type: 'LEAVE',
        related_business_id: 'leave-001',
        created_at: '2026-03-05T08:00:00Z',
      };

      const vm = NotificationViewModelFactory.createNotificationViewModel(dto);

      expect(vm.hasRelatedBusiness).toBe(true);
      expect(vm.relatedBusinessType).toBe('LEAVE');
      expect(vm.priorityLabel).toBe('緊急');
      expect(vm.priorityColor).toBe('red');
      expect(vm.notificationTypeLabel).toBe('審核請求');
    });
  });

  describe('createNotificationList', () => {
    it('應正確批次轉換通知列表', () => {
      const dtos: NotificationDto[] = [
        {
          notification_id: 'n-001',
          recipient_id: 'emp-001',
          title: '通知1',
          content: '內容1',
          notification_type: 'REMINDER',
          channels: ['IN_APP'],
          priority: 'NORMAL',
          status: 'SENT',
          created_at: '2026-03-05T10:00:00Z',
        },
        {
          notification_id: 'n-002',
          recipient_id: 'emp-001',
          title: '通知2',
          content: '內容2',
          notification_type: 'ALERT',
          channels: ['EMAIL'],
          priority: 'LOW',
          status: 'READ',
          created_at: '2026-03-04T10:00:00Z',
        },
      ];

      const vms = NotificationViewModelFactory.createNotificationList(dtos);

      expect(vms).toHaveLength(2);
      expect(vms[0].notificationId).toBe('n-001');
      expect(vms[1].notificationId).toBe('n-002');
    });
  });

  describe('createTemplateViewModel', () => {
    it('應正確轉換啟用中的範本', () => {
      const dto: NotificationTemplateDto = {
        template_id: 't-001',
        template_code: 'LEAVE_APPROVED',
        template_name: '請假核准通知',
        subject: '您的請假申請已核准',
        body: '親愛的 {{employee_name}}，您的 {{date}} 請假已核准。',
        default_channels: ['IN_APP', 'EMAIL'],
        is_active: true,
        created_at: '2026-01-15T00:00:00Z',
      };

      const vm = NotificationViewModelFactory.createTemplateViewModel(dto);

      expect(vm.templateId).toBe('t-001');
      expect(vm.templateCode).toBe('LEAVE_APPROVED');
      expect(vm.templateName).toBe('請假核准通知');
      expect(vm.defaultChannelsDisplay).toBe('系統, 郵件');
      expect(vm.isActive).toBe(true);
      expect(vm.statusLabel).toBe('啟用中');
      expect(vm.statusColor).toBe('success');
    });

    it('應正確轉換停用的範本', () => {
      const dto: NotificationTemplateDto = {
        template_id: 't-002',
        template_code: 'OLD_TEMPLATE',
        template_name: '舊範本',
        body: '已停用',
        default_channels: [],
        is_active: false,
        created_at: '2025-06-01T00:00:00Z',
      };

      const vm = NotificationViewModelFactory.createTemplateViewModel(dto);

      expect(vm.isActive).toBe(false);
      expect(vm.statusLabel).toBe('已停用');
      expect(vm.statusColor).toBe('default');
    });
  });

  describe('createPreferenceViewModel', () => {
    it('應正確轉換有靜音時段的偏好設定', () => {
      const dto: NotificationPreferenceDto = {
        preference_id: 'pref-001',
        employee_id: 'emp-001',
        email_enabled: true,
        push_enabled: false,
        in_app_enabled: true,
        quiet_hours_start: '22:00',
        quiet_hours_end: '08:00',
        updated_at: '2026-03-01T00:00:00Z',
      };

      const vm = NotificationViewModelFactory.createPreferenceViewModel(dto);

      expect(vm.emailEnabled).toBe(true);
      expect(vm.pushEnabled).toBe(false);
      expect(vm.inAppEnabled).toBe(true);
      expect(vm.hasQuietHours).toBe(true);
      expect(vm.quietHoursDisplay).toBe('22:00 - 08:00');
    });

    it('應正確轉換無靜音時段的偏好設定', () => {
      const dto: NotificationPreferenceDto = {
        preference_id: 'pref-002',
        employee_id: 'emp-002',
        email_enabled: false,
        push_enabled: true,
        in_app_enabled: true,
        updated_at: '2026-03-01T00:00:00Z',
      };

      const vm = NotificationViewModelFactory.createPreferenceViewModel(dto);

      expect(vm.hasQuietHours).toBe(false);
      expect(vm.quietHoursDisplay).toBe('未設定');
    });
  });

  describe('createAnnouncementViewModel', () => {
    it('應正確轉換已發布的公告', () => {
      const dto: AnnouncementDto = {
        announcement_id: 'ann-001',
        title: '系統維護通知',
        content: '系統將於週六進行維護',
        priority: 'HIGH',
        target_roles: ['ADMIN', 'HR'],
        published_at: '2026-03-05T09:00:00Z',
        status: 'PUBLISHED',
        created_by: 'admin',
        created_at: '2026-03-05T08:00:00Z',
      };

      const vm = NotificationViewModelFactory.createAnnouncementViewModel(dto);

      expect(vm.announcementId).toBe('ann-001');
      expect(vm.title).toBe('系統維護通知');
      expect(vm.priorityLabel).toBe('高');
      expect(vm.priorityColor).toBe('orange');
      expect(vm.targetRolesDisplay).toBe('管理員, 人資');
      expect(vm.statusLabel).toBe('已發布');
      expect(vm.statusColor).toBe('success');
    });

    it('應正確轉換全體對象的公告', () => {
      const dto: AnnouncementDto = {
        announcement_id: 'ann-002',
        title: '新年快樂',
        content: '祝大家新年快樂',
        priority: 'NORMAL',
        target_roles: [],
        status: 'PUBLISHED',
        created_by: 'hr_admin',
        created_at: '2026-01-01T00:00:00Z',
      };

      const vm = NotificationViewModelFactory.createAnnouncementViewModel(dto);

      expect(vm.targetRolesDisplay).toBe('全體');
    });

    it('應正確轉換已撤銷的公告', () => {
      const dto: AnnouncementDto = {
        announcement_id: 'ann-003',
        title: '舊公告',
        content: '已撤銷',
        priority: 'LOW',
        status: 'REVOKED',
        created_by: 'admin',
        created_at: '2026-02-01T00:00:00Z',
      };

      const vm = NotificationViewModelFactory.createAnnouncementViewModel(dto);

      expect(vm.statusLabel).toBe('已撤銷');
      expect(vm.statusColor).toBe('error');
    });
  });

  describe('createSummary', () => {
    it('應正確計算通知摘要', () => {
      const today = new Date().toISOString().split('T')[0];
      const dtos: NotificationDto[] = [
        {
          notification_id: 'n-1',
          recipient_id: 'emp-1',
          title: '審核',
          content: '',
          notification_type: 'APPROVAL_REQUEST',
          channels: ['IN_APP'],
          priority: 'NORMAL',
          status: 'SENT',
          created_at: `${today}T10:00:00Z`,
        },
        {
          notification_id: 'n-2',
          recipient_id: 'emp-1',
          title: '提醒',
          content: '',
          notification_type: 'REMINDER',
          channels: ['EMAIL'],
          priority: 'LOW',
          status: 'READ',
          created_at: '2026-01-01T00:00:00Z',
        },
        {
          notification_id: 'n-3',
          recipient_id: 'emp-1',
          title: '提醒2',
          content: '',
          notification_type: 'REMINDER',
          channels: ['IN_APP'],
          priority: 'NORMAL',
          status: 'SENT',
          created_at: `${today}T08:00:00Z`,
        },
      ];

      const summary = NotificationViewModelFactory.createSummary(dtos);

      expect(summary.totalCount).toBe(3);
      expect(summary.unreadCount).toBe(2);
      expect(summary.todayCount).toBe(2);
      expect(summary.approvalRequestCount).toBe(1);
      expect(summary.reminderCount).toBe(2);
    });
  });
});
