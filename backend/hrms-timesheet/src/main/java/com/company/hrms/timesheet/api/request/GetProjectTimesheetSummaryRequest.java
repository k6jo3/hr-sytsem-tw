package com.company.hrms.timesheet.api.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class GetProjectTimesheetSummaryRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID projectId; // Optional filter
}
