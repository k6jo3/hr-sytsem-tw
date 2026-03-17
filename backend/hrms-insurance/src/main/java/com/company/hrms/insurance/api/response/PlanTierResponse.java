package com.company.hrms.insurance.api.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 方案職等對應回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanTierResponse {

    /** 職等方案 ID */
    private String tierId;

    /** 職等 */
    private String jobGrade;

    /** 保障金額 */
    private BigDecimal coverageAmount;

    /** 月繳保費 */
    private BigDecimal monthlyPremium;

    /** 公司負擔比例 */
    private BigDecimal employerShareRate;

    /** 公司負擔金額 */
    private BigDecimal employerAmount;

    /** 員工自付金額 */
    private BigDecimal employeeAmount;
}
