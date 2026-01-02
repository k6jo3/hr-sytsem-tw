package com.company.hrms.timesheet.api.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTimesheetSummaryResponse {
    private BigDecimal totalHours;
    private BigDecimal projectHours;
    private BigDecimal averageDailyHours;
    private int unreportedCount;
}
