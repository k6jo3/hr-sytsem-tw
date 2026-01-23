package com.company.hrms.notification.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告持久化物件 (Persistence Object)
 * <p>
 * 對應 Announcement 聚合根的資料庫表結構
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Entity
@Table(name = "announcements", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_published_at", columnList = "published_at"),
        @Index(name = "idx_published_by", columnList = "published_by"),
        @Index(name = "idx_effective_dates", columnList = "effective_from, effective_to")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementPO {

    /**
     * 公告 ID (主鍵)
     */
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 公告標題
     */
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    /**
     * 公告內容
     */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 狀態 (DRAFT, PUBLISHED, WITHDRAWN)
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    /**
     * 優先級
     */
    @Column(name = "priority", length = 20, nullable = false)
    private String priority;

    /**
     * 目標對象 (ALL, DEPARTMENT, ROLE, SPECIFIC)
     */
    @Column(name = "target_audience", length = 50, nullable = false)
    private String targetAudience;

    /**
     * 目標部門 ID 列表 (JSON 格式儲存)
     */
    @Column(name = "target_department_ids", columnDefinition = "TEXT")
    private String targetDepartmentIds;

    /**
     * 目標角色 ID 列表 (JSON 格式儲存)
     */
    @Column(name = "target_role_ids", columnDefinition = "TEXT")
    private String targetRoleIds;

    /**
     * 目標員工 ID 列表 (JSON 格式儲存)
     */
    @Column(name = "target_employee_ids", columnDefinition = "TEXT")
    private String targetEmployeeIds;

    /**
     * 發布者 ID
     */
    @Column(name = "published_by", length = 50)
    private String publishedBy;

    /**
     * 發布時間
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /**
     * 生效開始時間
     */
    @Column(name = "effective_from", nullable = false)
    private LocalDateTime effectiveFrom;

    /**
     * 生效結束時間
     */
    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    /**
     * 是否置頂
     */
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned;

    /**
     * 附件 URL 列表 (JSON 格式儲存)
     */
    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    /**
     * 已讀數量
     */
    @Column(name = "read_count")
    private Integer readCount;

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
        if (this.isPinned == null) {
            this.isPinned = false;
        }
        if (this.readCount == null) {
            this.readCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
