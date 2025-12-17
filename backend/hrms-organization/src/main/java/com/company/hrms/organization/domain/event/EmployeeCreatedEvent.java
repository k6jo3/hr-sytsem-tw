package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 員工建立事件
 * 觸發時機: 新員工到職
 * 訂閱服務: IAM, Insurance, Payroll
 */
@Getter
public class EmployeeCreatedEvent extends DomainEvent {

    private final UUID employeeId;
    private final String employeeNumber;
    private final String fullName;
    private final String companyEmail;
    private final UUID organizationId;
    private final UUID departmentId;
    private final String jobTitle;
    private final LocalDate hireDate;

    public EmployeeCreatedEvent(UUID employeeId, String employeeNumber, String fullName,
                                 String companyEmail, UUID organizationId, UUID departmentId,
                                 String jobTitle, LocalDate hireDate) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.companyEmail = companyEmail;
        this.organizationId = organizationId;
        this.departmentId = departmentId;
        this.jobTitle = jobTitle;
        this.hireDate = hireDate;
    }
}
