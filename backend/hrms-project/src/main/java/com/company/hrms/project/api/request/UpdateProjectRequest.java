package com.company.hrms.project.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.project.domain.model.command.UpdateProjectCommand;
import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "更新專案請求")
public class UpdateProjectRequest {

    @Schema(description = "專案ID", hidden = true)
    private String projectId;

    @Schema(description = "專案名稱")
    private String projectName;

    @Schema(description = "專案描述")
    private String description;

    @Schema(description = "專案類型")
    private ProjectType projectType;

    @Schema(description = "預計開始日期")
    private LocalDate plannedStartDate;

    @Schema(description = "預計結束日期")
    private LocalDate plannedEndDate;

    @Schema(description = "預算類型")
    private BudgetType budgetType;

    @Schema(description = "預算金額")
    private BigDecimal budgetAmount;

    @Schema(description = "預算工時")
    private BigDecimal budgetHours;

    public UpdateProjectCommand toCommand() {
        return UpdateProjectCommand.builder()
                .projectName(projectName)
                .description(description)
                .projectType(projectType)
                .plannedStartDate(plannedStartDate)
                .plannedEndDate(plannedEndDate)
                .budgetType(budgetType)
                .budgetAmount(budgetAmount)
                .budgetHours(budgetHours)
                .build();
    }
}
