package com.company.hrms.iam.api.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重新整理 Token 回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {

    /**
     * 新的 Access Token
     */
    private String accessToken;

    /**
     * Token 類型 (Bearer)
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access Token 有效期 (秒)
     */
    private Long expiresIn;
}
