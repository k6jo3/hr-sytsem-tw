package com.company.hrms.reporting.api.request;

import java.time.LocalDate;

import com.company.hrms.common.query.QueryCondition.EQ;
import com.company.hrms.common.query.QueryCondition.GTE;
import com.company.hrms.common.query.QueryCondition.LTE;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 專案成本分析請求
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Schema(description = "專案成本分析請求")
public class GetProjectCostAnalysisRequest {

    @EQ
    @Schema(description = "租戶ID (自動注入)", hidden = true)
    private String tenantId;

    @EQ("projectId")
    @Schema(description = "專案ID")
    private String projectId;

    @EQ("customerId")
    @Schema(description = "客戶ID")
    private String customerId;

    @GTE("date")
    @Schema(description = "分析期間起", example = "2024-01-01")
    private LocalDate startDate;

    @LTE("date")
    @Schema(description = "分析期間迄", example = "2024-12-31")
    private LocalDate endDate;

    @Schema(description = "頁碼", example = "0")
    private Integer page = 0;

    @Schema(description = "每頁筆數", example = "20")
    private Integer size = 20;
}
