package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 組織持久化對象
 */
@Data
public class OrganizationPO {

    private UUID organizationId;
    private String organizationCode;
    private String organizationName;
    private String organizationType;
    private UUID parentOrganizationId;
    private String taxId;
    private String address;
    private String phoneNumber;
    private LocalDate establishedDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
