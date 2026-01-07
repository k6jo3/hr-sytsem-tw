package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "查詢專案列表請求")
public class GetProjectListRequest {

    @Schema(description = "頁碼 (0-based)", defaultValue = "0")
    private int page = 0;

    @Schema(description = "每頁筆數", defaultValue = "20")
    private int size = 20;

    @Schema(description = "排序欄位", defaultValue = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "排序方向 (ASC, DESC)", defaultValue = "DESC")
    private String sortDirection = "DESC";

    @Schema(description = "關鍵字 (專案代碼或名稱)")
    private String keyword;

    @Schema(description = "專案狀態")
    private String status;

    @Schema(description = "負責人ID")
    private String ownerId;
}
