package com.company.hrms.project.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

/**
 * 客戶建立事件
 */
@Getter
public class CustomerCreatedEvent extends DomainEvent {
    private final String customerId;
    private final String customerCode;
    private final String customerName;

    public CustomerCreatedEvent(String customerId, String customerCode, String customerName) {
        super();
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
    }

    @Override
    public String getAggregateId() {
        return customerId;
    }

    @Override
    public String getAggregateType() {
        return "Customer";
    }
}
