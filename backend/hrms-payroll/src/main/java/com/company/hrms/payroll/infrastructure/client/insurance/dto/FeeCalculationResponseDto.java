package com.company.hrms.payroll.infrastructure.client.insurance.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FeeCalculationResponseDto {
    private Integer levelNumber;
    private BigDecimal monthlySalary;
    private BigDecimal laborEmployeeFee;
    private BigDecimal laborEmployerFee;
    private BigDecimal healthEmployeeFee;
    private BigDecimal healthEmployerFee;
    private BigDecimal pensionEmployerFee;
    private BigDecimal pensionSelfContribution;
    private BigDecimal totalEmployeeFee;
    private BigDecimal totalEmployerFee;

    // 為了向後相容，提供 alias getter
    public BigDecimal getLaborInsurance() {
        return laborEmployeeFee;
    }

    public BigDecimal getHealthInsurance() {
        return healthEmployeeFee;
    }

    public BigDecimal getEmployerLaborInsurance() {
        return laborEmployerFee;
    }

    public BigDecimal getEmployerHealthInsurance() {
        return healthEmployerFee;
    }

    public BigDecimal getEmployerPension() {
        return pensionEmployerFee;
    }
}
