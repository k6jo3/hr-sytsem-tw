package com.company.hrms.insurance.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 退保請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawEnrollmentRequest {

    /** 退保日期 (格式: yyyy-MM-dd) */
    @NotBlank(message = "退保日期不能為空")
    private String withdrawDate;

    /** 退保原因 */
    @NotBlank(message = "退保原因不能為空")
    private String reason;
}
