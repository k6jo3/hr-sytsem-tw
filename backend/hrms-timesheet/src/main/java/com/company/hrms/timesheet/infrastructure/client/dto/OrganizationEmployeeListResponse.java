package com.company.hrms.timesheet.infrastructure.client.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrganizationEmployeeListResponse {
    private List<OrganizationEmployeeDto> items;
    private long total;

    @Data
    public static class OrganizationEmployeeDto {
        private String employeeId;
        private String fullName;
        private String departmentId;
        private String departmentPath;
        private String status;
    }
}
