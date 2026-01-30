package com.company.hrms.notification.infrastructure.client.organization.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ContractDto {
    private String contractId;
    private String employeeId;
    private LocalDate expiryDate;
}
