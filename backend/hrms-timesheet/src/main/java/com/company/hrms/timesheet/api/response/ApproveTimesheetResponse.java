package com.company.hrms.timesheet.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApproveTimesheetResponse {
    private UUID timesheetId;
    private TimesheetStatus status;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
}
