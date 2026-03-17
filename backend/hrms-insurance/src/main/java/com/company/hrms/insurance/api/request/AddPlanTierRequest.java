package com.company.hrms.insurance.api.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增方案職等對應請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPlanTierRequest {

    /** 職等（如 M1, M2, E1, E2） */
    @NotBlank(message = "職等不可為空")
    private String jobGrade;

    /** 保障金額 */
    @NotNull(message = "保障金額不可為空")
    @Positive(message = "保障金額必須為正數")
    private BigDecimal coverageAmount;

    /** 月繳保費 */
    @NotNull(message = "月繳保費不可為空")
    @Positive(message = "月繳保費必須為正數")
    private BigDecimal monthlyPremium;

    /** 公司負擔比例（0~1） */
    @NotNull(message = "公司負擔比例不可為空")
    @DecimalMin(value = "0", message = "公司負擔比例不可小於 0")
    @DecimalMax(value = "1", message = "公司負擔比例不可大於 1")
    private BigDecimal employerShareRate;
}
