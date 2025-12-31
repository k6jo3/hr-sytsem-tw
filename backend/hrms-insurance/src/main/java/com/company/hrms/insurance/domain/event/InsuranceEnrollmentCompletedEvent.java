package com.company.hrms.insurance.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加保完成事件
 * 發布到 Kafka，供 Payroll 等服務訂閱
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceEnrollmentCompletedEvent {

    /** 事件ID */
    private String eventId;

    /** 事件類型 */
    @Builder.Default
    private String eventType = "InsuranceEnrollmentCompleted";

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
        private LocalDate enrollDate;
        private List<EnrollmentDetail> enrollments;
        private FeeDetail fees;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentDetail {
        private String enrollmentId;
        private String insuranceType;
        private BigDecimal monthlySalary;
        private LocalDate enrollDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeeDetail {
        private BigDecimal laborEmployeeFee;
        private BigDecimal laborEmployerFee;
        private BigDecimal healthEmployeeFee;
        private BigDecimal healthEmployerFee;
        private BigDecimal pensionEmployerFee;
    }

    /**
     * 建立事件
     */
    public static InsuranceEnrollmentCompletedEvent create(
            String employeeId,
            LocalDate enrollDate,
            List<EnrollmentDetail> enrollments,
            FeeDetail fees,
            String tenantId) {

        return InsuranceEnrollmentCompletedEvent.builder()
                .eventId("evt-ins-" + UUID.randomUUID().toString().substring(0, 8))
                .timestamp(LocalDateTime.now())
                .tenantId(tenantId)
                .payload(Payload.builder()
                        .employeeId(employeeId)
                        .enrollDate(enrollDate)
                        .enrollments(enrollments)
                        .fees(fees)
                        .build())
                .build();
    }
}
