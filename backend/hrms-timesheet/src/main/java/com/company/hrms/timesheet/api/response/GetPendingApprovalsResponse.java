package com.company.hrms.timesheet.api.response;

import java.util.List;

import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPendingApprovalsResponse {
    private List<TimesheetSummaryDto> items;
    private long total;

    @Data
    @Builder
    public static class TimesheetSummaryDto {
        private String timesheetId;
        private String employeeId; // Manager needs to know whose timesheet it is
        private String periodStartDate;
        private String periodEndDate;
        private java.math.BigDecimal totalHours;
        private TimesheetStatus status;
        private String submittedAt;
    }
}
