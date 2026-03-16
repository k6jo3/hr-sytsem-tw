package com.company.hrms.payroll.infrastructure.client.organization.dto;

import lombok.Data;

@Data
public class EmployeeSummaryDto {
    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private String departmentId;
    private String departmentName;
    private String status;

    // 向後相容 alias
    public String getId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeNumber;
    }
}
