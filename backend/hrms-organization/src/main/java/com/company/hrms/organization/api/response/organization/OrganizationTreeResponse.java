package com.company.hrms.organization.api.response.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 組織樹狀結構回應 DTO
 */
@Data
@Builder
@Schema(description = "組織樹狀結構回應")
public class OrganizationTreeResponse {

    @Schema(description = "組織ID")
    private String organizationId;

    @Schema(description = "組織代碼")
    private String code;

    @Schema(description = "組織名稱")
    private String name;

    @Schema(description = "組織類型")
    private String type;

    @Schema(description = "組織狀態")
    private String status;

    @Schema(description = "組織總員工數")
    private int employeeCount;

    @Schema(description = "子組織清單")
    private List<OrganizationTreeResponse> children;

    @Schema(description = "部門清單")
    private List<DepartmentTreeNode> departments;

    @Data
    @Builder
    @Schema(description = "部門樹狀節點")
    public static class DepartmentTreeNode {
        @Schema(description = "部門ID")
        private String departmentId;

        @Schema(description = "部門代碼")
        private String code;

        @Schema(description = "部門名稱")
        private String name;

        @Schema(description = "部門層級")
        private int level;

        @Schema(description = "主管ID")
        private String managerId;

        @Schema(description = "主管姓名")
        private String managerName;

        @Schema(description = "部門員工人數")
        private int employeeCount;

        @Schema(description = "子部門清單")
        private List<DepartmentTreeNode> children;
    }
}
