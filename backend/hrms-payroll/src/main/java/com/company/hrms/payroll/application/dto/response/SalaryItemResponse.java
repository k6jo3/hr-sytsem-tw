package com.company.hrms.payroll.application.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryItemResponse {
    private String itemId;
    private String code;
    private String name;
    private String type;
    private BigDecimal amount;
    private boolean fixedAmount;
    private boolean taxable;
    private boolean insurable;
}
