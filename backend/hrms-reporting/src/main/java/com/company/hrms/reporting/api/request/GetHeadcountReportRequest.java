package com.company.hrms.reporting.api.request;

import com.company.hrms.common.query.QueryCondition.EQ;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 人力盤點報表請求
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Schema(description = "人力盤點報表請求")
public class GetHeadcountReportRequest {

    @EQ
    @Schema(description = "租戶ID (自動注入)", hidden = true)
    private String tenantId;

    @EQ("departmentId")
    @Schema(description = "部門ID")
    private String departmentId;

    @EQ("status")
    @Schema(description = "員工狀態", example = "ACTIVE")
    private String status;

    @Schema(description = "統計維度 (DEPARTMENT/POSITION/LEVEL)", example = "DEPARTMENT")
    private String dimension = "DEPARTMENT";

    @Schema(description = "頁碼", example = "0")
    private Integer page = 0;

    @Schema(description = "每頁筆數", example = "50")
    private Integer size = 50;
}
