package com.company.hrms.attendance.api.request.report;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 月報表查詢請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "月報表查詢請求")
public class GetMonthlyReportRequest {

    @Schema(description = "組織ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @QueryFilter(operator = Operator.EQ)
    private String organizationId;

    @Schema(description = "年份", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer year;

    @Schema(description = "月份", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer month;

    @Schema(description = "部門ID")
    @QueryFilter(operator = Operator.EQ)
    private String departmentId;
}
