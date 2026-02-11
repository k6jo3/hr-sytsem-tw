package com.company.hrms.payroll.infrastructure.client.organization.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmployeeListResponseDto {
    private List<EmployeeSummaryDto> items;
    private long total;
}
