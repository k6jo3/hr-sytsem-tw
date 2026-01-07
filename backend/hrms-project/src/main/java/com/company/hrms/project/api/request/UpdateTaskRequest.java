package com.company.hrms.project.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.project.domain.model.command.UpdateTaskCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "更新工項請求")
public class UpdateTaskRequest {

    @Schema(description = "工項ID", hidden = true)
    private String taskId;

    @Schema(description = "工項名稱")
    private String taskName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "預計開始日期")
    private LocalDate plannedStartDate;

    @Schema(description = "預計結束日期")
    private LocalDate plannedEndDate;

    @Schema(description = "預計工時")
    private BigDecimal estimatedHours;

    public UpdateTaskCommand toCommand() {
        return UpdateTaskCommand.builder()
                .taskName(taskName)
                .description(description)
                .plannedStartDate(plannedStartDate)
                .plannedEndDate(plannedEndDate)
                .estimatedHours(estimatedHours)
                .build();
    }
}
