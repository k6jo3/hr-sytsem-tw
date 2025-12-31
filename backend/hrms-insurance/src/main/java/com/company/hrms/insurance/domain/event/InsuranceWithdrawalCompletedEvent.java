package com.company.hrms.insurance.domain.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 退保完成事件
 * 發布到 Kafka，供 Payroll 等服務訂閱
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceWithdrawalCompletedEvent {

    /** 事件ID */
    private String eventId;

    /** 事件類型 */
    @Builder.Default
    private String eventType = "InsuranceWithdrawalCompleted";

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
        private LocalDate withdrawDate;
        private String reason;
    }

    /**
     * 建立事件
     */
    public static InsuranceWithdrawalCompletedEvent create(
            String employeeId,
            String enrollmentId,
            String insuranceType,
            LocalDate withdrawDate,
            String reason,
            String tenantId) {

        return InsuranceWithdrawalCompletedEvent.builder()
                .eventId("evt-ins-" + UUID.randomUUID().toString().substring(0, 8))
                .timestamp(LocalDateTime.now())
                .tenantId(tenantId)
                .payload(Payload.builder()
                        .employeeId(employeeId)
                        .enrollmentId(enrollmentId)
                        .insuranceType(insuranceType)
                        .withdrawDate(withdrawDate)
                        .reason(reason)
                        .build())
                .build();
    }
}
