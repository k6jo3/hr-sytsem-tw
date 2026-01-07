package com.company.hrms.project.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.company.hrms.project.domain.model.valueobject.ProjectStatus;
import com.company.hrms.project.domain.model.valueobject.ProjectType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectListItemResponse {
    private String projectId;
    private String projectCode;
    private String projectName;
    private ProjectType projectType;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalBudget;
    private UUID ownerId;
    private UUID customerId;
}
