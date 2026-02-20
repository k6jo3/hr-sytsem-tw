package com.company.hrms.common.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.common.domain.event.EventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Kafka 事件發布器實作
 * 將領域事件發布到 Kafka Topic
 *
 * <p>
 * Topic 命名規則：{aggregate-type}.{event-type}
 * <ul>
 * <li>user.created - 使用者創建事件</li>
 * <li>employee.terminated - 員工離職事件</li>
 * </ul>
 */
@Component
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = true)
@SuppressWarnings("null")
public class KafkaEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    @SuppressWarnings("null")
    public void publish(DomainEvent event) {
        String topic = buildTopicName(event);
        String key = event.getAggregateId();

        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(topic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event {} to topic {}: {}",
                                    event.getEventType(), topic, ex.getMessage());
                        } else {
                            log.info("Published event {} to topic {} with key {}",
                                    event.getEventType(), topic, key);
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event {}: {}", event.getEventType(), e.getMessage());
            throw new RuntimeException("Event serialization failed", e);
        }
    }

    /**
     * 建構 Kafka Topic 名稱
     * 格式：{aggregate-type}.{event-action}
     * 例如：user.created, employee.terminated
     */
    private String buildTopicName(DomainEvent event) {
        String aggregateType = event.getAggregateType().toLowerCase();
        String eventType = event.getEventType()
                .replace("Event", "")
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase();

        // 移除聚合類型前綴
        if (eventType.startsWith(aggregateType)) {
            eventType = eventType.substring(aggregateType.length());
            if (eventType.startsWith("-")) {
                eventType = eventType.substring(1);
            }
        }

        return aggregateType + "." + eventType;
    }
}
