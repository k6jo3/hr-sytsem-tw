package com.company.hrms.notification.domain.model.aggregate;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.notification.domain.event.*;
import com.company.hrms.notification.domain.model.valueobject.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 通知聚合根
 * <p>
 * 管理通知的建立、發送與狀態追蹤
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class Notification extends AggregateRoot<NotificationId> {

    /**
     * 收件人員工 ID
     */
    private String recipientId;

    /**
     * 通知標題
     */
    private String title;

    /**
     * 通知內容
     */
    private String content;

    /**
     * 通知類型
     */
    private NotificationType notificationType;

    /**
     * 發送渠道列表
     */
    private List<NotificationChannel> channels;

    /**
     * 優先級
     */
    private NotificationPriority priority;

    /**
     * 通知狀態
     */
    private NotificationStatus status;

    /**
     * 發送時間
     */
    private LocalDateTime sentAt;

    /**
     * 已讀時間
     */
    private LocalDateTime readAt;

    /**
     * 關聯業務類型
     */
    private String relatedBusinessType;

    /**
     * 關聯業務 ID
     */
    private String relatedBusinessId;

    /**
     * 關聯業務 URL
     */
    private String relatedBusinessUrl;

    /**
     * 範本代碼
     */
    private String templateCode;

    /**
     * 範本變數
     */
    private Map<String, Object> templateVariables;

    /**
     * 發送失敗原因
     */
    private String failureReason;

    /**
     * 重試次數
     */
    private int retryCount;

    /**
     * 建立者
     */
    private String createdBy;

    /**
     * 最後更新者
     */
    private String updatedBy;

    /**
     * 版本號 (樂觀鎖)
     */
    private Long version;

    /**
     * 軟刪除標記
     */
    private Boolean isDeleted;

    /**
     * Package-private 建構子，供 Mapper 重建物件使用
     * 一般使用請使用 Factory Method (create)
     */
    public Notification(NotificationId id) {
        super(id);
        this.retryCount = 0;
        this.isDeleted = false;
    }

    /**
     * 建立通知 (Factory Method)
     *
     * @param recipientId      收件人 ID
     * @param title            標題
     * @param content          內容
     * @param notificationType 通知類型
     * @param channels         發送渠道
     * @param priority         優先級
     * @return Notification 實例
     */
    public static Notification create(
            String recipientId,
            String title,
            String content,
            NotificationType notificationType,
            List<NotificationChannel> channels,
            NotificationPriority priority) {

        // 驗證必填欄位
        Objects.requireNonNull(recipientId, "收件人 ID 不可為空");
        Objects.requireNonNull(title, "通知標題不可為空");
        Objects.requireNonNull(content, "通知內容不可為空");
        Objects.requireNonNull(notificationType, "通知類型不可為空");

        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("通知標題不可為空白");
        }
        if (content.trim().isEmpty()) {
            throw new IllegalArgumentException("通知內容不可為空白");
        }

        // 建立通知實例
        Notification notification = new Notification(NotificationId.generate());
        notification.recipientId = recipientId;
        notification.title = title;
        notification.content = content;
        notification.notificationType = notificationType;
        notification.channels = (channels != null && !channels.isEmpty())
                ? List.copyOf(channels)
                : List.of(NotificationChannel.IN_APP);
        notification.priority = priority != null ? priority : NotificationPriority.NORMAL;
        notification.status = NotificationStatus.PENDING;

        // 發布通知建立事件
        notification.registerEvent(new NotificationCreatedEvent(
                notification.getId().getValue(),
                recipientId,
                notification.channels
        ));

        return notification;
    }

    /**
     * 標記為已發送
     *
     * @throws IllegalStateException 當通知狀態不是 PENDING 時
     */
    public void markAsSent() {
        if (this.status != NotificationStatus.PENDING) {
            throw new IllegalStateException("只有待發送的通知可以標記為已發送");
        }

        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        touch();

        // 發布通知發送成功事件
        this.registerEvent(new NotificationSentEvent(
                this.getId().getValue(),
                this.recipientId,
                this.channels
        ));
    }

    /**
     * 標記為發送失敗
     *
     * @param errorMessage 錯誤訊息
     */
    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.failureReason = errorMessage;
        this.retryCount++;
        touch();

        // 發布通知發送失敗事件
        this.registerEvent(new NotificationFailedEvent(
                this.getId().getValue(),
                this.recipientId,
                errorMessage
        ));
    }

    /**
     * 標記為已讀
     *
     * @throws IllegalStateException 當通知狀態不是 SENT 時
     */
    public void markAsRead() {
        if (this.status != NotificationStatus.SENT) {
            throw new IllegalStateException("只有已發送的通知可以標記為已讀");
        }

        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
        touch();

        // 發布通知已讀事件
        this.registerEvent(new NotificationReadEvent(
                this.getId().getValue(),
                this.recipientId
        ));
    }

    /**
     * 設定業務關聯
     *
     * @param businessType 業務類型
     * @param businessId   業務 ID
     */
    public void setBusinessRelation(String businessType, String businessId) {
        this.relatedBusinessType = businessType;
        this.relatedBusinessId = businessId;
        touch();
    }

    /**
     * 設定業務關聯 URL
     *
     * @param businessUrl 業務詳情 URL
     */
    public void setBusinessUrl(String businessUrl) {
        this.relatedBusinessUrl = businessUrl;
        touch();
    }

    /**
     * 檢查是否未讀
     *
     * @return true 表示未讀
     */
    public boolean isUnread() {
        return this.status == NotificationStatus.SENT;
    }

    /**
     * 檢查是否已發送
     *
     * @return true 表示已發送（包含已讀狀態）
     */
    public boolean isSent() {
        return this.status == NotificationStatus.SENT || this.status == NotificationStatus.READ;
    }

    /**
     * 檢查是否失敗
     *
     * @return true 表示發送失敗
     */
    public boolean isFailed() {
        return this.status == NotificationStatus.FAILED;
    }

    // ========== Getters ==========

    public String getRecipientId() {
        return recipientId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public List<NotificationChannel> getChannels() {
        return channels;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public String getRelatedBusinessType() {
        return relatedBusinessType;
    }

    public String getRelatedBusinessId() {
        return relatedBusinessId;
    }

    public String getRelatedBusinessUrl() {
        return relatedBusinessUrl;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public Map<String, Object> getTemplateVariables() {
        return templateVariables;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Long getVersion() {
        return version;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    // ========== Setters (for Mapper) ==========

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public void setChannels(List<NotificationChannel> channels) {
        this.channels = channels;
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public void setRelatedBusinessType(String relatedBusinessType) {
        this.relatedBusinessType = relatedBusinessType;
    }

    public void setRelatedBusinessId(String relatedBusinessId) {
        this.relatedBusinessId = relatedBusinessId;
    }

    public void setRelatedBusinessUrl(String relatedBusinessUrl) {
        this.relatedBusinessUrl = relatedBusinessUrl;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public void setTemplateVariables(Map<String, Object> templateVariables) {
        this.templateVariables = templateVariables;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
