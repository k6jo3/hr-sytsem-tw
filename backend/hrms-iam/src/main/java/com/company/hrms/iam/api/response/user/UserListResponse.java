package com.company.hrms.iam.api.response.user;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 使用者列表項目回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {

    /**
     * 使用者 ID
     */
    private String userId;

    /**
     * 使用者名稱
     */
    private String username;

    /**
     * Email
     */
    private String email;

    /**
     * 顯示名稱
     */
    private String displayName;

    /**
     * 姓
     */
    private String firstName;

    /**
     * 名
     */
    private String lastName;

    /**
     * 關聯員工 ID
     */
    private String employeeId;

    /**
     * 員工姓名
     */
    private String employeeName;

    /**
     * 部門名稱
     */
    private String department;

    /**
     * 角色代碼列表
     */
    private List<String> roles;

    /**
     * 租戶 ID
     */
    private String tenantId;

    /**
     * 帳號狀態
     */
    private String status;

    /**
     * 最後登入時間
     */
    private LocalDateTime lastLoginAt;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 角色簡要資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        private String roleId;
        private String roleName;
        private String displayName;
    }
}
