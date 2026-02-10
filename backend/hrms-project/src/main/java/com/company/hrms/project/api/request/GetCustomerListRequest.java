package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢客戶列表請求")
public class GetCustomerListRequest {

    @Schema(description = "頁碼 (0-based)")
    private int page = 0;

    @Schema(description = "每頁筆數")
    private int size = 20;

    @Schema(description = "關鍵字 (名稱/代碼/統編)")
    private String keyword;

    @Schema(description = "客戶狀態")
    private String status;

    @Schema(description = "產業類型")
    private String industry;

    @Schema(description = "是否有專案")
    private Boolean hasProjects;

    @Schema(description = "負責業務ID")
    private String salesRepId;
}
