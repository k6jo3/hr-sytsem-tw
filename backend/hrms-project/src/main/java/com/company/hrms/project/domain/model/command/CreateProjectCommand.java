package com.company.hrms.project.domain.model.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateProjectCommand {
    private String projectCode;
    private String projectName;
    private String description;
    private UUID customerId;
    private ProjectType projectType;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private BudgetType budgetType;
    private BigDecimal budgetAmount;
    private BigDecimal budgetHours;
    private UUID projectManager;
    private List<MemberInfo> members;

    @Data
    @Builder
    public static class MemberInfo {
        private UUID employeeId;
        private String role;
        private BigDecimal allocatedHours;
    }
}
