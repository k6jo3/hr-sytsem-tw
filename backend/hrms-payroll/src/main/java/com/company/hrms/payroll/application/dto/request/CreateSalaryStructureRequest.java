package com.company.hrms.payroll.application.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSalaryStructureRequest {
    private String employeeId;
    private BigDecimal monthlySalary;
    private BigDecimal dailyRate;
    private BigDecimal hourlyRate;
    private String payrollSystem;
    private String payrollCycle;
    private String paymentMethod;
    private LocalDate effectiveDate;
    private List<SalaryItemRequest> items;
}
