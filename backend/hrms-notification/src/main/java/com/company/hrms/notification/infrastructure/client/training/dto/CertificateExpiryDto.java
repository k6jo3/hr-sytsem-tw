package com.company.hrms.notification.infrastructure.client.training.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CertificateExpiryDto {
    private String certificateId;
    private String employeeId;
    private String certificateName;
    private LocalDate expiryDate;
}
