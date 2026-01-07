package com.company.hrms.timesheet.api.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class GetUnreportedEmployeesRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
