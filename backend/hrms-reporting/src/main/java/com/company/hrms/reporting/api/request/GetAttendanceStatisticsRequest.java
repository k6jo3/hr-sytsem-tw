package com.company.hrms.reporting.api.request;

import java.time.LocalDate;

import com.company.hrms.common.query.QueryCondition.EQ;
import com.company.hrms.common.query.QueryCondition.GTE;
import com.company.hrms.common.query.QueryCondition.LTE;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 差勤統計報表請求
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Schema(description = "差勤統計報表請求")
public class GetAttendanceStatisticsRequest {

    @EQ
    @Schema(description = "租戶ID (自動注入)", hidden = true)
    private String tenantId;

    @EQ("departmentId")
    @Schema(description = "部門ID")
    private String departmentId;

    @GTE("date")
    @Schema(description = "統計期間起", example = "2024-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @LTE("date")
    @Schema(description = "統計期間迄", example = "2024-01-31", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;

    @Schema(description = "頁碼", example = "0")
    private Integer page = 0;

    @Schema(description = "每頁筆數", example = "50")
    private Integer size = 50;
}
