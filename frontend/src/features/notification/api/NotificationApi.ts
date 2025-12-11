/**
 * Notification API (通知服務 API)
 * Domain Code: HR12
 */

import { apiClient } from '@shared/api';
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
    const response = await apiClient.get<GetMyNotificationsResponse>(`${BASE_URL}/me`, { params });
    return response.data;
  },

  /**
   * 取得未讀通知數量
   */
  getUnreadCount: async (): Promise<GetUnreadCountResponse> => {
    const response = await apiClient.get<GetUnreadCountResponse>(`${BASE_URL}/unread-count`);
    return response.data;
  },

  /**
   * 標記單一通知為已讀
   */
  markAsRead: async (notificationId: string): Promise<MarkAsReadResponse> => {
    const response = await apiClient.put<MarkAsReadResponse>(
      `${BASE_URL}/${notificationId}/read`
    );
    return response.data;
  },

  /**
   * 標記所有通知為已讀
   */
  markAllAsRead: async (): Promise<MarkAllAsReadResponse> => {
    const response = await apiClient.put<MarkAllAsReadResponse>(`${BASE_URL}/read-all`);
    return response.data;
  },

  // ========== Send Notifications (Admin) ==========

  /**
   * 發送通知
   */
  sendNotification: async (
    request: SendNotificationRequest
  ): Promise<SendNotificationResponse> => {
    const response = await apiClient.post<SendNotificationResponse>(`${BASE_URL}/send`, request);
    return response.data;
  },

  // ========== Notification Templates (Admin) ==========

  /**
   * 取得通知範本列表
   */
  getTemplates: async (
    params?: GetNotificationTemplatesRequest
  ): Promise<GetNotificationTemplatesResponse> => {
    const response = await apiClient.get<GetNotificationTemplatesResponse>(
      `${BASE_URL}/templates`,
      { params }
    );
    return response.data;
  },

  /**
   * 建立通知範本
   */
  createTemplate: async (
    request: CreateNotificationTemplateRequest
  ): Promise<CreateNotificationTemplateResponse> => {
    const response = await apiClient.post<CreateNotificationTemplateResponse>(
      `${BASE_URL}/templates`,
      request
    );
    return response.data;
  },

  // ========== Notification Preferences ==========

  /**
   * 取得通知偏好設定
   */
  getPreference: async (): Promise<GetNotificationPreferenceResponse> => {
    const response = await apiClient.get<GetNotificationPreferenceResponse>(
      `${BASE_URL}/preferences`
    );
    return response.data;
  },

  /**
   * 更新通知偏好設定
   */
  updatePreference: async (
    request: UpdateNotificationPreferenceRequest
  ): Promise<UpdateNotificationPreferenceResponse> => {
    const response = await apiClient.put<UpdateNotificationPreferenceResponse>(
      `${BASE_URL}/preferences`,
      request
    );
    return response.data;
  },
};
