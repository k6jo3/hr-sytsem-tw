package com.company.hrms.project.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.company.hrms.project.domain.model.command.CreateTaskCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "建立任務請求")
public class CreateTaskRequest {

    @Schema(description = "專案ID")
    private UUID projectId;

    @Schema(description = "上層任務ID (可選)")
    private UUID parentTaskId;

    @Schema(description = "任務代碼", example = "T-001")
    private String taskCode;

    @Schema(description = "任務名稱", example = "需求分析")
    private String taskName;

    @Schema(description = "任務描述")
    private String description;

    @Schema(description = "預計開始日期")
    private LocalDate plannedStartDate;

    @Schema(description = "預計結束日期")
    private LocalDate plannedEndDate;

    @Schema(description = "預估工時")
    private BigDecimal estimatedHours;

    @Schema(description = "負責人ID (員工ID)")
    private UUID assigneeId;

    public CreateTaskCommand toCommand() {
        return CreateTaskCommand.builder()
                .taskCode(taskCode)
                .taskName(taskName)
                .description(description)
                .plannedStartDate(plannedStartDate)
                .plannedEndDate(plannedEndDate)
                .estimatedHours(estimatedHours)
                .assigneeId(assigneeId)
                .build();
    }
}
