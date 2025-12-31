package com.company.hrms.attendance.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class OvertimeLimitExceededEvent extends DomainEvent {
    private final String employeeId;
    private final Double requestedHours;
    private final Double currentTotalHours;
    private final Double limitHours;

    public OvertimeLimitExceededEvent(String employeeId, Double requestedHours, Double currentTotalHours,
            Double limitHours) {
        super();
        this.employeeId = employeeId;
        this.requestedHours = requestedHours;
        this.currentTotalHours = currentTotalHours;
        this.limitHours = limitHours;
    }

    @Override
    public String getAggregateId() {
        return employeeId;
    }

    @Override
    public String getAggregateType() {
        return "Employee"; // Or OvertimeApplication, but limit is per employee
    }
}
