package com.company.hrms.project.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTaskDetailResponse {
    private String taskId;
    private String projectId;
    private String taskName;
    private String description;
    private String parentTaskId;
    private String status;
    private Integer progress;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal estimatedHours;
    private UUID assigneeId;
}
