package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.util.UUID;

/**
 * 部門主管異動事件
 * 觸發時機: 主管異動
 * 訂閱服務: Attendance
 */
@Getter
public class DepartmentManagerChangedEvent extends DomainEvent {

    private final UUID departmentId;
    private final String departmentCode;
    private final String departmentName;
    private final UUID oldManagerId;
    private final UUID newManagerId;

    public DepartmentManagerChangedEvent(UUID departmentId, String departmentCode, String departmentName,
                                          UUID oldManagerId, UUID newManagerId) {
        super();
        this.departmentId = departmentId;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.oldManagerId = oldManagerId;
        this.newManagerId = newManagerId;
    }
}
