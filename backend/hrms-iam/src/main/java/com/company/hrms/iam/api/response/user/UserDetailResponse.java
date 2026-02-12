package com.company.hrms.iam.api.response.user;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 使用者詳細資訊回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {

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
     * 關聯員工 ID
     */
    private String employeeId;

    /**
     * 租戶 ID
     */
    private String tenantId;

    /**
     * 角色列表 (代碼)
     */
    private List<String> roles;

    /**
     * 角色詳細資訊
     */
    private List<RoleInfo> roleDetails;

    /**
     * 帳號狀態
     */
    private String status;

    /**
     * 登入失敗次數
     */
    private int failedLoginAttempts;

    /**
     * 最後登入時間
     */
    private LocalDateTime lastLoginAt;

    /**
     * 最後登入 IP
     */
    private String lastLoginIp;

    /**
     * 密碼變更時間
     */
    private LocalDateTime passwordChangedAt;

    /**
     * 是否需要首次登入變更密碼
     */
    private boolean mustChangePassword;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

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
