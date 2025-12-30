package com.company.hrms.iam.api.response.permission;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 權限樹回應
 * 按資源分組的權限結構
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionTreeResponse {

    /**
     * 資源名稱
     */
    private String resource;

    /**
     * 資源顯示名稱
     */
    private String resourceDisplayName;

    /**
     * 該資源下的權限列表
     */
    private List<PermissionItem> permissions;

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
