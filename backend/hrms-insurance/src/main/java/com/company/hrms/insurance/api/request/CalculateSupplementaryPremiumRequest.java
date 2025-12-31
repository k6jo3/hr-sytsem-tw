package com.company.hrms.insurance.api.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 補充保費計算請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateSupplementaryPremiumRequest {

    /** 員工ID */
    private String employeeId;

    /** 收入類型 (BONUS, PART_TIME_INCOME, PROFESSIONAL_FEE) */
    private String incomeType;

    /** 收入金額 */
    private BigDecimal incomeAmount;

    /** 收入日期 (格式: yyyy-MM-dd) */
    private String incomeDate;

    /** 投保薪資 */
    private BigDecimal insuredSalary;
}
