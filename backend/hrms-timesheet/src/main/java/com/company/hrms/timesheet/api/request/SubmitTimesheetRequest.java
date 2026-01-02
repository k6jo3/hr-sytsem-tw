package com.company.hrms.timesheet.api.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitTimesheetRequest {
    @NotNull
    private UUID timesheetId;
}
