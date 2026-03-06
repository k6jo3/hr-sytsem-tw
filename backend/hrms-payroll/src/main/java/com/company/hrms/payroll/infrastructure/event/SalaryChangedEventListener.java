package com.company.hrms.payroll.infrastructure.event;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 監聯 EmployeeSalaryChangedEvent（Kafka）
 *
 * <p>訂閱 Organization 服務發布的員工調薪事件，觸發薪資結構更新。
 * 僅在 Kafka 啟用時生效。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class SalaryChangedEventListener {

    private final SalaryStructureEventHandler salaryStructureEventHandler;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "hrms.employee.salary-changed", groupId = "payroll-service")
    public void handleSalaryChanged(String message) {
        log.info("[SalaryChangedListener] 收到調薪事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);
            String employeeId = json.path("employeeId").asText();
            BigDecimal newSalary = new BigDecimal(json.path("newSalary").asText());
            LocalDate effectiveDate = LocalDate.parse(json.path("effectiveDate").asText());
            String reason = json.path("reason").asText(null);

            salaryStructureEventHandler.updateSalaryFromEvent(
                    employeeId, newSalary, effectiveDate, reason);

        } catch (Exception e) {
            log.error("[SalaryChangedListener] 處理調薪事件失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 薪資結構事件處理介面
     *
     * <p>由 Application 層實作，負責將外部調薪事件轉化為薪資結構更新。
     */
    public interface SalaryStructureEventHandler {

        /**
         * 根據外部調薪事件更新薪資結構
         *
         * @param employeeId    員工 ID
         * @param newSalary     新薪資
         * @param effectiveDate 生效日期
         * @param reason        調薪原因
         */
        void updateSalaryFromEvent(String employeeId, BigDecimal newSalary,
                                   LocalDate effectiveDate, String reason);
    }
}
