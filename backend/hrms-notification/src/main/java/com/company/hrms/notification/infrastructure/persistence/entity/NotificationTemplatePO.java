package com.company.hrms.notification.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知範本持久化物件 (Persistence Object)
 * <p>
 * 對應 NotificationTemplate 聚合根的資料庫表結構
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Entity
@Table(name = "notification_templates", indexes = {
        @Index(name = "idx_template_code", columnList = "template_code", unique = true),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplatePO {

    /**
     * 範本 ID (主鍵)
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 範本代碼 (唯一)
     */
    @Column(name = "template_code", length = 50, nullable = false, unique = true)
    private String templateCode;

    /**
     * 範本名稱
     */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * 範本說明
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 標題範本
     */
    @Column(name = "subject", length = 200, nullable = false)
    private String subject;

    /**
     * 內容範本
     */
    @Column(name = "body", columnDefinition = "TEXT", nullable = false)
    private String body;

    /**
     * 通知類型
     */
    @Column(name = "notification_type", length = 50, nullable = false)
    private String notificationType;

    /**
     * 預設優先級
     */
    @Column(name = "default_priority", length = 20, nullable = false)
    private String defaultPriority;

    /**
     * 預設渠道列表 (JSON 格式儲存)
     */
    @Column(name = "default_channels", columnDefinition = "TEXT")
    private String defaultChannels;

    /**
     * 範本變數說明 (JSON 格式儲存)
     */
    @Column(name = "variables", columnDefinition = "TEXT")
    private String variables;

    /**
     * 狀態 (ACTIVE, INACTIVE)
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status;

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
