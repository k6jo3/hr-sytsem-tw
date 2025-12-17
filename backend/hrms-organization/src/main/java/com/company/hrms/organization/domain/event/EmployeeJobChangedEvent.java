package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 員工職務異動事件
 * 觸發時機: 職務異動
 * 訂閱服務: Payroll
 */
@Getter
public class EmployeeJobChangedEvent extends DomainEvent {

    private final UUID employeeId;
    private final String employeeNumber;
    private final String fullName;
    private final String oldJobTitle;
    private final String newJobTitle;
    private final String oldJobLevel;
    private final String newJobLevel;
    private final LocalDate effectiveDate;
    private final String reason;

    public EmployeeJobChangedEvent(UUID employeeId, String employeeNumber, String fullName,
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
