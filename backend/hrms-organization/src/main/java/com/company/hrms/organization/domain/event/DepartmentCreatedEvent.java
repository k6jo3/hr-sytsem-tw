package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.util.UUID;

/**
 * 部門建立事件
 * 觸發時機: 新增部門
 */
@Getter
public class DepartmentCreatedEvent extends DomainEvent {

    private final UUID departmentId;
    private final String departmentCode;
    private final String departmentName;
    private final UUID organizationId;
    private final UUID parentDepartmentId;
    private final Integer level;

    public DepartmentCreatedEvent(UUID departmentId, String departmentCode, String departmentName,
                                   UUID organizationId, UUID parentDepartmentId, Integer level) {
        super();
        this.departmentId = departmentId;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.organizationId = organizationId;
        this.parentDepartmentId = parentDepartmentId;
        this.level = level;
    }
}
