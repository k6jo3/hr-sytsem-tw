package com.company.hrms.payroll.application.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 預借薪資申請請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplySalaryAdvanceRequest {

    /** 員工 ID */
    @NotBlank(message = "員工 ID 不可為空")
    private String employeeId;

    /** 申請金額 */
    @NotNull(message = "申請金額不可為空")
    @Min(value = 1, message = "申請金額必須大於 0")
    private BigDecimal requestedAmount;

    /** 分期月數 */
    @NotNull(message = "分期月數不可為空")
    @Min(value = 1, message = "分期月數必須 >= 1")
    private Integer installmentMonths;

    /** 申請原因 */
    @NotBlank(message = "申請原因不可為空")
    private String reason;
}
