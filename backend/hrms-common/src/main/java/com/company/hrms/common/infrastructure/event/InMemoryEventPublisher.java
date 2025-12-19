package com.company.hrms.common.infrastructure.event;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.common.domain.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 記憶體內事件發布器實作
 * 使用 Spring ApplicationEvent 機制發布事件
 * 適用於開發測試環境或單體應用
 *
 * <p>使用方式：
 * <pre>
 * {@literal @}EventListener
 * public void handleUserCreated(UserCreatedEvent event) {
 *     // 處理事件
 * }
 * </pre>
 */
public class InMemoryEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InMemoryEventPublisher.class);

    private final ApplicationEventPublisher applicationEventPublisher;

    public InMemoryEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        log.debug("Publishing event: {} for aggregate: {}",
                event.getEventType(), event.getAggregateId());

        applicationEventPublisher.publishEvent(event);

        log.info("Published event: {}", event.getEventType());
    }
}
