package com.company.hrms.iam.api.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得角色列表請求 (支援分頁與過濾)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取得角色列表請求")
public class GetRoleListRequest {

    @Schema(description = "角色名稱 (模糊查詢)")
    private String name;

    @Schema(description = "角色狀態 (ACTIVE/INACTIVE)")
    private String status;

    @Schema(description = "角色類型 (SYSTEM/CUSTOM)")
    private String type;

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
