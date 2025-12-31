package com.company.hrms.attendance.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class LeaveRejectedEvent extends DomainEvent {
    private final String applicationId;
    private final String rejectedBy;
    private final String reason;

    public LeaveRejectedEvent(String applicationId, String rejectedBy, String reason) {
        super();
        this.applicationId = applicationId;
        this.rejectedBy = rejectedBy;
        this.reason = reason;
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
