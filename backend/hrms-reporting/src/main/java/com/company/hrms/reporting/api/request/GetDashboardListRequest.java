package com.company.hrms.reporting.api.request;

import com.company.hrms.common.query.QueryCondition.EQ;
import com.company.hrms.common.query.QueryCondition.LIKE;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查詢儀表板列表請求
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Schema(description = "查詢儀表板列表請求")
public class GetDashboardListRequest {

    @EQ
    @Schema(description = "租戶ID (自動注入)", hidden = true)
    private String tenantId;

    @LIKE("dashboardName")
    @Schema(description = "儀表板名稱 (模糊查詢)", example = "高階")
    private String keyword;

    @EQ("isPublic")
    @Schema(description = "是否公開", example = "true")
    private Boolean isPublic;

    @EQ("ownerId")
    @Schema(description = "擁有者ID")
    private String ownerId;

    @Schema(description = "頁碼", example = "0")
    private Integer page = 0;

    @Schema(description = "每頁筆數", example = "20")
    private Integer size = 20;
}
