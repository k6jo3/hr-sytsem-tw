package com.company.hrms.timesheet.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimesheetApprovedEvent extends DomainEvent {
    private final UUID timesheetId;
    private final UUID employeeId;
    private final UUID approverId;
    private final LocalDateTime approvedAt;

    @Override
    public String getAggregateId() {
        return timesheetId.toString();
    }

    @Override
    public String getAggregateType() {
        return "Timesheet";
    }
}
