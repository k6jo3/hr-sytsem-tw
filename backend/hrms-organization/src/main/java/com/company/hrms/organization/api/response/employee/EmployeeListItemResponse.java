package com.company.hrms.organization.api.response.employee;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

/**
 * 員工列表項目回應 DTO
 */
@Data
@Builder
public class EmployeeListItemResponse {

    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private String departmentId; // New
    private String departmentPath;
    private String positionId; // New
    private String email; // New
    private String status; // New
    private String statusDisplay; // New
    private String jobTitle;
    private String employmentStatus;
    private String employmentStatusDisplayName;
    private LocalDate hireDate;
    private String photoUrl;
}
