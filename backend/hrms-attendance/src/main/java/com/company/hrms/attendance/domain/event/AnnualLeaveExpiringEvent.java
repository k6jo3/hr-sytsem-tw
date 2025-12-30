package com.company.hrms.attendance.domain.event;

import java.time.LocalDate;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class AnnualLeaveExpiringEvent extends DomainEvent {
    private final String employeeId;
    private final LocalDate expiryDate;
    private final java.math.BigDecimal remainingDays;

    public AnnualLeaveExpiringEvent(String employeeId, LocalDate expiryDate, java.math.BigDecimal remainingDays) {
        super();
        this.employeeId = employeeId;
        this.expiryDate = expiryDate;
        this.remainingDays = remainingDays;
    }

    @Override
    public String getAggregateId() {
        return employeeId;
    }

    @Override
    public String getAggregateType() {
        return "LeaveBalance";
    }
}
