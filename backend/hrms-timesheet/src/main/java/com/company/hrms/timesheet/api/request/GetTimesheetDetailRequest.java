package com.company.hrms.timesheet.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GetTimesheetDetailRequest {
    private String timesheetId;
}
