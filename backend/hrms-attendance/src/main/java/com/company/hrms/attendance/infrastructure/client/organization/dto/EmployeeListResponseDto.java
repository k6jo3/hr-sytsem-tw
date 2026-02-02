package com.company.hrms.attendance.infrastructure.client.organization.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeListResponseDto {
    private List<EmployeeDto> data;
    private long total;
}
