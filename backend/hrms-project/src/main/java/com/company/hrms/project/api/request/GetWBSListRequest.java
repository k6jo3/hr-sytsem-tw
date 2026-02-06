package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "查詢 WBS 列表請求")
public class GetWBSListRequest {

    @Schema(description = "頁碼 (0-based)", defaultValue = "0")
    private int page = 0;

    @Schema(description = "每頁筆數", defaultValue = "20")
    private int size = 20;

    @Schema(description = "專案ID")
    private String projectId;

    @Schema(description = "父工作包ID (空字串表示查詢頂層)")
    private String parentId;

    @Schema(description = "是否查詢頂層 (parent_id IS NULL)")
    private Boolean isTopLevel;

    @Schema(description = "工作包狀態")
    private String status;

    @Schema(description = "是否延遲")
    private Boolean isDelayed;

    @Schema(description = "負責人ID")
    private String ownerId;
}
