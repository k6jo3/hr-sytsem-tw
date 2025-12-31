package com.company.hrms.insurance.api.request;

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
    private String withdrawDate;

    /** 退保原因 */
    private String reason;
}
