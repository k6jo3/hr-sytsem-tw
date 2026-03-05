/**
 * Notification ViewModel Factory (通知服務 視圖模型工廠)
 * Domain Code: HR12
 */

import type {
  AnnouncementDto,
  NotificationChannel,
  NotificationDto,
  NotificationPreferenceDto,
  NotificationPriority,
  NotificationStatus,
  NotificationTemplateDto,
  NotificationType,
} from '../api/NotificationTypes';
import type {
  AnnouncementViewModel,
  NotificationPreferenceViewModel,
  NotificationSummaryViewModel,
  NotificationTemplateViewModel,
  NotificationViewModel,
} from '../model/NotificationViewModel';

export class NotificationViewModelFactory {
  // ========== Notification ==========

  static createNotificationViewModel(dto: NotificationDto): NotificationViewModel {
    const isRead = dto.status === 'READ';

    return {
      notificationId: dto.notification_id,
      recipientId: dto.recipient_id,
      title: dto.title,
      content: dto.content,
      notificationType: dto.notification_type,
      notificationTypeLabel: this.mapNotificationTypeLabel(dto.notification_type),
      notificationTypeIcon: this.mapNotificationTypeIcon(dto.notification_type),
      channels: dto.channels,
      channelsDisplay: this.mapChannelsDisplay(dto.channels),
      priority: dto.priority,
      priorityLabel: this.mapPriorityLabel(dto.priority),
      priorityColor: this.mapPriorityColor(dto.priority),
      status: dto.status,
      statusLabel: this.mapStatusLabel(dto.status),
      sentAt: dto.sent_at,
      sentAtDisplay: dto.sent_at ? this.formatDateTime(dto.sent_at) : undefined,
      readAt: dto.read_at,
      readAtDisplay: dto.read_at ? this.formatDateTime(dto.read_at) : undefined,
      relatedBusinessType: dto.related_business_type,
      relatedBusinessId: dto.related_business_id,
      createdAt: dto.created_at,
      createdAtDisplay: this.formatDateTime(dto.created_at),
      timeAgo: this.calculateTimeAgo(dto.created_at),
      isRead,
      isUnread: !isRead,
      hasRelatedBusiness: !!(dto.related_business_type && dto.related_business_id),
    };
  }

  static createNotificationList(dtos: NotificationDto[]): NotificationViewModel[] {
    return dtos.map((dto) => this.createNotificationViewModel(dto));
  }

  private static mapNotificationTypeLabel(type: NotificationType): string {
    const labelMap: Record<NotificationType, string> = {
      APPROVAL_REQUEST: '審核請求',
      APPROVAL_RESULT: '審核結果',
      REMINDER: '提醒',
      ANNOUNCEMENT: '公告',
      ALERT: '警示',
    };
    return labelMap[type];
  }

  private static mapNotificationTypeIcon(type: NotificationType): string {
    const iconMap: Record<NotificationType, string> = {
      APPROVAL_REQUEST: 'audit',
      APPROVAL_RESULT: 'check-circle',
      REMINDER: 'clock-circle',
      ANNOUNCEMENT: 'notification',
      ALERT: 'warning',
    };
    return iconMap[type];
  }

  private static mapChannelsDisplay(channels: NotificationChannel[]): string {
    const channelLabels: Record<NotificationChannel, string> = {
      IN_APP: '系統',
      EMAIL: '郵件',
      PUSH: '推播',
      TEAMS: 'Teams',
      LINE: 'LINE',
    };
    return channels.map((ch) => channelLabels[ch]).join(', ');
  }

  private static mapPriorityLabel(priority: NotificationPriority): string {
    const labelMap: Record<NotificationPriority, string> = {
      LOW: '低',
      NORMAL: '一般',
      HIGH: '高',
      URGENT: '緊急',
    };
    return labelMap[priority];
  }

  private static mapPriorityColor(priority: NotificationPriority): string {
    const colorMap: Record<NotificationPriority, string> = {
      LOW: 'default',
      NORMAL: 'blue',
      HIGH: 'orange',
      URGENT: 'red',
    };
    return colorMap[priority];
  }

  private static mapStatusLabel(status: NotificationStatus): string {
    const labelMap: Record<NotificationStatus, string> = {
      PENDING: '待發送',
      SENT: '已發送',
      FAILED: '發送失敗',
      READ: '已讀',
    };
    return labelMap[status];
  }

  private static calculateTimeAgo(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMinutes = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMinutes / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMinutes < 1) return '剛剛';
    if (diffMinutes < 60) return `${diffMinutes} 分鐘前`;
    if (diffHours < 24) return `${diffHours} 小時前`;
    if (diffDays < 7) return `${diffDays} 天前`;
    return this.formatDate(dateString);
  }

  // ========== Notification Template ==========

  static createTemplateViewModel(dto: NotificationTemplateDto): NotificationTemplateViewModel {
    return {
      templateId: dto.template_id,
      templateCode: dto.template_code,
      templateName: dto.template_name,
      subject: dto.subject,
      body: dto.body,
      defaultChannels: dto.default_channels,
      defaultChannelsDisplay: this.mapChannelsDisplay(dto.default_channels),
      isActive: dto.is_active,
      statusLabel: dto.is_active ? '啟用中' : '已停用',
      statusColor: dto.is_active ? 'success' : 'default',
      createdAt: dto.created_at,
      createdAtDisplay: this.formatDate(dto.created_at),
    };
  }

  static createTemplateList(dtos: NotificationTemplateDto[]): NotificationTemplateViewModel[] {
    return dtos.map((dto) => this.createTemplateViewModel(dto));
  }

  // ========== Notification Preference ==========

  static createPreferenceViewModel(
    dto: NotificationPreferenceDto
  ): NotificationPreferenceViewModel {
    const hasQuietHours = !!(dto.quiet_hours_start && dto.quiet_hours_end);
    const quietHoursDisplay = hasQuietHours
      ? `${dto.quiet_hours_start} - ${dto.quiet_hours_end}`
      : '未設定';

    return {
      preferenceId: dto.preference_id,
      employeeId: dto.employee_id,
      emailEnabled: dto.email_enabled,
      pushEnabled: dto.push_enabled,
      inAppEnabled: dto.in_app_enabled,
      quietHoursStart: dto.quiet_hours_start,
      quietHoursEnd: dto.quiet_hours_end,
      quietHoursDisplay,
      hasQuietHours,
      updatedAt: dto.updated_at,
      updatedAtDisplay: this.formatDateTime(dto.updated_at),
    };
  }

  // ========== Notification Summary ==========

  static createSummary(notifications: NotificationDto[]): NotificationSummaryViewModel {
    const today = new Date().toISOString().split('T')[0];

    return {
      totalCount: notifications.length,
      unreadCount: notifications.filter((n) => n.status !== 'READ').length,
      todayCount: notifications.filter((n) => n.created_at.startsWith(today)).length,
      approvalRequestCount: notifications.filter((n) => n.notification_type === 'APPROVAL_REQUEST')
        .length,
      reminderCount: notifications.filter((n) => n.notification_type === 'REMINDER').length,
    };
  }

  // ========== Announcement ==========

  static createAnnouncementViewModel(dto: AnnouncementDto): AnnouncementViewModel {
    const roleLabels: Record<string, string> = {
      ADMIN: '管理員',
      HR: '人資',
      FINANCE: '財務',
      PM: '專案經理',
      MANAGER: '主管',
      EMPLOYEE: '員工',
    };
    const statusLabels: Record<string, string> = {
      DRAFT: '草稿',
      PUBLISHED: '已發布',
      EXPIRED: '已過期',
      REVOKED: '已撤銷',
    };
    const statusColors: Record<string, string> = {
      DRAFT: 'default',
      PUBLISHED: 'success',
      EXPIRED: 'warning',
      REVOKED: 'error',
    };

    return {
      announcementId: dto.announcement_id,
      title: dto.title,
      content: dto.content,
      priority: dto.priority,
      priorityLabel: this.mapPriorityLabel(dto.priority),
      priorityColor: this.mapPriorityColor(dto.priority),
      targetRoles: dto.target_roles ?? [],
      targetRolesDisplay: (dto.target_roles ?? []).map((r) => roleLabels[r] ?? r).join(', ') || '全體',
      publishedAt: dto.published_at,
      publishedAtDisplay: dto.published_at ? this.formatDateTime(dto.published_at) : undefined,
      expiresAt: dto.expires_at,
      expiresAtDisplay: dto.expires_at ? this.formatDateTime(dto.expires_at) : undefined,
      status: dto.status,
      statusLabel: statusLabels[dto.status] ?? dto.status,
      statusColor: statusColors[dto.status] ?? 'default',
      createdBy: dto.created_by,
      createdAt: dto.created_at,
      createdAtDisplay: this.formatDateTime(dto.created_at),
    };
  }

  static createAnnouncementList(dtos: AnnouncementDto[]): AnnouncementViewModel[] {
    return dtos.map((dto) => this.createAnnouncementViewModel(dto));
  }

  // ========== Utility Methods ==========

  private static formatDate(isoString: string): string {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private static formatDateTime(isoString: string): string {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  }
}
