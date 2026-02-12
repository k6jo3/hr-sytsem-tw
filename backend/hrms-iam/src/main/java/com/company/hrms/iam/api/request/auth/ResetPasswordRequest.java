package com.company.hrms.iam.api.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重置密碼請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    /**
     * 重置 Token
     */
    @NotBlank(message = "Token 不可為空")
    private String token;

    /**
     * 目前密碼 (選填，若已登入狀態變更密碼時使用)
     */
    private String currentPassword;

    /**
     * 新密碼
     */
    @NotBlank(message = "新密碼不可為空")
    @Size(min = 8, max = 128, message = "密碼長度必須介於 8 到 128 個字元")
    private String newPassword;

    /**
     * 確認新密碼
     */
    @NotBlank(message = "確認密碼不可為空")
    private String confirmPassword;
}
