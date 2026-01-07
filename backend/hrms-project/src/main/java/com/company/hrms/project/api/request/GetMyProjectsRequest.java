package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢我的專案請求 (ESS)")
public class GetMyProjectsRequest {

    @Schema(description = "頁碼", example = "0")
    private Integer page = 0;

    @Schema(description = "每頁筆數", example = "10")
    private Integer size = 10;

    @Schema(description = "專案狀態過濾")
    private String status;
}
