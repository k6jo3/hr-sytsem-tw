package com.company.hrms.timesheet.api.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class GetProjectTimesheetSummaryRequest {
    @com.company.hrms.common.query.QueryFilter(property = "periodStartDate", operator = com.company.hrms.common.query.Operator.GTE)
    private LocalDate startDate;

    @com.company.hrms.common.query.QueryFilter(property = "periodEndDate", operator = com.company.hrms.common.query.Operator.LTE)
    private LocalDate endDate;

    @com.company.hrms.common.query.QueryFilter(property = "entries.projectId", operator = com.company.hrms.common.query.Operator.EQ)
    private UUID projectId; // Optional filter
}
