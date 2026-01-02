package com.company.hrms.timesheet.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LockTimesheetRequest {
    private String timesheetId;
    private String reason; // Optional reason for manual lock
}
