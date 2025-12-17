package com.company.hrms.organization.domain.event;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 合約續約事件
 * 觸發時機: 合約續約
 */
@Getter
public class ContractRenewedEvent extends DomainEvent {

    private final UUID contractId;
    private final String contractNumber;
    private final UUID employeeId;
    private final String employeeName;
    private final LocalDate oldEndDate;
    private final LocalDate newEndDate;

    public ContractRenewedEvent(UUID contractId, String contractNumber, UUID employeeId,
                                 String employeeName, LocalDate oldEndDate, LocalDate newEndDate) {
        super();
        this.contractId = contractId;
        this.contractNumber = contractNumber;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.oldEndDate = oldEndDate;
        this.newEndDate = newEndDate;
    }
}
