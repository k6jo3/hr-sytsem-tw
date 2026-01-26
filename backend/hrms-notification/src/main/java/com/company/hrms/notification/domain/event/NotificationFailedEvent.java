package com.company.hrms.notification.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 通知發送失敗事件
 * <p>
 * 當通知發送失敗時發布此事件
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFailedEvent extends DomainEvent {

    private String notificationId;
    private String recipientId;
    private String errorMessage;

    @Override
    public String getAggregateId() {
        return notificationId;
    }

    @Override
    public String getAggregateType() {
        return "Notification";
    }
}
