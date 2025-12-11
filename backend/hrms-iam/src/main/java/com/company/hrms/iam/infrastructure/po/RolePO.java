package com.company.hrms.iam.infrastructure.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Role Persistent Object
 * 資料庫映射物件，對應 roles 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePO {

    /**
     * 角色 ID (主鍵)
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
     * 租戶 ID (NULL 表示系統角色)
     */
    private String tenantId;

    /**
     * 是否為系統內建角色
     */
    private Boolean isSystemRole;

    /**
     * 角色狀態
     */
    private String status;

    /**
     * 建立時間
     */
    private Timestamp createdAt;

    /**
     * 更新時間
     */
    private Timestamp updatedAt;
}
