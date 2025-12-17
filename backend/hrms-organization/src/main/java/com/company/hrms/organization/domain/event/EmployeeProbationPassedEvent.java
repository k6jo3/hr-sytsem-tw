package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 員工試用期轉正事件
 * 觸發時機: 試用期轉正
 * 訂閱服務: Payroll
 */
@Getter
public class EmployeeProbationPassedEvent extends DomainEvent {

    private final UUID employeeId;
    private final String employeeNumber;
    private final String fullName;
    private final LocalDate effectiveDate;

    public EmployeeProbationPassedEvent(UUID employeeId, String employeeNumber,
                                         String fullName, LocalDate effectiveDate) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.effectiveDate = effectiveDate;
    }
}
