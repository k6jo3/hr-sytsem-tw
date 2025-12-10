/**
 * Notification DTOs (通知服務 資料傳輸物件)
 * Domain Code: HR12
 */

// ========== Enums ==========

/**
 * 通知類型
 */
export type NotificationType =
  | 'APPROVAL_REQUEST' // 審核請求
  | 'APPROVAL_RESULT' // 審核結果
  | 'REMINDER' // 提醒
  | 'ANNOUNCEMENT' // 公告
  | 'ALERT'; // 警示

/**
 * 通知優先級
 */
export type NotificationPriority =
  | 'LOW' // 低
  | 'NORMAL' // 一般
  | 'HIGH' // 高
  | 'URGENT'; // 緊急

/**
 * 通知狀態
 */
export type NotificationStatus =
  | 'PENDING' // 待發送
  | 'SENT' // 已發送
  | 'FAILED' // 發送失敗
  | 'READ'; // 已讀

/**
 * 通知渠道
 */
export type NotificationChannel =
  | 'IN_APP' // 系統內
  | 'EMAIL' // 電郵
  | 'PUSH' // 推播
  | 'TEAMS' // Teams
  | 'LINE'; // LINE

// ========== DTOs ==========

/**
 * 通知 DTO
 */
export interface NotificationDto {
  notification_id: string;
  recipient_id: string;
  title: string;
  content: string;
  notification_type: NotificationType;
  channels: NotificationChannel[];
  priority: NotificationPriority;
  status: NotificationStatus;
  sent_at?: string;
  read_at?: string;
  related_business_type?: string;
  related_business_id?: string;
  created_at: string;
}

/**
 * 通知範本 DTO
 */
export interface NotificationTemplateDto {
  template_id: string;
  template_code: string;
  template_name: string;
  subject?: string;
  body: string;
  default_channels: NotificationChannel[];
  is_active: boolean;
  created_at: string;
}

/**
 * 通知偏好設定 DTO
 */
export interface NotificationPreferenceDto {
  preference_id: string;
  employee_id: string;
  email_enabled: boolean;
  push_enabled: boolean;
  in_app_enabled: boolean;
  quiet_hours_start?: string;
  quiet_hours_end?: string;
  updated_at: string;
}

// ========== Request/Response Types ==========

export interface GetMyNotificationsRequest {
  status?: NotificationStatus;
  notification_type?: NotificationType;
  page?: number;
  page_size?: number;
}

export interface GetMyNotificationsResponse {
  data: NotificationDto[];
  total: number;
  page: number;
  page_size: number;
}

export interface GetUnreadCountResponse {
  count: number;
}

export interface MarkAsReadResponse {
  message: string;
}

export interface MarkAllAsReadResponse {
  message: string;
  count: number;
}

export interface SendNotificationRequest {
  recipient_ids: string[];
  title: string;
  content: string;
  notification_type: NotificationType;
  channels?: NotificationChannel[];
  priority?: NotificationPriority;
  related_business_type?: string;
  related_business_id?: string;
}

export interface SendNotificationResponse {
  notification_ids: string[];
  message: string;
}

export interface GetNotificationTemplatesRequest {
  is_active?: boolean;
  page?: number;
  page_size?: number;
}

export interface GetNotificationTemplatesResponse {
  data: NotificationTemplateDto[];
  total: number;
  page: number;
  page_size: number;
}

export interface CreateNotificationTemplateRequest {
  template_code: string;
  template_name: string;
  subject?: string;
  body: string;
  default_channels?: NotificationChannel[];
}

export interface CreateNotificationTemplateResponse {
  template_id: string;
  message: string;
}

export interface GetNotificationPreferenceResponse {
  preference: NotificationPreferenceDto;
}

export interface UpdateNotificationPreferenceRequest {
  email_enabled?: boolean;
  push_enabled?: boolean;
  in_app_enabled?: boolean;
  quiet_hours_start?: string;
  quiet_hours_end?: string;
}

export interface UpdateNotificationPreferenceResponse {
  message: string;
}
