package com.company.hrms.notification.infrastructure.client.organization.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmployeeListResponseDto {
    private List<EmployeeDto> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
}
