package com.company.hrms.iam.api.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得使用者列表請求 (支援分頁與過濾)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取得使用者列表請求")
public class GetUserListRequest {

    @Schema(description = "關鍵字 (使用者名稱/顯示名稱)")
    private String keyword;

    @Schema(description = "使用者名稱 (模糊查詢)")
    private String username;

    @Schema(description = "帳號狀態 (ACTIVE/LOCKED etc)")
    private String status;

    @Schema(description = "角色 ID")
    private String roleId;

    @Schema(description = "租戶 ID (僅 SUPER_ADMIN 可用)")
    private String tenantId;

    @Schema(description = "頁碼 (預設 1)")
    @Builder.Default
    private int page = 1;

    @Schema(description = "每頁筆數 (預設 20)")
    @Builder.Default
    private int size = 20;

    @Schema(description = "排序欄位")
    private String sort;
}
