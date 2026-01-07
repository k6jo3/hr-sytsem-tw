package com.company.hrms.timesheet.api.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class GetTimesheetSummaryRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID employeeId; // Optional filter
    private UUID departmentId; // Optional filter
}
