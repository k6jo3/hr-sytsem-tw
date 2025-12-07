package com.company.hrms.iam.api.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 使用者詳情回應 VO
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
     * 使用者狀態
     */
    private String status;

    /**
     * 角色列表
     */
    private List<String> roles;

    /**
     * 最後登入時間
     */
    private LocalDateTime lastLoginAt;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;
}
