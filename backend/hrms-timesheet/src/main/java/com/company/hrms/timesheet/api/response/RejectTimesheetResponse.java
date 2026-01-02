package com.company.hrms.timesheet.api.response;

import java.util.UUID;

import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectTimesheetResponse {
    private UUID timesheetId;
    private TimesheetStatus status;
    private String reason;
}
