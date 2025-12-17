package com.company.hrms.organization.api.response.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 員工列表項目回應 DTO
 */
@Data
@Builder
public class EmployeeListItemResponse {

    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private String departmentPath;
    private String jobTitle;
    private String employmentStatus;
    private String employmentStatusDisplayName;
    private LocalDate hireDate;
    private String photoUrl;
}
