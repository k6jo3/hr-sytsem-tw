package com.company.hrms.iam.api.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 刷新 Token 請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    /**
     * Refresh Token
     */
    @NotBlank(message = "Refresh Token 不可為空")
    private String refreshToken;
}
