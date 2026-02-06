package com.company.hrms.organization.api.response.department;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部門列表項目回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "部門列表項目回應")
public class DepartmentListItemResponse {

    @Schema(description = "部門 ID")
    private String departmentId;

    @Schema(description = "部門代碼")
    private String code;

    @Schema(description = "部門名稱")
    private String name;

    @Schema(description = "部門層級")
    private Integer level;

    @Schema(description = "顯示順序")
    private Integer sortOrder;

    @Schema(description = "組織 ID")
    private String organizationId;

    @Schema(description = "上層部門 ID")
    private String parentId;

    @Schema(description = "部門主管 ID")
    private String managerId;

    @Schema(description = "部門主管姓名")
    private String managerName;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "狀態名稱")
    private String statusDisplay;

    @Schema(description = "員工人數 (列表查詢中固定為 0,詳情查詢中才會載入實際值)")
    private Integer employeeCount;
}
