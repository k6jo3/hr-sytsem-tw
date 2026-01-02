package com.company.hrms.timesheet.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimesheetLockedEvent extends DomainEvent {
    // This event might be per timesheet or per month.
    // Spec says "TimesheetLocked" event payload has month and stats.
    // The aggregate is likely the Service or a "MonthLock" aggregate?
    // Or it's a domain event emitted by Timesheet Service when a batch lock
    // happens.
    // However, DomainEvent usually ties to an Aggregate.
    // If we lock individual Timesheets, each emits an event?
    // Spec Payload:
    // { "month": "2025-11", "totalTimesheets": 150... }
    // This looks like a Service-level event or a "PeriodLock" aggregate event.
    // But for now, let's assume it's a general event.
    // AggregateId? Maybe the Month? "2025-11"?

    private final String month;
    private final int totalTimesheets;
    private final int lockedTimesheets;

    @Override
    public String getAggregateId() {
        return month;
    }

    @Override
    public String getAggregateType() {
        return "TimesheetPeriod"; // Virtual aggregate
    }
}
