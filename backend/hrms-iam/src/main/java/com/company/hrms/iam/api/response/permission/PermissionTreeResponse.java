package com.company.hrms.iam.api.response.permission;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 權限樹結構回應
 * 依據資源進行分組
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionTreeResponse {

    /**
     * 資源代碼 (e.g., user, role)
     */
    private String resource;

    /**
     * 資源顯示名稱 (e.g., 使用者管理, 角色管理)
     */
    private String resourceDisplayName;

    /**
     * 該資源下的權限清單
     */
    @Builder.Default
    private List<PermissionItem> permissions = new ArrayList<>();

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
        private String action;
        private String description;
    }
}
