package com.company.hrms.insurance.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投保級距調整事件
 * 發布到 Kafka，供 Payroll, Notification 等服務訂閱
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceLevelAdjustedEvent {

    /** 事件ID */
    private String eventId;

    /** 事件類型 */
    @Builder.Default
    private String eventType = "InsuranceLevelAdjusted";

    /** 時間戳 */
    private LocalDateTime timestamp;

    /** 租戶ID */
    private String tenantId;

    /** 事件內容 */
    private Payload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String employeeId;
        private String enrollmentId;
        private String insuranceType;
        private BigDecimal oldMonthlySalary;
        private BigDecimal newMonthlySalary;
        private int newLevelNumber;
        private LocalDate effectiveDate;
        private String reason;
    }

    /**
     * 建立事件
     */
    public static InsuranceLevelAdjustedEvent create(
            String employeeId,
            String enrollmentId,
            String insuranceType,
            BigDecimal oldSalary,
            BigDecimal newSalary,
            int newLevel,
            LocalDate effectiveDate,
            String reason,
            String tenantId) {

        return InsuranceLevelAdjustedEvent.builder()
                .eventId("evt-ins-" + UUID.randomUUID().toString().substring(0, 8))
                .timestamp(LocalDateTime.now())
                .tenantId(tenantId)
                .payload(Payload.builder()
                        .employeeId(employeeId)
                        .enrollmentId(enrollmentId)
                        .insuranceType(insuranceType)
                        .oldMonthlySalary(oldSalary)
                        .newMonthlySalary(newSalary)
                        .newLevelNumber(newLevel)
                        .effectiveDate(effectiveDate)
                        .reason(reason)
                        .build())
                .build();
    }
}
