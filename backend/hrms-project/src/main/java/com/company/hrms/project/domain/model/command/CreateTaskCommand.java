package com.company.hrms.project.domain.model.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTaskCommand {
    private String taskCode;
    private String taskName;
    private String description;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private BigDecimal estimatedHours;
    private UUID assigneeId;
}
