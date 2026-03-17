package com.company.hrms.attendance.infrastructure.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.company.hrms.attendance.domain.event.AnnualLeaveSettlementEvent;
import com.company.hrms.attendance.domain.model.aggregate.LeaveBalance;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveBalanceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 監聽 EmployeeTerminatedEvent（Kafka）
 *
 * <p>訂閱 Organization 服務發布的員工離職事件，
 * 查詢員工剩餘特休天數並發布 AnnualLeaveSettlementEvent 供 Payroll 使用。
 * 僅在 Kafka 啟用時生效。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class EmployeeTerminatedEventListener {

    private final ILeaveBalanceRepository leaveBalanceRepository;
    private final ObjectMapper objectMapper;

    /**
     * 處理員工離職事件，結算剩餘特休天數
     *
     * @param message Kafka 訊息（JSON 格式的 EmployeeTerminatedEvent）
     * @return 結算事件（若有剩餘天數），否則 null
     */
    @KafkaListener(topics = "hrms.employee.terminated", groupId = "attendance-service")
    public AnnualLeaveSettlementEvent handleEmployeeTerminated(String message) {
        log.info("[AttendanceTerminatedListener] 收到離職事件: {}", message);

        try {
            JsonNode json = objectMapper.readTree(message);
            String employeeId = json.path("employeeId").asText();
            String terminationDateStr = json.path("terminationDate").asText();
            LocalDate terminationDate = LocalDate.parse(terminationDateStr);

            // 查詢該員工當年度所有假別餘額
            int currentYear = terminationDate.getYear();
            List<LeaveBalance> balances = leaveBalanceRepository
                .findByEmployeeIdAndYear(employeeId, currentYear);

            // 篩選特休假餘額（LeaveTypeId = "ANNUAL"）並加總剩餘天數
            BigDecimal totalRemaining = balances.stream()
                .filter(b -> "ANNUAL".equals(b.getLeaveTypeId().getValue()))
                .map(LeaveBalance::getAvailableDays)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalRemaining.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("[AttendanceTerminatedListener] 員工 {} 無剩餘特休天數，不需結算", employeeId);
                return null;
            }

            // 建立結算事件（預估金額由 Payroll 計算，此處傳 null）
            AnnualLeaveSettlementEvent event = new AnnualLeaveSettlementEvent(
                employeeId, totalRemaining, terminationDate, null
            );

            log.info("[AttendanceTerminatedListener] 員工 {} 剩餘特休 {} 天，已產生結算事件",
                employeeId, totalRemaining);

            // TODO: 透過 EventPublisher 發布到 Kafka topic hrms.leave.settlement
            // eventPublisher.publish(event);

            return event;

        } catch (Exception e) {
            log.error("[AttendanceTerminatedListener] 處理離職事件失敗: {}", e.getMessage(), e);
            return null;
        }
    }
}
