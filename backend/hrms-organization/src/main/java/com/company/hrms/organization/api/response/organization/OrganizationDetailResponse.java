package com.company.hrms.organization.api.response.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 組織詳情回應 DTO
 */
@Data
@Builder
@Schema(description = "組織詳情回應")
public class OrganizationDetailResponse {

    @Schema(description = "組織ID")
    private String organizationId;

    @Schema(description = "組織代碼")
    private String code;

    @Schema(description = "組織名稱")
    private String name;

    @Schema(description = "組織英文名稱")
    private String nameEn;

    @Schema(description = "組織類型")
    private String type;

    @Schema(description = "組織類型顯示名稱")
    private String typeDisplay;

    @Schema(description = "組織狀態")
    private String status;

    @Schema(description = "組織狀態顯示名稱")
    private String statusDisplay;

    @Schema(description = "母組織ID")
    private String parentId;

    @Schema(description = "母組織名稱")
    private String parentName;

    @Schema(description = "統一編號")
    private String taxId;

    @Schema(description = "聯絡電話")
    private String phone;

    @Schema(description = "傳真")
    private String fax;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "成立日期")
    private LocalDate establishedDate;

    @Schema(description = "組織說明")
    private String description;

    @Schema(description = "員工數")
    private int employeeCount;

    @Schema(description = "部門數")
    private int departmentCount;
}
