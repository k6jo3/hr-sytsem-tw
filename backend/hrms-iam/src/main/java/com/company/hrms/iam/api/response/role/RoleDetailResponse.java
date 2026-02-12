package com.company.hrms.iam.api.response.role;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色詳細資訊回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDetailResponse {

    /**
     * 角色 ID
     */
    private String roleId;

    /**
     * 角色名稱
     */
    private String roleName;

    /**
     * 角色代碼
     */
    private String roleCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否為系統角色
     */
    @JsonProperty("isSystemRole")
    private boolean isSystemRole;

    /**
     * 狀態 (ACTIVE, INACTIVE)
     */
    private String status;

    /**
     * 租戶 ID
     */
    private String tenantId;

    /**
     * 關聯用戶數量
     */
    private int userCount;

    /**
     * 權限代碼列表
     */
    private List<String> permissions;

    /**
     * 權限詳細資訊列表
     */
    private List<PermissionItem> permissionDetails;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    /**
     * 權限簡要資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionItem {
        private String permissionId;
        private String permissionCode;
        private String permissionName;
    }
}
