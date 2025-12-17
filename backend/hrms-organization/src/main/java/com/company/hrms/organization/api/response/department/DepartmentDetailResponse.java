package com.company.hrms.organization.api.response.department;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 部門詳情回應 DTO
 */
@Data
@Builder
@Schema(description = "部門詳情回應")
public class DepartmentDetailResponse {

    @Schema(description = "部門ID")
    private String departmentId;

    @Schema(description = "部門代碼")
    private String code;

    @Schema(description = "部門名稱")
    private String name;

    @Schema(description = "部門英文名稱")
    private String nameEn;

    @Schema(description = "所屬組織ID")
    private String organizationId;

    @Schema(description = "所屬組織名稱")
    private String organizationName;

    @Schema(description = "父部門ID")
    private String parentId;

    @Schema(description = "父部門名稱")
    private String parentName;

    @Schema(description = "部門層級 (1-5)")
    private int level;

    @Schema(description = "部門路徑")
    private String path;

    @Schema(description = "部門主管ID")
    private String managerId;

    @Schema(description = "部門主管姓名")
    private String managerName;

    @Schema(description = "部門狀態")
    private String status;

    @Schema(description = "部門狀態顯示名稱")
    private String statusDisplay;

    @Schema(description = "排序順序")
    private int sortOrder;

    @Schema(description = "部門說明")
    private String description;

    @Schema(description = "員工數")
    private int employeeCount;

    @Schema(description = "子部門數")
    private int childDepartmentCount;
}
