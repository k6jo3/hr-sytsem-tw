package com.company.hrms.iam.api.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登入請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 使用者名稱 (Email)
     */
    @NotBlank(message = "使用者名稱不可為空")
    private String username;

    /**
     * 密碼
     */
    @NotBlank(message = "密碼不可為空")
    private String password;

    /**
     * 租戶 ID (可選)
     */
    private String tenantId;
}
