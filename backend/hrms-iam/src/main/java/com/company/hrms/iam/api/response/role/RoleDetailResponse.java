package com.company.hrms.iam.api.response.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色詳情回應
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
     * 角色描述
     */
    private String description;

    /**
     * 租戶 ID
     */
    private String tenantId;

    /**
     * 是否為系統角色
     */
    private boolean systemRole;

    /**
     * 角色狀態
     */
    private String status;

    /**
     * 權限列表
     */
    private List<PermissionItem> permissions;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    /**
     * 權限項目
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
