package com.company.hrms.payroll.application.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryItemRequest {
    private String code;
    private String name;
    private String type;
    private BigDecimal amount;
    private boolean fixedAmount;
    private boolean taxable;
    private boolean insurable;
}
