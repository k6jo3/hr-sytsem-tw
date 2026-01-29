package com.company.hrms.notification.infrastructure.client.organization.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private String email;
    private String status;
    private String departmentId;
}
