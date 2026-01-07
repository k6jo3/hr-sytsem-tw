package com.company.hrms.timesheet.api.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateEntryResponse {
    private UUID timesheetId;
    private UUID entryId;
    private BigDecimal totalHours;
    private TimesheetStatus status;
}
