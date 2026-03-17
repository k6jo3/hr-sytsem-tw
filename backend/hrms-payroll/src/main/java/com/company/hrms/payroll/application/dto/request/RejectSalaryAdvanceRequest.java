package com.company.hrms.payroll.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 駁回預借薪資請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectSalaryAdvanceRequest {

    /** 駁回原因 */
    @NotBlank(message = "駁回原因不可為空")
    private String reason;
}
