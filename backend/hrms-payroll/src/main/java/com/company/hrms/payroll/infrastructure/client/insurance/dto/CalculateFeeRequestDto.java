package com.company.hrms.payroll.infrastructure.client.insurance.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculateFeeRequestDto {
    private BigDecimal monthlySalary;
    private BigDecimal selfContributionRate;
}
