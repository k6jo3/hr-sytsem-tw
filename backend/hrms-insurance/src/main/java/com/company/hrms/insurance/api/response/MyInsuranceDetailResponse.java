package com.company.hrms.insurance.api.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 我的保險詳細資訊回應 DTO
 * 包含加保記錄、保費計算、投保歷程
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyInsuranceDetailResponse {

    /** 員工姓名 */
    private String employeeName;

    /** 投保單位名稱 */
    private String unitName;

    /** 加保記錄列表 */
    private List<EnrollmentDetailResponse> enrollments;

    /** 保費計算結果 */
    private FeeCalculationResponse fees;

    /** 投保歷程 */
    private List<EnrollmentHistoryItem> history;

    /**
     * 投保歷程項目
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentHistoryItem {
        private String historyId;
        private String changeDate;
        private String changeType;
        private String insuranceType;
        private BigDecimal monthlySalary;
        private Integer levelNumber;
        private String reason;
    }
}
