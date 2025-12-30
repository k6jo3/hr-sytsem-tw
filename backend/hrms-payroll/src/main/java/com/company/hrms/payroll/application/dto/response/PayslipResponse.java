package com.company.hrms.payroll.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayslipResponse {
    private String id;
    private String payrollRunId;
    private String employeeId;
    private String employeeNumber;
    private String employeeName;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private LocalDate payDate;

    private BigDecimal baseSalary;
    private BigDecimal grossWage;
    private BigDecimal netWage;

    // Detailed breakdown could be separate or included
    private BigDecimal totalEarnings;
    private BigDecimal totalDeductions;
    private BigDecimal incomeTax;
    private BigDecimal insuranceDeductions;
    private BigDecimal leaveDeduction;
    private BigDecimal overtimePay;

    private String status;
    private String pdfUrl;
}
