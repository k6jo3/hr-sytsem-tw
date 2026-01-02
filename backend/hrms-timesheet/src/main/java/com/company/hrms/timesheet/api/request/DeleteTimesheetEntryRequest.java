package com.company.hrms.timesheet.api.request;

import java.util.UUID;

import lombok.Data;

@Data
public class DeleteTimesheetEntryRequest {
    private UUID timesheetId; // From path
    private UUID entryId; // From path
}
