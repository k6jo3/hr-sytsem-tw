package com.company.hrms.attendance.api.request.monthclose;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 執行月結請求 DTO
 */
@Data
@Schema(description = "執行月結請求")
public class ExecuteMonthCloseRequest {

    @NotBlank(message = "組織ID不可為空")
    @Schema(description = "組織ID")
    private String organizationId;

    @NotNull(message = "年份不可為空")
    @Min(value = 2000, message = "年份需大於等於2000")
    @Max(value = 2100, message = "年份需小於等於2100")
    @Schema(description = "年份", example = "2025")
    private Integer year;

    @NotNull(message = "月份不可為空")
    @Min(value = 1, message = "月份需大於等於1")
    @Max(value = 12, message = "月份需小於等於12")
    @Schema(description = "月份", example = "1")
    private Integer month;

    @Schema(description = "部門ID (選填，不填則處理全組織)")
    private String departmentId;

    @Schema(description = "是否強制重新結算", example = "false")
    private Boolean forceRecalculate;
}
