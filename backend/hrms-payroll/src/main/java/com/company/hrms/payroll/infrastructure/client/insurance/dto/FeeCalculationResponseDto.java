package com.company.hrms.payroll.infrastructure.client.insurance.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FeeCalculationResponseDto {
    private BigDecimal laborInsurance;
    private BigDecimal healthInsurance;
    private BigDecimal pensionSelfContribution;
    private BigDecimal employerLaborInsurance;
    private BigDecimal employerHealthInsurance;
    private BigDecimal employerPension;
}
