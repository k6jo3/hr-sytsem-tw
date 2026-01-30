package com.company.hrms.reporting.api.request;

import com.company.hrms.common.query.QueryCondition.EQ;
import com.company.hrms.common.query.QueryCondition.LIKE;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查詢排程報表列表請求")
public class GetScheduledReportsRequest {

    @EQ
    @Schema(description = "租戶ID (由系統自動填入)", hidden = true)
    private String tenantId;

    @LIKE("scheduleName")
    @Schema(description = "排程名稱關鍵字")
    private String keyword;

    @EQ
    @Schema(description = "排程報表ID")
    private String scheduleId;

    @EQ("isEnabled")
    @Schema(description = "是否啟用")
    private Boolean enabled;

    @Schema(description = "頁碼", defaultValue = "0")
    private int page = 0;

    @Schema(description = "每頁筆數", defaultValue = "20")
    private int size = 20;
}
