package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 員工調薪事件
 * 觸發時機: 調薪
 * 訂閱服務: Payroll, Insurance
 */
@Getter
public class EmployeeSalaryChangedEvent extends DomainEvent {

    private final UUID employeeId;
    private final String employeeNumber;
    private final String fullName;
    private final LocalDate effectiveDate;
    private final String reason;

    public EmployeeSalaryChangedEvent(UUID employeeId, String employeeNumber, String fullName,
                                       LocalDate effectiveDate, String reason) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.effectiveDate = effectiveDate;
        this.reason = reason;
    }
}
