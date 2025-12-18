package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部門持久化對象
 */
@Data
public class DepartmentPO {

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
}
