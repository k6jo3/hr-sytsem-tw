package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 員工部門異動事件
 * 觸發時機: 部門調動
 * 訂閱服務: Attendance, Payroll
 */
@Getter
public class EmployeeDepartmentChangedEvent extends DomainEvent {

    private final UUID employeeId;
    private final String employeeNumber;
    private final String fullName;
    private final UUID oldDepartmentId;
    private final UUID newDepartmentId;
    private final UUID oldManagerId;
    private final UUID newManagerId;
    private final LocalDate effectiveDate;
    private final String reason;

    public EmployeeDepartmentChangedEvent(UUID employeeId, String employeeNumber, String fullName,
                                           UUID oldDepartmentId, UUID newDepartmentId,
                                           UUID oldManagerId, UUID newManagerId,
                                           LocalDate effectiveDate, String reason) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.oldDepartmentId = oldDepartmentId;
        this.newDepartmentId = newDepartmentId;
        this.oldManagerId = oldManagerId;
        this.newManagerId = newManagerId;
        this.effectiveDate = effectiveDate;
        this.reason = reason;
    }
}
