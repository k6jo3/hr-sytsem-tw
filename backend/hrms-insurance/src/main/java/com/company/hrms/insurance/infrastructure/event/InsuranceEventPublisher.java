package com.company.hrms.insurance.infrastructure.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.company.hrms.insurance.domain.event.InsuranceEnrollmentCompletedEvent;
import com.company.hrms.insurance.domain.event.InsuranceLevelAdjustedEvent;
import com.company.hrms.insurance.domain.event.InsuranceWithdrawalCompletedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 保險事件發布器
 * 負責將領域事件發布到 Kafka
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InsuranceEventPublisher {

    private static final String TOPIC_ENROLLMENT = "insurance.enrollment";
    private static final String TOPIC_WITHDRAWAL = "insurance.withdrawal";
    private static final String TOPIC_ADJUSTMENT = "insurance.adjustment";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * 發布加保完成事件
     */
    public void publishEnrollmentCompleted(InsuranceEnrollmentCompletedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_ENROLLMENT, event.getPayload().getEmployeeId(), message);
            log.info("發布加保完成事件: eventId={}, employeeId={}",
                    event.getEventId(), event.getPayload().getEmployeeId());
        } catch (JsonProcessingException e) {
            log.error("序列化事件失敗", e);
            throw new RuntimeException("事件發布失敗", e);
        }
    }

    /**
     * 發布退保完成事件
     */
    public void publishWithdrawalCompleted(InsuranceWithdrawalCompletedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_WITHDRAWAL, event.getPayload().getEmployeeId(), message);
            log.info("發布退保完成事件: eventId={}, employeeId={}",
                    event.getEventId(), event.getPayload().getEmployeeId());
        } catch (JsonProcessingException e) {
            log.error("序列化事件失敗", e);
            throw new RuntimeException("事件發布失敗", e);
        }
    }

    /**
     * 發布級距調整事件
     */
    public void publishLevelAdjusted(InsuranceLevelAdjustedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_ADJUSTMENT, event.getPayload().getEmployeeId(), message);
            log.info("發布級距調整事件: eventId={}, employeeId={}, {}->{}",
                    event.getEventId(),
                    event.getPayload().getEmployeeId(),
                    event.getPayload().getOldMonthlySalary(),
                    event.getPayload().getNewMonthlySalary());
        } catch (JsonProcessingException e) {
            log.error("序列化事件失敗", e);
            throw new RuntimeException("事件發布失敗", e);
        }
    }
}
