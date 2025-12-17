package com.company.hrms.organization.api.request.department;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新部門請求 DTO
 */
@Data
@Schema(description = "更新部門請求")
public class UpdateDepartmentRequest {

    @Size(max = 100, message = "部門名稱長度不可超過100字元")
    @Schema(description = "部門名稱", example = "研發部")
    private String name;

    @Size(max = 100, message = "英文名稱長度不可超過100字元")
    @Schema(description = "部門英文名稱", example = "R&D Department")
    private String nameEn;

    @Size(max = 500, message = "說明長度不可超過500字元")
    @Schema(description = "部門說明")
    private String description;
}
