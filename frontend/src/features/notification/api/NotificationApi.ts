/**
 * Notification API (通知服務 API)
 * Domain Code: HR12
 */

import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { MockNotificationApi } from '../../../shared/api/SupportModuleMockApis';
import type {
    CreateNotificationTemplateRequest,
    CreateNotificationTemplateResponse,
    GetMyNotificationsRequest,
    GetMyNotificationsResponse,
    GetNotificationPreferenceResponse,
    GetNotificationTemplatesRequest,
    GetNotificationTemplatesResponse,
    GetUnreadCountResponse,
    MarkAllAsReadResponse,
    MarkAsReadResponse,
    NotificationDto,
    NotificationPreferenceDto,
    NotificationTemplateDto,
    SendNotificationRequest,
    SendNotificationResponse,
    UpdateNotificationPreferenceRequest,
    UpdateNotificationPreferenceResponse,
} from './NotificationTypes';

const BASE_URL = '/notifications';

// ========== Response Adapter ==========

/** 分頁參數轉換（後端 /me 使用 1-indexed page + pageSize） */
function adaptPageParams(params?: { page?: number; page_size?: number; [key: string]: any }) {
  if (!params) return params;
  const { page, page_size, ...rest } = params;
  return {
    ...rest,
    ...(page != null ? { page } : {}),
    ...(page_size != null ? { pageSize: page_size } : {}),
  };
}

/** Spring Page → 前端分頁格式 */
function adaptPage<T>(raw: any, adaptFn: (item: any) => T): { data: T[]; total: number; page: number; page_size: number } {
  const content = raw.content ?? raw.data ?? (Array.isArray(raw) ? raw : []);
  return {
    data: content.map(adaptFn),
    total: raw.totalElements ?? raw.total ?? content.length,
    page: (raw.pageable?.pageNumber ?? raw.number ?? 0) + 1,
    page_size: raw.pageable?.pageSize ?? raw.size ?? content.length,
  };
}

/** 後端 camelCase → 前端 NotificationDto */
function adaptNotification(raw: any): NotificationDto {
  return {
    notification_id: raw.notificationId ?? raw.notification_id ?? '',
    recipient_id: raw.recipientId ?? raw.recipient_id ?? '',
    title: raw.title ?? '',
    content: raw.content ?? '',
    notification_type: raw.notificationType ?? raw.notification_type ?? 'REMINDER',
    channels: raw.channels ?? raw.channel ? [raw.channel] : ['IN_APP'],
    priority: raw.priority ?? 'NORMAL',
    status: raw.status ?? 'SENT',
    sent_at: raw.sentAt ?? raw.sent_at ?? '',
    read_at: raw.readAt ?? raw.read_at ?? '',
    related_business_type: raw.relatedBusinessType ?? raw.related_business_type ?? '',
    related_business_id: raw.relatedBusinessId ?? raw.related_business_id ?? '',
    created_at: raw.createdAt ?? raw.created_at ?? '',
  };
}

/** 後端 camelCase → 前端 NotificationTemplateDto */
function adaptTemplate(raw: any): NotificationTemplateDto {
  return {
    template_id: raw.templateId ?? raw.template_id ?? '',
    template_code: raw.templateCode ?? raw.template_code ?? '',
    template_name: raw.name ?? raw.templateName ?? raw.template_name ?? '',
    subject: raw.subjectTemplate ?? raw.subject ?? '',
    body: raw.contentTemplate ?? raw.body ?? raw.content ?? '',
    default_channels: raw.defaultChannels ?? raw.default_channels ?? [],
    is_active: raw.status === 'ACTIVE' || (raw.isActive ?? raw.is_active ?? true),
    created_at: raw.createdAt ?? raw.created_at ?? '',
  };
}

/** 後端 camelCase → 前端 NotificationPreferenceDto */
function adaptPreference(raw: any): NotificationPreferenceDto {
  return {
    preference_id: raw.preferenceId ?? raw.preference_id ?? '',
    employee_id: raw.employeeId ?? raw.employee_id ?? '',
    email_enabled: raw.emailEnabled ?? raw.email_enabled ?? true,
    push_enabled: raw.pushEnabled ?? raw.push_enabled ?? false,
    in_app_enabled: raw.inAppEnabled ?? raw.in_app_enabled ?? true,
    quiet_hours_start: raw.quietHoursStart ?? raw.quiet_hours_start ?? '',
    quiet_hours_end: raw.quietHoursEnd ?? raw.quiet_hours_end ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

export const NotificationApi = {
  // ========== My Notifications ==========

  /**
   * 取得我的通知列表
   */
  getMyNotifications: async (
    params?: GetMyNotificationsRequest
  ): Promise<GetMyNotificationsResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) {
      const res = await MockNotificationApi.getNotifications();
      return { data: res.notifications, total: res.total, page: 1, page_size: 10 };
    }
    const raw = await apiClient.get<any>(`${BASE_URL}/me`, { params: adaptPageParams(params) });
    // 後端 /me 回傳自訂分頁格式 {items, pagination, summary}
    if (raw.items) {
      return {
        data: (raw.items ?? []).map(adaptNotification),
        total: raw.pagination?.totalItems ?? raw.items.length,
        page: raw.pagination?.currentPage ?? 1,
        page_size: raw.pagination?.pageSize ?? 10,
      };
    }
    // Spring Page 格式 fallback
    return adaptPage(raw, adaptNotification);
  },

  /**
   * 取得未讀通知數量
   */
  getUnreadCount: async (): Promise<GetUnreadCountResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) {
      const res = await MockNotificationApi.getNotifications();
      return { count: res.unread_count };
    }
    const raw = await apiClient.get<any>(`${BASE_URL}/unread-count`);
    return { count: raw.unreadCount ?? raw.count ?? 0 };
  },

  /**
   * 標記單一通知為已讀
   */
  markAsRead: async (notificationId: string): Promise<MarkAsReadResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) {
      await MockNotificationApi.markAsRead(notificationId);
      return { message: '已標記為已讀 (Mock)' };
    }
    const response = await apiClient.put<any>(`${BASE_URL}/${notificationId}/read`);
    return { message: response.message ?? '已標記為已讀' };
  },

  /**
   * 標記所有通知為已讀
   */
  markAllAsRead: async (): Promise<MarkAllAsReadResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) {
      await MockNotificationApi.markAllAsRead();
      return { message: '全部標記為已讀 (Mock)', count: 0 };
    }
    const response = await apiClient.put<any>(`${BASE_URL}/read-all`);
    return { message: response.message ?? '全部標記為已讀', count: response.count ?? 0 };
  },

  // ========== Send Notifications (Admin) ==========

  /**
   * 發送通知
   */
  sendNotification: async (
    request: SendNotificationRequest
  ): Promise<SendNotificationResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { notification_ids: ['mock-id'], message: '已發送 (Mock)' };
    const raw = await apiClient.post<any>(`${BASE_URL}/send`, request);
    return {
      notification_ids: raw.notificationIds ?? raw.notification_ids ?? [],
      message: raw.message ?? '已發送',
    };
  },

  // ========== Notification Templates (Admin) ==========

  /**
   * 取得通知範本列表
   */
  getTemplates: async (
    params?: GetNotificationTemplatesRequest
  ): Promise<GetNotificationTemplatesResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { data: [], total: 0, page: 1, page_size: 10 };
    const raw = await apiClient.get<any>(`${BASE_URL}/templates`, { params: adaptPageParams(params) });
    // 後端回傳 {items, pagination} 自訂格式
    if (raw.items) {
      return {
        data: (raw.items ?? []).map(adaptTemplate),
        total: raw.pagination?.totalItems ?? raw.items.length,
        page: raw.pagination?.currentPage ?? 1,
        page_size: raw.pagination?.pageSize ?? 10,
      };
    }
    return adaptPage(raw, adaptTemplate);
  },

  /**
   * 建立通知範本
   */
  createTemplate: async (
    request: CreateNotificationTemplateRequest
  ): Promise<CreateNotificationTemplateResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { template_id: 'mock-template-id', message: '範本已建立 (Mock)' };
    const raw = await apiClient.post<any>(`${BASE_URL}/templates`, request);
    return {
      template_id: raw.templateId ?? raw.template_id ?? '',
      message: raw.message ?? '範本已建立',
    };
  },

  // ========== Notification Preferences ==========

  /**
   * 取得通知偏好設定
   */
  getPreference: async (): Promise<GetNotificationPreferenceResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { preference: {} as any };
    const raw = await apiClient.get<any>(`${BASE_URL}/preferences`);
    return { preference: adaptPreference(raw) };
  },

  /**
   * 更新通知偏好設定
   */
  updatePreference: async (
    request: UpdateNotificationPreferenceRequest
  ): Promise<UpdateNotificationPreferenceResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { message: '設定已更新 (Mock)' };
    const raw = await apiClient.put<any>(`${BASE_URL}/preferences`, request);
    return { message: raw.message ?? '設定已更新' };
  },
};
