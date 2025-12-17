package com.company.hrms.organization.api.request.department;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 調整部門順序請求 DTO
 */
@Data
@Schema(description = "調整部門順序請求")
public class ReorderDepartmentRequest {

    @NotNull(message = "排序順序為必填")
    @Schema(description = "排序順序", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sortOrder;
}
