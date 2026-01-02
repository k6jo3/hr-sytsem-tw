package com.company.hrms.timesheet.api.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTimesheetDetailResponse {
    private UUID timesheetId;
    private UUID employeeId;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private java.math.BigDecimal totalHours;
    private TimesheetStatus status;
    private LocalDateTime submittedAt;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private List<TimesheetEntryDto> entries;

    @Data
    @Builder
    public static class TimesheetEntryDto {
        private UUID entryId;
        private LocalDate workDate;
        private java.math.BigDecimal hours;
        private UUID projectId; // Or Code/Name if we enrich it
        private String description;
        private String location;
    }
}
