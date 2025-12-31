package com.company.hrms.insurance.api.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 調整投保級距請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustLevelRequest {

    /** 新月薪 (用於重新計算投保級距) */
    private BigDecimal newMonthlySalary;

    /** 生效日期 (格式: yyyy-MM-dd) */
    private String effectiveDate;

    /** 調整原因 */
    private String reason;
}
