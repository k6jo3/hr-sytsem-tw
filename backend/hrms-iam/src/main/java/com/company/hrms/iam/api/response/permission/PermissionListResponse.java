package com.company.hrms.iam.api.response.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 權限列表回應
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
     * 權限代碼
     */
    private String permissionCode;

    /**
     * 資源名稱
     */
    private String resource;

    /**
     * 操作名稱
     */
    private String action;

    /**
     * 權限描述
     */
    private String description;
}
