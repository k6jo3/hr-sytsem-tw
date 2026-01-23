package com.company.hrms.notification.domain.model.aggregate;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.notification.domain.event.AnnouncementPublishedEvent;
import com.company.hrms.notification.domain.model.valueobject.AnnouncementId;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;
import com.company.hrms.notification.domain.model.valueobject.NotificationPriority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 公告聚合根
 * <p>
 * 管理系統公告的發布、更新與撤銷
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class Announcement extends AggregateRoot<AnnouncementId> {

    /**
     * 公告標題
     */
    private String title;

    /**
     * 公告內容
     */
    private String content;

    /**
     * 優先級
     */
    private NotificationPriority priority;

    /**
     * 發送渠道
     */
    private List<NotificationChannel> channels;

    /**
     * 目標對象類型 (ALL, DEPARTMENT, ROLE)
     */
    private String targetAudienceType;

    /**
     * 目標部門 ID 列表（當 type = DEPARTMENT 時使用）
     */
    private List<String> targetDepartmentIds;

    /**
     * 目標角色 ID 列表（當 type = ROLE 時使用）
     */
    private List<String> targetRoleIds;

    /**
     * 發布者員工 ID
     */
    private String publishedBy;

    /**
     * 發布時間（可排程）
     */
    private LocalDateTime publishedAt;

    /**
     * 過期時間
     */
    private LocalDateTime expireAt;

    /**
     * 公告狀態 (DRAFT, PUBLISHED, WITHDRAWN)
     */
    private AnnouncementStatus status;

    /**
     * 實際發送的收件人數量
     */
    private int recipientCount;

    /**
     * 公告狀態列舉
     */
    public enum AnnouncementStatus {
        DRAFT,      // 草稿
        PUBLISHED,  // 已發布
        WITHDRAWN   // 已撤銷
    }

    /**
     * 私有建構子，強制使用 Factory Method
     */
    private Announcement(AnnouncementId id) {
        super(id);
    }

    /**
     * 建立公告 (Factory Method)
     *
     * @param title              公告標題
     * @param content            公告內容
     * @param priority           優先級
     * @param channels           發送渠道
     * @param targetAudienceType 目標對象類型
     * @param publishedBy        發布者員工 ID
     * @param publishedAt        發布時間（可排程）
     * @param expireAt           過期時間
     * @return Announcement 實例
     */
    public static Announcement create(
            String title,
            String content,
            NotificationPriority priority,
            List<NotificationChannel> channels,
            String targetAudienceType,
            String publishedBy,
            LocalDateTime publishedAt,
            LocalDateTime expireAt) {

        // 驗證必填欄位
        Objects.requireNonNull(title, "公告標題不可為空");
        Objects.requireNonNull(content, "公告內容不可為空");
        Objects.requireNonNull(publishedBy, "發布者不可為空");

        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("公告標題不可為空白");
        }
        if (content.trim().isEmpty()) {
            throw new IllegalArgumentException("公告內容不可為空白");
        }

        // 驗證過期時間
        if (expireAt != null && expireAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("過期時間不可早於當前時間");
        }

        // 建立公告實例
        Announcement announcement = new Announcement(AnnouncementId.generate());
        announcement.title = title;
        announcement.content = content;
        announcement.priority = priority != null ? priority : NotificationPriority.NORMAL;
        announcement.channels = (channels != null && !channels.isEmpty())
                ? List.copyOf(channels)
                : List.of(NotificationChannel.IN_APP);
        announcement.targetAudienceType = targetAudienceType != null ? targetAudienceType : "ALL";
        announcement.publishedBy = publishedBy;
        announcement.publishedAt = publishedAt != null ? publishedAt : LocalDateTime.now();
        announcement.expireAt = expireAt;
        announcement.status = AnnouncementStatus.PUBLISHED;
        announcement.recipientCount = 0;

        // 發布公告發布事件
        announcement.registerEvent(new AnnouncementPublishedEvent(
                announcement.getId().getValue(),
                title,
                announcement.targetAudienceType,
                announcement.channels,
                publishedBy
        ));

        return announcement;
    }

    /**
     * 設定目標部門
     *
     * @param departmentIds 部門 ID 列表
     */
    public void setTargetDepartments(List<String> departmentIds) {
        this.targetAudienceType = "DEPARTMENT";
        this.targetDepartmentIds = departmentIds != null ? List.copyOf(departmentIds) : List.of();
        this.targetRoleIds = null;
        touch();
    }

    /**
     * 設定目標角色
     *
     * @param roleIds 角色 ID 列表
     */
    public void setTargetRoles(List<String> roleIds) {
        this.targetAudienceType = "ROLE";
        this.targetRoleIds = roleIds != null ? List.copyOf(roleIds) : List.of();
        this.targetDepartmentIds = null;
        touch();
    }

    /**
     * 更新公告內容
     *
     * @param title    標題
     * @param content  內容
     * @param priority 優先級
     * @param expireAt 過期時間
     */
    public void updateContent(
            String title,
            String content,
            NotificationPriority priority,
            LocalDateTime expireAt) {

        if (this.status == AnnouncementStatus.WITHDRAWN) {
            throw new IllegalStateException("已撤銷的公告無法修改");
        }

        if (this.isExpired()) {
            throw new IllegalStateException("已過期的公告無法修改");
        }

        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
        if (content != null && !content.trim().isEmpty()) {
            this.content = content;
        }
        if (priority != null) {
            this.priority = priority;
        }
        if (expireAt != null) {
            if (expireAt.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("過期時間不可早於當前時間");
            }
            this.expireAt = expireAt;
        }

        touch();

        // 公告更新事件（可選）
        // 如需追蹤公告修改歷史，可在此發布 AnnouncementUpdatedEvent
    }

    /**
     * 撤銷公告
     */
    public void withdraw() {
        if (this.status == AnnouncementStatus.WITHDRAWN) {
            throw new IllegalStateException("公告已被撤銷");
        }

        this.status = AnnouncementStatus.WITHDRAWN;
        touch();

        // 公告撤銷事件（可選）
        // 如需追蹤公告撤銷記錄，可在此發布 AnnouncementWithdrawnEvent
    }

    /**
     * 設定收件人數量
     *
     * @param count 收件人數量
     */
    public void setRecipientCount(int count) {
        this.recipientCount = count;
        touch();
    }

    /**
     * 檢查是否已過期
     *
     * @return true 表示已過期
     */
    public boolean isExpired() {
        return expireAt != null && LocalDateTime.now().isAfter(expireAt);
    }

    /**
     * 檢查是否已撤銷
     *
     * @return true 表示已撤銷
     */
    public boolean isWithdrawn() {
        return this.status == AnnouncementStatus.WITHDRAWN;
    }

    /**
     * 檢查是否為全員公告
     *
     * @return true 表示全員公告
     */
    public boolean isForAll() {
        return "ALL".equals(this.targetAudienceType);
    }

    // ========== Getters ==========

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public List<NotificationChannel> getChannels() {
        return channels;
    }

    public String getTargetAudienceType() {
        return targetAudienceType;
    }

    public List<String> getTargetDepartmentIds() {
        return targetDepartmentIds;
    }

    public List<String> getTargetRoleIds() {
        return targetRoleIds;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public AnnouncementStatus getStatus() {
        return status;
    }

    public int getRecipientCount() {
        return recipientCount;
    }
}
