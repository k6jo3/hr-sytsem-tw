package com.company.hrms.timesheet.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimesheetSubmittedEvent extends DomainEvent {
    private final UUID timesheetId;
    private final UUID employeeId;
    private final BigDecimal totalHours;
    private final LocalDate periodStartDate;
    private final LocalDate periodEndDate;

    @Override
    public String getAggregateId() {
        return timesheetId.toString();
    }

    @Override
    public String getAggregateType() {
        return "Timesheet";
    }
}
