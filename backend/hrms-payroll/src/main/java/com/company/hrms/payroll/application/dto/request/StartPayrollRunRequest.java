package com.company.hrms.payroll.application.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartPayrollRunRequest {
    private String payrollSystem; // MONTHLY, HOURLY
    private String organizationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
}
