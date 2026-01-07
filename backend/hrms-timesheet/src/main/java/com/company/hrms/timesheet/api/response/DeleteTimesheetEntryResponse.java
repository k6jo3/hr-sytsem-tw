package com.company.hrms.timesheet.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteTimesheetEntryResponse {
    private boolean success;
}
