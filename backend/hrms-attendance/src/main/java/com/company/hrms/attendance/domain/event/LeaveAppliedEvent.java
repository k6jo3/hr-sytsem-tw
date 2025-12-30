package com.company.hrms.attendance.domain.event;

import java.math.BigDecimal;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class LeaveAppliedEvent extends DomainEvent {
    private final String applicationId;
    private final String employeeId;
    private final String leaveTypeId;
    private final BigDecimal totalDays;

    public LeaveAppliedEvent(String applicationId, String employeeId, String leaveTypeId, BigDecimal totalDays) {
        super();
        this.applicationId = applicationId;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.totalDays = totalDays;
    }

    @Override
    public String getAggregateId() {
        return applicationId;
    }

    @Override
    public String getAggregateType() {
        return "LeaveApplication";
    }
}
