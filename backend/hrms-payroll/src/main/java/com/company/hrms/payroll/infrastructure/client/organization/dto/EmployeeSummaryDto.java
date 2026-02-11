package com.company.hrms.payroll.infrastructure.client.organization.dto;

import lombok.Data;

@Data
public class EmployeeSummaryDto {
    private String id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String departmentName;
    private String status;
}
