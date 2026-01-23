package com.company.hrms.notification.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

/**
 * 通知已讀事件
 * <p>
 * 當使用者標記通知為已讀時發布此事件
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class NotificationReadEvent extends DomainEvent {

    private final String notificationId;
    private final String recipientId;

    /**
     * 建構子
     *
     * @param notificationId 通知 ID
     * @param recipientId    收件人 ID
     */
    public NotificationReadEvent(String notificationId, String recipientId) {
        super();
        this.notificationId = notificationId;
        this.recipientId = recipientId;
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
}
