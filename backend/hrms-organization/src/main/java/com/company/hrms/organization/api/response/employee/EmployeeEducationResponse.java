package com.company.hrms.organization.api.response.employee;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeEducationResponse {
    private String id;
    private String employeeId;
    private String schoolName;
    private String degree;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isHighest;
}
