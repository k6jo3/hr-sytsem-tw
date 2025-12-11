package com.company.hrms.iam.infrastructure.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Permission Persistent Object
 * 資料庫映射物件，對應 permissions 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionPO {

    /**
     * 權限 ID (主鍵)
     */
    private String permissionId;

    /**
     * 權限代碼 (格式: resource:action)
     */
    private String permissionCode;

    /**
     * 權限名稱
     */
    private String permissionName;

    /**
     * 權限描述
     */
    private String description;

    /**
     * 資源名稱
     */
    private String resource;

    /**
     * 操作名稱
     */
    private String action;

    /**
     * 建立時間
     */
    private Timestamp createdAt;
}
