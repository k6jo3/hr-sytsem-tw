package com.company.hrms.training.infrastructure.client.organization.dto;

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
    private ManagerDto manager;
    private String departmentId;
    private String employmentStatus;
}
