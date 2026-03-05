package com.company.hrms.payroll.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryStructureResponse {
    private String id;
    private String employeeId;
    private String payrollSystem;
    private String payrollCycle;
    private String paymentMethod;
    private BigDecimal monthlySalary;
    private BigDecimal dailyRate;
    private BigDecimal hourlyRate;
    private BigDecimal calculatedHourlyRate;
    private LocalDate effectiveDate;
    private LocalDate endDate;
    private boolean active;
    private List<SalaryItemResponse> items;
}
