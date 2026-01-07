package com.company.hrms.timesheet.infrastructure.client.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class ProjectDto {
    private UUID projectId;
    private String projectCode;
    private String projectName;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
