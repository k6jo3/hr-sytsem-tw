package com.company.hrms.timesheet.api.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectTimesheetRequest {
    @NotNull
    private UUID timesheetId;

    @NotNull
    private UUID rejectorId; // Usually the PM (Approver)

    @NotNull
    private String reason;
}
