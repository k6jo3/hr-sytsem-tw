package com.company.hrms.insurance.api.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加保請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollEmployeeRequest {

    /** 員工ID */
    private String employeeId;

    /** 投保單位ID */
    private String insuranceUnitId;

    /** 月薪 (用於計算投保級距) */
    private BigDecimal monthlySalary;

    /** 加保日期 (格式: yyyy-MM-dd) */
    private String enrollDate;

    /** 個人自提比例 (0~6%, 可選) */
    private BigDecimal selfContributionRate;
}
