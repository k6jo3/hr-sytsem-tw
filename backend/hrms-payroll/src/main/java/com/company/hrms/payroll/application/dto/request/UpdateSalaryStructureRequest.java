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
public class UpdateSalaryStructureRequest {
    private BigDecimal monthlySalary;
    private BigDecimal dailyRate;
    private BigDecimal hourlyRate;
    private String paymentMethod;
    private LocalDate effectiveDate;
    private LocalDate endDate;
    private List<SalaryItemRequest> items;
    private Boolean active;
}
