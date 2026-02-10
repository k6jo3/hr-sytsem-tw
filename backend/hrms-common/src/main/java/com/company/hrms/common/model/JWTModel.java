package com.company.hrms.common.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT Token 解析後的使用者資訊模型
 * 用於存放從 JWT Token 解析出的當前使用者資訊
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JWTModel {

    /**
     * 使用者ID
     */
    private String userId;

    /**
     * 員工ID
     */
    private String employeeId;

    /**
     * 使用者名稱
     */
    private String username;

    /**
     * 員工編號
     */
    private String employeeNumber;

    /**
     * 使用者姓名
     */
    private String displayName;

    /**
     * 使用者Email
     */
    private String email;

    /**
     * 所屬部門ID
     */
    private String departmentId;

    /**
     * 所屬部門名稱
     */
    private String departmentName;

    /**
     * 使用者角色列表
     */
    private List<String> roles;

    /**
     * 使用者權限列表
     */
    private List<String> permissions;

    /**
     * Token 過期時間 (Unix timestamp)
     */
    private Long expiresAt;

    /**
     * 管理的部門ID列表 (主管權限使用)
     */
    private List<String> managedDepartmentIds;

    private String tenantId;

    /**
     * 檢查使用者是否具有指定角色
     * 
     * @param role 角色名稱
     * @return 是否具有該角色
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * 檢查使用者是否具有指定權限
     * 
     * @param permission 權限名稱
     * @return 是否具有該權限
     */
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}
