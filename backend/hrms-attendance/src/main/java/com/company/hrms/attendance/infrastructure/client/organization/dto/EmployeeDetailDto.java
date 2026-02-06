package com.company.hrms.attendance.infrastructure.client.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetailDto {
    private String employeeId;
    private String employeeNumber;
    private String fullName;
    private String firstName;
    private String lastName;
}
