package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
    @Column(name = "organization_id")
    private String id;

    @Column(name = "organization_code")
    private String code;

    @Column(name = "organization_name")
    private String name;

    @Column(name = "organization_name_en")
    private String nameEn;

    @Column(name = "organization_type")
    private String type;

    private String status;

    @Column(name = "parent_organization_id")
    private String parentId;

    private String taxId;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "fax_number")
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
