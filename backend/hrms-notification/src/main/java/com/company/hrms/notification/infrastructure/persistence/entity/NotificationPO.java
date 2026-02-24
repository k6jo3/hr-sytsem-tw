package com.company.hrms.notification.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知持久化物件 (Persistence Object)
 * <p>
 * 對應 Notification 聚合根的資料庫表結構
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_recipient_id", columnList = "recipient_id"),
        @Index(name = "idx_notification_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPO {

    /**
     * 通知 ID (主鍵)
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 收件人 ID (員工 ID)
     */
    @Column(name = "recipient_id", length = 50, nullable = false)
    private String recipientId;

    /**
     * 標題
     */
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    /**
     * 內容
     */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 通知類型
     */
    @Column(name = "notification_type", length = 50, nullable = false)
    private String notificationType;

    /**
     * 優先級
     */
    @Column(name = "priority", length = 20, nullable = false)
    private String priority;

    /**
     * 狀態
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    /**
     * 發送渠道列表 (JSON 格式儲存)
     */
    @Column(name = "channels", columnDefinition = "TEXT")
    private String channels;

    /**
     * 相關業務 ID
     */
    @Column(name = "related_business_id", length = 50)
    private String relatedBusinessId;

    /**
     * 相關業務類型
     */
    @Column(name = "related_business_type", length = 50)
    private String relatedBusinessType;

    /**
     * 相關業務 URL
     */
    @Column(name = "related_business_url", length = 500)
    private String relatedBusinessUrl;

    /**
     * 範本代碼
     */
    @Column(name = "template_code", length = 50)
    private String templateCode;

    /**
     * 範本變數 (JSON 格式儲存)
     */
    @Column(name = "template_variables", columnDefinition = "TEXT")
    private String templateVariables;

    /**
     * 發送時間
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /**
     * 已讀時間
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 失敗原因
     */
    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    /**
     * 重試次數
     */
    @Column(name = "retry_count")
    private Integer retryCount;

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
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
