package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 組織持久化對象
 */
@Data
public class OrganizationPO {

    private String id;
    private String code;
    private String name;
    private String nameEn;
    private String type;
    private String status;
    private String parentId;
    private String taxId;
    private String phone;
    private String fax;
    private String email;
    private String address;
    private LocalDate establishedDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
