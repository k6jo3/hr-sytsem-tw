package com.company.hrms.attendance.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class LeaveCancelledEvent extends DomainEvent {
    private final String applicationId;
    private final String cancelledBy;

    public LeaveCancelledEvent(String applicationId, String cancelledBy) {
        super();
        this.applicationId = applicationId;
        this.cancelledBy = cancelledBy;
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
