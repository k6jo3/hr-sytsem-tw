package com.company.hrms.organization.api.request.department;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查詢部門列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢部門列表請求")
public class GetDepartmentListRequest {

    @Schema(description = "搜尋關鍵字 (代碼/名稱)")
    private String keyword;

    @Schema(description = "部門代碼")
    @QueryFilter(property = "department_code", operator = Operator.EQ)
    private String code;

    @Schema(description = "部門名稱 (模糊查詢)")
    @QueryFilter(property = "department_name", operator = Operator.LIKE)
    private String name;

    @Schema(description = "組織 ID")
    @QueryFilter(property = "organization_id", operator = Operator.EQ)
    private String organizationId;

    @Schema(description = "上層部門 ID")
    @QueryFilter(property = "parent_department_id", operator = Operator.EQ)
    private String parentId;

    @Schema(description = "狀態 (ACTIVE/INACTIVE)")
    @QueryFilter(property = "status", operator = Operator.EQ)
    private String status;

    @Schema(description = "頁碼 (預設 1)")
    @Builder.Default
    private int page = 1;

    @Schema(description = "每頁筆數 (預設 20)")
    @Builder.Default
    private int size = 20;

    @Schema(description = "排序欄位")
    private String sort;
}
