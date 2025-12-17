package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 員工離職事件 (系統關鍵事件)
 * 觸發時機: 員工離職
 * 訂閱服務: IAM, Attendance, Insurance, Payroll, Project
 */
@Getter
public class EmployeeTerminatedEvent extends DomainEvent {

    private final UUID employeeId;
    private final String employeeNumber;
    private final String fullName;
    private final String companyEmail;
    private final UUID organizationId;
    private final UUID departmentId;
    private final LocalDate terminationDate;
    private final String terminationReason;

    public EmployeeTerminatedEvent(UUID employeeId, String employeeNumber, String fullName,
                                    String companyEmail, UUID organizationId, UUID departmentId,
                                    LocalDate terminationDate, String terminationReason) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.companyEmail = companyEmail;
        this.organizationId = organizationId;
        this.departmentId = departmentId;
        this.terminationDate = terminationDate;
        this.terminationReason = terminationReason;
    }
}
