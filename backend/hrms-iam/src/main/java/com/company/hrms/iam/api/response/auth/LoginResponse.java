package com.company.hrms.iam.api.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登入回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * Access Token
     */
    private String accessToken;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * Token 類型 (Bearer)
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access Token 有效期 (秒)
     */
    private Long expiresIn;

    /**
     * 使用者資訊
     */
    private UserInfo user;

    /**
     * 使用者資訊內部類別
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        /**
         * 使用者 ID
         */
        private String userId;

        /**
         * 使用者名稱
         */
        private String username;

        /**
         * 顯示名稱
         */
        private String displayName;

        /**
         * Email
         */
        private String email;

        /**
         * 角色列表
         */
        private List<String> roles;
    }
}
