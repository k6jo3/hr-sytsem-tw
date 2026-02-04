package com.company.hrms.common.infrastructure.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.hrms.common.domain.event.EventPublisher;

/**
 * 事件發布器配置
 */
@Configuration
public class EventPublisherConfig {

    /**
     * 當 spring.kafka.enabled=false 時，使用記憶體內事件發布器
     */
    @Bean
    @ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "false")
    @ConditionalOnMissingBean(EventPublisher.class)
    public EventPublisher inMemoryEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new InMemoryEventPublisher(applicationEventPublisher);
    }
}
