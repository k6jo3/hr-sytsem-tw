package com.company.hrms.iam.infrastructure.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * RolePermission Persistent Object
 * 資料庫映射物件，對應 role_permissions 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionPO {

    /**
     * 角色 ID
     */
    private String roleId;

    /**
     * 權限 ID
     */
    private String permissionId;

    /**
     * 授權時間
     */
    private Timestamp grantedAt;
}
