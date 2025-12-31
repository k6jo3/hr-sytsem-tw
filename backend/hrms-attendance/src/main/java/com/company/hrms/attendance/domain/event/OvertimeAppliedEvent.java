package com.company.hrms.attendance.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class OvertimeAppliedEvent extends DomainEvent {
    private final String applicationId;
    private final String employeeId;
    private final Double hours;

    public OvertimeAppliedEvent(String applicationId, String employeeId, Double hours) {
        super();
        this.applicationId = applicationId;
        this.employeeId = employeeId;
        this.hours = hours;
    }

    @Override
    public String getAggregateId() {
        return applicationId;
    }

    @Override
    public String getAggregateType() {
        return "OvertimeApplication";
    }
}
