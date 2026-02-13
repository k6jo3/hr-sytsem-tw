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
    SendNotificationRequest,
    SendNotificationResponse,
    UpdateNotificationPreferenceRequest,
    UpdateNotificationPreferenceResponse,
} from './NotificationTypes';

const BASE_URL = '/notifications';

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
    const response = await apiClient.get<GetMyNotificationsResponse>(`${BASE_URL}/me`, { params });
    return response as any;
  },

  /**
   * 取得未讀通知數量
   */
  getUnreadCount: async (): Promise<GetUnreadCountResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) {
      const res = await MockNotificationApi.getNotifications();
      return { count: res.unread_count };
    }
    const response = await apiClient.get<GetUnreadCountResponse>(`${BASE_URL}/unread-count`);
    return response as any;
  },

  /**
   * 標記單一通知為已讀
   */
  markAsRead: async (notificationId: string): Promise<MarkAsReadResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) {
      await MockNotificationApi.markAsRead(notificationId);
      return { message: '已標記為已讀 (Mock)' };
    }
    const response = await apiClient.put<MarkAsReadResponse>(
      `${BASE_URL}/${notificationId}/read`
    );
    return response as any;
  },

  /**
   * 標記所有通知為已讀
   */
  markAllAsRead: async (): Promise<MarkAllAsReadResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) {
      await MockNotificationApi.markAllAsRead();
      return { message: '全部標記為已讀 (Mock)', count: 0 };
    }
    const response = await apiClient.put<MarkAllAsReadResponse>(`${BASE_URL}/read-all`);
    return response as any;
  },

  // ========== Send Notifications (Admin) ==========

  /**
   * 發送通知
   */
  sendNotification: async (
    request: SendNotificationRequest
  ): Promise<SendNotificationResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { notification_ids: ['mock-id'], message: '已發送 (Mock)' };
    const response = await apiClient.post<SendNotificationResponse>(`${BASE_URL}/send`, request);
    return response as any;
  },

  // ========== Notification Templates (Admin) ==========

  /**
   * 取得通知範本列表
   */
  getTemplates: async (
    params?: GetNotificationTemplatesRequest
  ): Promise<GetNotificationTemplatesResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { data: [], total: 0, page: 1, page_size: 10 };
    const response = await apiClient.get<GetNotificationTemplatesResponse>(
      `${BASE_URL}/templates`,
      { params }
    );
    return response as any;
  },

  /**
   * 建立通知範本
   */
  createTemplate: async (
    request: CreateNotificationTemplateRequest
  ): Promise<CreateNotificationTemplateResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { template_id: 'mock-template-id', message: '範本已建立 (Mock)' };
    const response = await apiClient.post<CreateNotificationTemplateResponse>(
      `${BASE_URL}/templates`,
      request
    );
    return response as any;
  },

  // ========== Notification Preferences ==========

  /**
   * 取得通知偏好設定
   */
  getPreference: async (): Promise<GetNotificationPreferenceResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { preference: {} as any };
    const response = await apiClient.get<GetNotificationPreferenceResponse>(
      `${BASE_URL}/preferences`
    );
    return response as any;
  },

  /**
   * 更新通知偏好設定
   */
  updatePreference: async (
    request: UpdateNotificationPreferenceRequest
  ): Promise<UpdateNotificationPreferenceResponse> => {
    if (MockConfig.isEnabled('NOTIFICATION')) return { message: '設定已更新 (Mock)' };
    const response = await apiClient.put<UpdateNotificationPreferenceResponse>(
      `${BASE_URL}/preferences`,
      request
    );
    return response as any;
  },
};
