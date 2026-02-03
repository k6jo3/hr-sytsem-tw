package com.company.hrms.organization.api.response.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeExperienceResponse {
    private String id;
    private String employeeId;
    private String companyName;
    private String jobTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal salary;
    private String reasonForLeaving;
}
