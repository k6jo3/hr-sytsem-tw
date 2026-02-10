package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 部門持久化對象
 */
@Data
@Entity
@Table(name = "departments")
public class DepartmentPO {

    @Id
    @Column(name = "department_id")
    private String id;

    @Column(name = "department_code")
    private String code;

    @Column(name = "department_name")
    private String name;

    @Column(name = "department_name_en")
    private String nameEn;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "parent_department_id")
    private String parentId;

    private Integer level;
    private String path;
    private String managerId;
    private String status;

    @Column(name = "display_order")
    private Integer sortOrder;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted = false;
}
