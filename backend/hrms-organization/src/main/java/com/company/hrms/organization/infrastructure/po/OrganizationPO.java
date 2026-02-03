package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 組織持久化對象
 */
@Data
@Entity
@Table(name = "organizations")
public class OrganizationPO {

    @Id
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
    private Boolean isDeleted = false;
    private String createdBy;
    private String updatedBy;
}
