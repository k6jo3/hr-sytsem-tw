package com.company.hrms.attendance.infrastructure.client.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private String departmentId;
    private String departmentName;
    private String organizationId;
    private String employmentStatus;
    private String jobTitle;
}
