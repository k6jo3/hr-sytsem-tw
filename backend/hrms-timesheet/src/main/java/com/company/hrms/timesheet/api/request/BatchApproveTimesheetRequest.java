package com.company.hrms.timesheet.api.request;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class BatchApproveTimesheetRequest {
    private List<UUID> timesheetIds;
}
