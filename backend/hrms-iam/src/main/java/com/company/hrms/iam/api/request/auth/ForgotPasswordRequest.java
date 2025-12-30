package com.company.hrms.iam.api.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 忘記密碼請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    /**
     * 使用者 Email
     */
    @NotBlank(message = "Email 不可為空")
    @Email(message = "Email 格式不正確")
    private String email;
}
