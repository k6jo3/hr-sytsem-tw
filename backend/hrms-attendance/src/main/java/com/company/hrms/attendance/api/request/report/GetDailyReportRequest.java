package com.company.hrms.attendance.api.request.report;

import java.time.LocalDate;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日報表查詢請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "日報表查詢請求")
public class GetDailyReportRequest {

    @Schema(description = "組織ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @QueryFilter(operator = Operator.EQ)
    private String organizationId;

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @QueryFilter(operator = Operator.EQ, property = "date")
    private LocalDate date;

    @Schema(description = "部門ID")
    @QueryFilter(operator = Operator.EQ)
    private String departmentId;
}
