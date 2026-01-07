package com.company.hrms.timesheet.api.request;

import java.time.LocalDate;
import java.util.UUID;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GetMyTimesheetRequest extends PageRequest {
    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private UUID employeeId;

    @QueryFilter(property = "periodStartDate", operator = Operator.GTE)
    private LocalDate startDate;

    @QueryFilter(property = "periodEndDate", operator = Operator.LTE)
    private LocalDate endDate;
}
