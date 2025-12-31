package com.company.hrms.insurance.api.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保費計算請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateFeeRequest {

    /** 月薪 */
    private BigDecimal monthlySalary;

    /** 個人自提比例 (0~6%, 可選) */
    private BigDecimal selfContributionRate;
}
