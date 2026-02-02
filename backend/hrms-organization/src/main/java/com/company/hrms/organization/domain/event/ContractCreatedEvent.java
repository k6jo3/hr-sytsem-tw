package com.company.hrms.organization.domain.event;

import java.time.LocalDate;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

/**
 * 合約建立事件
 */
@Getter
public class ContractCreatedEvent extends DomainEvent {

    private final String contractId;
    private final String employeeId;
    private final String contractType;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public ContractCreatedEvent(String contractId, String employeeId, String contractType, LocalDate startDate,
            LocalDate endDate) {
        super();
        this.contractId = contractId;
        this.employeeId = employeeId;
        this.contractType = contractType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String getAggregateId() {
        return contractId;
    }

    @Override
    public String getAggregateType() {
        return "EmployeeContract";
    }
}
