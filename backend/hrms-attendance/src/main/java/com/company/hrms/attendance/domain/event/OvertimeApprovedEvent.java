package com.company.hrms.attendance.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

@Getter
public class OvertimeApprovedEvent extends DomainEvent {
    private final String applicationId;
    private final String approvedBy;

    public OvertimeApprovedEvent(String applicationId, String approvedBy) {
        super();
        this.applicationId = applicationId;
        this.approvedBy = approvedBy;
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
