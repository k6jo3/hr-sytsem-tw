package com.company.hrms.attendance.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class AttendanceMonthClosedEvent extends DomainEvent {
    private final String month; // YYYY-MM
    private final String operatorId;

    public AttendanceMonthClosedEvent(String month, String operatorId) {
        super();
        this.month = month;
        this.operatorId = operatorId;
    }

    @Override
    public String getAggregateId() {
        return month;
    }

    @Override
    public String getAggregateType() {
        return "AttendanceReport";
    }
}
