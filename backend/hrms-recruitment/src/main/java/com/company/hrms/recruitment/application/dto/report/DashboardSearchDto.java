package com.company.hrms.recruitment.application.dto.report;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 儀表板查詢條件
 */
@Data
@ParameterObject
@Schema(description = "儀表板查詢條件")
public class DashboardSearchDto {

    @Schema(description = "開始日期", example = "2025-12-01")
    private LocalDate dateFrom;

    @Schema(description = "結束日期", example = "2025-12-31")
    private LocalDate dateTo;

    @Schema(description = "部門 ID", example = "dept-001")
    private String departmentId;
}
