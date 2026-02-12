package com.company.hrms.iam.api.response.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 權限列表項目回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionListResponse {

    /**
     * 權限 ID
     */
    private String permissionId;

    /**
     * 權限代碼 (e.g., user:read)
     */
    private String permissionCode;

    /**
     * 權限名稱
     */
    private String permissionName;

    /**
     * 所屬資源
     */
    private String resource;

    /**
     * 操作類型
     */
    private String action;

    /**
     * 描述
     */
    private String description;
}
