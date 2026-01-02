package com.company.hrms.project.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.company.hrms.project.domain.model.command.CreateProjectCommand;
import com.company.hrms.project.domain.model.valueobject.BudgetType;
import com.company.hrms.project.domain.model.valueobject.ProjectType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "建立專案請求")
public class CreateProjectRequest {

    @Schema(description = "專案代碼", example = "PRJ-2025-001")
    private String projectCode;

    @Schema(description = "專案名稱", example = "HR系統二期開發")
    private String projectName;

    @Schema(description = "專案描述", example = "企業級人力資源管理系統")
    private String description;

    @Schema(description = "客戶ID")
    private UUID customerId;

    @Schema(description = "專案類型", example = "FIXED_PRICE")
    private ProjectType projectType;

    @Schema(description = "預計開始日期")
    private LocalDate plannedStartDate;

    @Schema(description = "預計結束日期")
    private LocalDate plannedEndDate;

    @Schema(description = "預算類型", example = "FIXED_AMOUNT")
    private BudgetType budgetType;

    @Schema(description = "預算金額")
    private BigDecimal budgetAmount;

    @Schema(description = "預算工時")
    private BigDecimal budgetHours;

    @Schema(description = "專案經理ID")
    private UUID projectManager;

    @Schema(description = "專案成員列表")
    private List<MemberRequest> members;

    @Data
    @NoArgsConstructor
    public static class MemberRequest {
        @Schema(description = "員工ID")
        private UUID employeeId;

        @Schema(description = "角色", example = "Developer")
        private String role;

        @Schema(description = "分配工時")
        private BigDecimal allocatedHours;
    }

    public CreateProjectCommand toCommand() {
        List<CreateProjectCommand.MemberInfo> memberInfos = null;
        if (members != null) {
            memberInfos = members.stream()
                    .map(m -> CreateProjectCommand.MemberInfo.builder()
                            .employeeId(m.getEmployeeId())
                            .role(m.getRole())
                            .allocatedHours(m.getAllocatedHours())
                            .build())
                    .collect(Collectors.toList());
        }

        return CreateProjectCommand.builder()
                .projectCode(projectCode)
                .projectName(projectName)
                .description(description)
                .customerId(customerId)
                .projectType(projectType)
                .plannedStartDate(plannedStartDate)
                .plannedEndDate(plannedEndDate)
                .budgetType(budgetType)
                .budgetAmount(budgetAmount)
                .budgetHours(budgetHours)
                .projectManager(projectManager)
                .members(memberInfos)
                .build();
    }
}
