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
}
