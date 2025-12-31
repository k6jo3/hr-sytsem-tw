package com.company.hrms.insurance.api.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 補充保費計算結果回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplementaryPremiumResponse {

    /** 是否需要繳納補充保費 */
    private boolean required;

    /** 門檻金額 (投保薪資 × 4) */
    private BigDecimal threshold;

    /** 計費基準 (收入 - 門檻，上限 1000 萬) */
    private BigDecimal premiumBase;

    /** 補充保費金額 (計費基準 × 2.11%) */
    private BigDecimal premiumAmount;

    /** 投保薪資 */
    private BigDecimal insuredSalary;

    /** 收入金額 */
    private BigDecimal incomeAmount;
}
