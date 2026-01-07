package com.company.hrms.timesheet.domain.event;

import java.util.UUID;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimesheetRejectedEvent extends DomainEvent {
    private final UUID timesheetId;
    private final UUID employeeId;
    private final String reason;

    @Override
    public String getAggregateId() {
        return timesheetId.toString();
    }

    @Override
    public String getAggregateType() {
        return "Timesheet";
    }
}
