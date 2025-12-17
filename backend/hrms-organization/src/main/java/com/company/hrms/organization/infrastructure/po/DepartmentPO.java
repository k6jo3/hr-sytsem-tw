package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 部門持久化對象
 */
@Data
public class DepartmentPO {

    private UUID departmentId;
    private String departmentCode;
    private String departmentName;
    private UUID organizationId;
    private UUID parentDepartmentId;
    private Integer level;
    private UUID managerId;
    private Integer displayOrder;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
