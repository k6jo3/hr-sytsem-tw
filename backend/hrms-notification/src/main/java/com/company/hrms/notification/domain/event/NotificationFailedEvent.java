package com.company.hrms.notification.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

/**
 * 通知發送失敗事件
 * <p>
 * 當通知發送失敗時發布此事件
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class NotificationFailedEvent extends DomainEvent {

    private final String notificationId;
    private final String recipientId;
    private final String errorMessage;

    /**
     * 建構子
     *
     * @param notificationId 通知 ID
     * @param recipientId    收件人 ID
     * @param errorMessage   錯誤訊息
     */
    public NotificationFailedEvent(
            String notificationId,
            String recipientId,
            String errorMessage) {
        super();
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getAggregateId() {
        return notificationId;
    }

    @Override
    public String getAggregateType() {
        return "Notification";
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
