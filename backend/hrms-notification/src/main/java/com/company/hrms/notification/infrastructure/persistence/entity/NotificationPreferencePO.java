package com.company.hrms.notification.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 通知偏好設定持久化物件 (Persistence Object)
 * <p>
 * 對應 NotificationPreference 聚合根的資料庫表結構
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Entity
@Table(name = "notification_preferences", indexes = {
        @Index(name = "idx_employee_id", columnList = "employee_id", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencePO {

    /**
     * 偏好設定 ID (主鍵)
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 員工 ID (唯一)
     */
    @Column(name = "employee_id", length = 50, nullable = false, unique = true)
    private String employeeId;

    /**
     * 系統內通知啟用
     */
    @Column(name = "in_app_enabled", nullable = false)
    private Boolean inAppEnabled;

    /**
     * Email 通知啟用
     */
    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled;

    /**
     * 推播通知啟用
     */
    @Column(name = "push_enabled", nullable = false)
    private Boolean pushEnabled;

    /**
     * Teams 通知啟用
     */
    @Column(name = "teams_enabled", nullable = false)
    private Boolean teamsEnabled;

    /**
     * LINE 通知啟用
     */
    @Column(name = "line_enabled", nullable = false)
    private Boolean lineEnabled;

    /**
     * 靜音時段啟用
     */
    @Column(name = "quiet_hours_enabled", nullable = false)
    private Boolean quietHoursEnabled;

    /**
     * 靜音時段開始時間
     */
    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    /**
     * 靜音時段結束時間
     */
    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;

    /**
     * Email 地址
     */
    @Column(name = "email_address", length = 100)
    private String emailAddress;

    /**
     * 推播裝置 Token (JSON 格式儲存多個裝置)
     */
    @Column(name = "push_tokens", columnDefinition = "TEXT")
    private String pushTokens;

    /**
     * LINE User ID
     */
    @Column(name = "line_user_id", length = 100)
    private String lineUserId;

    /**
     * Teams Webhook URL
     */
    @Column(name = "teams_webhook_url", length = 500)
    private String teamsWebhookUrl;

    /**
     * 建立時間
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 建立者
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * 更新者
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * 版本號 (樂觀鎖)
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * 軟刪除標記
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
