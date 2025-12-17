package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 領域事件基類
 */
@Getter
public abstract class DomainEvent {

    /**
     * 事件 ID
     */
    private final String eventId;

    /**
     * 事件發生時間
     */
    private final LocalDateTime occurredAt;

    /**
     * 事件類型
     */
    private final String eventType;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }
}
