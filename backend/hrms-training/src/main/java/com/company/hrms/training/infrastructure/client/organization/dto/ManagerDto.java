package com.company.hrms.training.infrastructure.client.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDto {
    private String employeeId;
    private String employeeNumber;
    private String fullName;
}
