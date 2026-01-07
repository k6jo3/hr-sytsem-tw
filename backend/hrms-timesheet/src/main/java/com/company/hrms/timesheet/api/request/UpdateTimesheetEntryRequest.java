package com.company.hrms.timesheet.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class UpdateTimesheetEntryRequest {
    private UUID timesheetId; // From Path Variable
    private UUID entryId; // From Path Variable

    private UUID projectId;
    private UUID taskId;
    private LocalDate workDate;
    private BigDecimal hours;
    private String description;
}
