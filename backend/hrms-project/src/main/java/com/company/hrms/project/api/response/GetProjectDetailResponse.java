package com.company.hrms.project.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.company.hrms.project.domain.model.valueobject.BudgetType;
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
public class GetProjectDetailResponse {
    private String projectId;
    private String projectCode;
    private String projectName;
    private ProjectType projectType;
    private ProjectStatus status;
    private String description;

    // Schedule
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;

    // Budget
    private BudgetType budgetType;
    private BigDecimal budgetAmount;
    private BigDecimal budgetHours;

    // Performance
    private BigDecimal actualHours;
    private BigDecimal actualCost;

    // Relations
    private UUID customerId;
    private List<ProjectMemberDto> members;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long version;
}
