package com.company.hrms.project.domain.model.command;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProjectCommand {
    private String projectName;
    private ProjectType projectType;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private String description;
    private BudgetType budgetType;
    private BigDecimal budgetAmount;
    private BigDecimal budgetHours;
}
