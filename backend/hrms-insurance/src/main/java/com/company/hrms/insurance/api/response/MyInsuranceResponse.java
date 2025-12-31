package com.company.hrms.insurance.api.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 我的保險資訊回應 DTO (ESS)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyInsuranceResponse {

    /** 投保單位名稱 */
    private String unitName;

    /** 加保日期 */
    private String enrollDate;

    /** 投保薪資 */
    private BigDecimal monthlySalary;

    /** 投保級距 */
    private Integer levelNumber;

    /** 投保狀態 */
    private String status;

    /** 保費明細 */
    private FeeDetail fees;

    /** 加保記錄列表 */
    private List<EnrollmentSummary> enrollments;

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
        private BigDecimal totalEmployeeFee;
        private BigDecimal totalEmployerFee;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentSummary {
        private String insuranceType;
        private String status;
        private String enrollDate;
        private BigDecimal monthlySalary;
    }
}
