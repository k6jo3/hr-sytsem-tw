package com.company.hrms.insurance.infrastructure.event;

import java.time.LocalDate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.company.hrms.insurance.application.service.withdrawal.AutoWithdrawOnTerminationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 監聽 EmployeeTerminatedEvent（Kafka）
 *
 * <p>訂閱 Organization 服務發布的員工離職事件，觸發自動退保流程。
 * 僅在 Kafka 啟用時生效。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class EmployeeTerminatedEventListener {

    private final AutoWithdrawOnTerminationService autoWithdrawService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "hrms.employee.terminated", groupId = "insurance-service")
    public void handleEmployeeTerminated(String message) {
        log.info("[EmployeeTerminatedListener] 收到離職事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);
            String employeeId = json.path("employeeId").asText();
            String terminationDateStr = json.path("terminationDate").asText();
            String tenantId = json.path("tenantId").asText(null);

            LocalDate terminationDate = LocalDate.parse(terminationDateStr);
            autoWithdrawService.withdrawAllOnTermination(employeeId, terminationDate, tenantId);

        } catch (Exception e) {
            log.error("[EmployeeTerminatedListener] 處理離職事件失敗: {}", e.getMessage(), e);
        }
    }
}
