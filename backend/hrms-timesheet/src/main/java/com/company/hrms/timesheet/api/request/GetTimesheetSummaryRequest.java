package com.company.hrms.timesheet.api.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class GetTimesheetSummaryRequest {
    @com.company.hrms.common.query.QueryFilter(property = "periodStartDate", operator = com.company.hrms.common.query.Operator.GTE)
    private LocalDate startDate;

    @com.company.hrms.common.query.QueryFilter(property = "periodEndDate", operator = com.company.hrms.common.query.Operator.LTE)
    private LocalDate endDate;

    @com.company.hrms.common.query.QueryFilter(property = "employeeId", operator = com.company.hrms.common.query.Operator.EQ)
    private UUID employeeId; // Optional filter

    // @QueryFilter annotation for departmentId is omitted as it might require
    // checking if Timesheet entity has department info directly or requires join.
    // Assuming departmentId might be handled separately or not supported in simple
    // query yet based on original implementation which only handled
    // start/end/employee.
    // However, original impl didn't use departmentId.
    private UUID departmentId; // Optional filter
}
