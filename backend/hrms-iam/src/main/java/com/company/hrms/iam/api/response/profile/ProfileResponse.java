package com.company.hrms.iam.api.response.profile;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 個人資料回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

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
     * 員工 ID
     */
    private String employeeId;

    /**
     * 租戶 ID
     */
    private String tenantId;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 最後登入時間
     */
    private LocalDateTime lastLoginAt;

    /**
     * 是否需要變更密碼
     */
    private boolean mustChangePassword;
}
