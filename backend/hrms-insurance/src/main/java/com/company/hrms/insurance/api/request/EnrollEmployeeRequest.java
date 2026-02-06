package com.company.hrms.insurance.api.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "員工ID不能為空")
    private String employeeId;

    /** 投保單位ID */
    @NotBlank(message = "投保單位ID不能為空")
    private String insuranceUnitId;

    /** 月薪 (用於計算投保級距) */
    @NotNull(message = "月薪不能為空")
    private BigDecimal monthlySalary;

    /** 加保日期 (格式: yyyy-MM-dd) */
    @NotBlank(message = "加保日期不能為空")
    private String enrollDate;

    /** 個人自提比例 (0~6%, 可選) */
    private BigDecimal selfContributionRate;
}
