package com.company.hrms.iam.api.request.permission;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得權限列表請求 (支援分頁與過濾)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取得權限列表請求")
public class GetPermissionListRequest {

    @Schema(description = "模組名稱 (EMPLOYEE/ATTENDANCE/PAYROLL etc)")
    @QueryFilter(property = "module", operator = Operator.EQ)
    private String module;

    @Schema(description = "權限類型 (MENU/BUTTON/API)")
    @QueryFilter(property = "type", operator = Operator.EQ)
    private String type;

    @Schema(description = "角色 ID (查詢特定角色的權限)")
    @QueryFilter(property = "roles.id", operator = Operator.EQ)
    private String roleId;

    @Schema(description = "頁碼 (預設 1)")
    @Builder.Default
    private int page = 1;

    @Schema(description = "每頁筆數 (預設 20)")
    @Builder.Default
    private int size = 20;

    @Schema(description = "排序欄位")
    private String sort;
}
