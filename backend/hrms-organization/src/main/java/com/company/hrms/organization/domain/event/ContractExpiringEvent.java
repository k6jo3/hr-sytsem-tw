package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 合約即將到期事件
 * 觸發時機: 合約即將到期 (30天內)
 * 訂閱服務: Notification
 */
@Getter
public class ContractExpiringEvent extends DomainEvent {

    private final UUID contractId;
    private final String contractNumber;
    private final UUID employeeId;
    private final String employeeName;
    private final LocalDate endDate;
    private final long daysRemaining;

    public ContractExpiringEvent(UUID contractId, String contractNumber, UUID employeeId,
                                  String employeeName, LocalDate endDate, long daysRemaining) {
        super();
        this.contractId = contractId;
        this.contractNumber = contractNumber;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.endDate = endDate;
        this.daysRemaining = daysRemaining;
    }
}
