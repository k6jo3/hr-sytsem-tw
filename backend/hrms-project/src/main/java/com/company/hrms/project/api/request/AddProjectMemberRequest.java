package com.company.hrms.project.api.request;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "新增專案成員請求")
public class AddProjectMemberRequest {

    @Schema(description = "專案ID", hidden = true)
    private String projectId; // Usually set from path variable by controller

    @Schema(description = "員工ID")
    private UUID employeeId;

    @Schema(description = "角色", example = "Developer")
    private String role;

    @Schema(description = "分配工時")
    private BigDecimal allocatedHours;

    @Schema(description = "時薪費率")
    private BigDecimal hourlyRate;
}
