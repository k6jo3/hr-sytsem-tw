package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "查詢專案成本列表請求")
public class GetProjectCostListRequest {

    @Schema(description = "頁碼 (0-based)", defaultValue = "0")
    private int page = 0;

    @Schema(description = "每頁筆數", defaultValue = "20")
    private int size = 20;

    @Schema(description = "專案ID")
    private String projectId;

    @Schema(description = "成本類型")
    private String costType;

    @Schema(description = "年月 (yyyy-MM)")
    private String yearMonth;

    @Schema(description = "是否超預算")
    private Boolean isOverBudget;
}
