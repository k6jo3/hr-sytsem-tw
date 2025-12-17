package com.company.hrms.organization.api.request.department;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增部門請求 DTO
 */
@Data
@Schema(description = "新增部門請求")
public class CreateDepartmentRequest {

    @NotBlank(message = "組織ID為必填")
    @Schema(description = "所屬組織ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String organizationId;

    @NotBlank(message = "部門代碼為必填")
    @Size(max = 20, message = "部門代碼長度不可超過20字元")
    @Schema(description = "部門代碼", example = "DEPT001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "部門名稱為必填")
    @Size(max = 100, message = "部門名稱長度不可超過100字元")
    @Schema(description = "部門名稱", example = "研發部", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 100, message = "英文名稱長度不可超過100字元")
    @Schema(description = "部門英文名稱", example = "R&D Department")
    private String nameEn;

    @Schema(description = "父部門ID (建立子部門時必填)")
    private String parentId;

    @Schema(description = "部門主管員工ID")
    private String managerId;

    @Schema(description = "排序順序", example = "1")
    private Integer sortOrder;

    @Size(max = 500, message = "說明長度不可超過500字元")
    @Schema(description = "部門說明")
    private String description;
}
