package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDateTime;

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
    private String id;
    private String code;
    private String name;
    private String nameEn;
    private String organizationId;
    private String parentId;
    private Integer level;
    private String path;
    private String managerId;
    private String status;
    private Integer sortOrder;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted = false;
    private String createdBy;
    private String updatedBy;
}
