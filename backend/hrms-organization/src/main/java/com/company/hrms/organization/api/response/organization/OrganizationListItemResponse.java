package com.company.hrms.organization.api.response.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 組織清單項目回應 DTO
 */
@Data
@Builder
@Schema(description = "組織清單項目")
public class OrganizationListItemResponse {

    @Schema(description = "組織ID")
    private String organizationId;

    @Schema(description = "組織代碼")
    private String code;

    @Schema(description = "組織名稱")
    private String name;

    @Schema(description = "組織類型")
    private String type;

    @Schema(description = "組織類型顯示名稱")
    private String typeDisplay;

    @Schema(description = "組織狀態")
    private String status;

    @Schema(description = "母組織ID")
    private String parentId;

    @Schema(description = "母組織名稱")
    private String parentName;

    @Schema(description = "員工數")
    private int employeeCount;

    @Schema(description = "部門數")
    private int departmentCount;
}
