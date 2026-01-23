package com.company.hrms.notification.domain.model.valueobject;

/**
 * 通知渠道列舉
 * <p>
 * 定義所有支援的通知發送渠道
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public enum NotificationChannel {

    /**
     * 系統內通知 (WebSocket - STOMP)
     */
    IN_APP("系統內通知", "WebSocket (STOMP)"),

    /**
     * 電子郵件 (Spring Mail + SMTP)
     */
    EMAIL("電子郵件", "Spring Mail + SMTP"),

    /**
     * 行動推播 (Firebase FCM)
     */
    PUSH("行動推播", "Firebase FCM"),

    /**
     * Microsoft Teams (Webhook)
     */
    TEAMS("Microsoft Teams", "Webhook"),

    /**
     * LINE 通知 (LINE Notify API)
     */
    LINE("LINE 通知", "LINE Notify API");

    private final String displayName;
    private final String technicalImpl;

    NotificationChannel(String displayName, String technicalImpl) {
        this.displayName = displayName;
        this.technicalImpl = technicalImpl;
    }

    /**
     * 取得渠道顯示名稱
     *
     * @return 顯示名稱
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 取得技術實作方式
     *
     * @return 技術實作說明
     */
    public String getTechnicalImpl() {
        return technicalImpl;
    }

    /**
     * 是否為非同步渠道
     * <p>
     * IN_APP 為同步渠道，其他為非同步
     * </p>
     *
     * @return true 為非同步渠道
     */
    public boolean isAsync() {
        return this != IN_APP;
    }
}