package com.company.hrms.insurance.api.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加保回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollEmployeeResponse {

    /** 員工ID */
    private String employeeId;

    /** 加保記錄ID列表 */
    private List<EnrollmentRecord> enrollments;

    /** 員工每月負擔總計 */
    private BigDecimal totalEmployeeFee;

    /** 雇主每月負擔總計 */
    private BigDecimal totalEmployerFee;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentRecord {
        private String enrollmentId;
        private String insuranceType;
        private String enrollDate;
        private BigDecimal monthlySalary;
        private int levelNumber;
    }
}
