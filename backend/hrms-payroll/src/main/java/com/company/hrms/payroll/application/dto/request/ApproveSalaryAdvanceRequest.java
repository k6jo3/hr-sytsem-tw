package com.company.hrms.payroll.application.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 核准預借薪資請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveSalaryAdvanceRequest {

    /** 核准金額（可與申請金額不同） */
    @NotNull(message = "核准金額不可為空")
    private BigDecimal approvedAmount;
}
