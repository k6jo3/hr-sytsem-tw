package com.company.hrms.timesheet.api.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetProjectTimesheetSummaryResponse {
    private List<ProjectSummary> projects;

    @Data
    @Builder
    public static class ProjectSummary {
        private UUID projectId;
        private String projectName;
        private BigDecimal totalHours;
        private int employeeCount;
    }
}
