package com.company.hrms.notification.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.notification.domain.model.valueobject.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreatedEvent extends DomainEvent {

    private String notificationId;
    private String recipientId;
    private List<NotificationChannel> channels;

    @Override
    public String getAggregateId() {
        return notificationId;
    }

    @Override
    public String getAggregateType() {
        return "Notification";
    }
}
