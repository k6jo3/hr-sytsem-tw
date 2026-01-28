package com.company.hrms.notification.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 通知已讀事件
 * <p>
 * 當使用者標記通知為已讀時發布此事件
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReadEvent extends DomainEvent {

    private String notificationId;
    private String recipientId;

    @Override
    public String getAggregateId() {
        return notificationId;
    }

    @Override
    public String getAggregateType() {
        return "Notification";
    }
}
