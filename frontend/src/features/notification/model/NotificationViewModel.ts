/**
 * Notification ViewModels (通知服務 前端視圖模型)
 * Domain Code: HR12
 */

import type {
  NotificationChannel,
  NotificationPriority,
  NotificationStatus,
  NotificationType,
} from '../api/NotificationTypes';

/**
 * 通知 ViewModel
 */
export interface NotificationViewModel {
  notificationId: string;
  recipientId: string;
  title: string;
  content: string;
  notificationType: NotificationType;
  notificationTypeLabel: string;
  notificationTypeIcon: string;
  channels: NotificationChannel[];
  channelsDisplay: string;
  priority: NotificationPriority;
  priorityLabel: string;
  priorityColor: string;
  status: NotificationStatus;
  statusLabel: string;
  sentAt?: string;
  sentAtDisplay?: string;
  readAt?: string;
  readAtDisplay?: string;
  relatedBusinessType?: string;
  relatedBusinessId?: string;
  createdAt: string;
  createdAtDisplay: string;
  timeAgo: string;
  isRead: boolean;
  isUnread: boolean;
  hasRelatedBusiness: boolean;
}

/**
 * 通知範本 ViewModel
 */
export interface NotificationTemplateViewModel {
  templateId: string;
  templateCode: string;
  templateName: string;
  subject?: string;
  body: string;
  defaultChannels: NotificationChannel[];
  defaultChannelsDisplay: string;
  isActive: boolean;
  statusLabel: string;
  statusColor: string;
  createdAt: string;
  createdAtDisplay: string;
}

/**
 * 通知偏好設定 ViewModel
 */
export interface NotificationPreferenceViewModel {
  preferenceId: string;
  employeeId: string;
  emailEnabled: boolean;
  pushEnabled: boolean;
  inAppEnabled: boolean;
  quietHoursStart?: string;
  quietHoursEnd?: string;
  quietHoursDisplay: string;
  hasQuietHours: boolean;
  updatedAt: string;
  updatedAtDisplay: string;
}

/**
 * 公告 ViewModel
 */
export interface AnnouncementViewModel {
  announcementId: string;
  title: string;
  content: string;
  priority: NotificationPriority;
  priorityLabel: string;
  priorityColor: string;
  targetRoles: string[];
  targetRolesDisplay: string;
  publishedAt?: string;
  publishedAtDisplay?: string;
  expiresAt?: string;
  expiresAtDisplay?: string;
  status: 'DRAFT' | 'PUBLISHED' | 'EXPIRED' | 'REVOKED';
  statusLabel: string;
  statusColor: string;
  createdBy: string;
  createdAt: string;
  createdAtDisplay: string;
}

/**
 * 通知摘要統計 ViewModel
 */
export interface NotificationSummaryViewModel {
  totalCount: number;
  unreadCount: number;
  todayCount: number;
  approvalRequestCount: number;
  reminderCount: number;
}
