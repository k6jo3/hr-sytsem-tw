package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 員工升遷事件
 * 觸發時機: 員工升遷
 * 訂閱服務: Payroll, Performance
 */
@Getter
public class EmployeePromotedEvent extends DomainEvent {

    private final UUID employeeId;
    private final String employeeNumber;
    private final String fullName;
    private final String oldJobTitle;
    private final String newJobTitle;
    private final String oldJobLevel;
    private final String newJobLevel;
    private final LocalDate effectiveDate;
    private final String reason;

    public EmployeePromotedEvent(UUID employeeId, String employeeNumber, String fullName,
                                  String oldJobTitle, String newJobTitle,
                                  String oldJobLevel, String newJobLevel,
                                  LocalDate effectiveDate, String reason) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.oldJobTitle = oldJobTitle;
        this.newJobTitle = newJobTitle;
        this.oldJobLevel = oldJobLevel;
        this.newJobLevel = newJobLevel;
        this.effectiveDate = effectiveDate;
        this.reason = reason;
    }
}
