package com.company.hrms.reporting.api.request;

import com.company.hrms.common.query.QueryCondition.EQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 薪資匯總報表請求
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Schema(description = "薪資匯總報表請求")
public class GetPayrollSummaryRequest {

    @EQ
    @Schema(description = "租戶ID (自動注入)", hidden = true)
    private String tenantId;

    @EQ("departmentId")
    @Schema(description = "部門ID")
    private String departmentId;

    @Schema(description = "薪資年月", example = "2024-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String yearMonth;

    @Schema(description = "頁碼", example = "0")
    private Integer page = 0;

    @Schema(description = "每頁筆數", example = "50")
    private Integer size = 50;
}
