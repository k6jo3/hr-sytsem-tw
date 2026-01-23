package com.company.hrms.notification.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;

import java.util.List;

/**
 * 通知建立事件
 * <p>
 * 當通知被建立時發布此事件
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
public class NotificationCreatedEvent extends DomainEvent {

    private final String notificationId;
    private final String recipientId;
    private final List<NotificationChannel> channels;

    /**
     * 建構子
     *
     * @param notificationId 通知 ID
     * @param recipientId    收件人 ID
     * @param channels       發送渠道
     */
    public NotificationCreatedEvent(
            String notificationId,
            String recipientId,
            List<NotificationChannel> channels) {
        super();
        this.notificationId = notificationId;
        this.recipientId = recipientId;
        this.channels = channels;
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

    public List<NotificationChannel> getChannels() {
        return channels;
    }
}
