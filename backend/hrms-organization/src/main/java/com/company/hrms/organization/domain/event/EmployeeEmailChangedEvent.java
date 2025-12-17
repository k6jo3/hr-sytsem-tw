package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.util.UUID;

/**
 * 員工 Email 變更事件
 * 觸發時機: Email變更
 * 訂閱服務: IAM
 */
@Getter
public class EmployeeEmailChangedEvent extends DomainEvent {

    private final UUID employeeId;
    private final String employeeNumber;
    private final String fullName;
    private final String oldEmail;
    private final String newEmail;

    public EmployeeEmailChangedEvent(UUID employeeId, String employeeNumber, String fullName,
                                      String oldEmail, String newEmail) {
        super();
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.oldEmail = oldEmail;
        this.newEmail = newEmail;
    }
}
