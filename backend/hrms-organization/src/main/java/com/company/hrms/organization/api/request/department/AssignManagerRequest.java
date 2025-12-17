package com.company.hrms.organization.api.request.department;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 指派部門主管請求 DTO
 */
@Data
@Schema(description = "指派部門主管請求")
public class AssignManagerRequest {

    @NotBlank(message = "主管員工ID為必填")
    @Schema(description = "主管員工ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String managerId;
}
