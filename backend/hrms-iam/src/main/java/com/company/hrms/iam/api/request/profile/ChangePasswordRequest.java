package com.company.hrms.iam.api.request.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 變更密碼請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    /**
     * 目前密碼
     */
    @NotBlank(message = "目前密碼不可為空")
    private String currentPassword;

    /**
     * 新密碼
     */
    @NotBlank(message = "新密碼不可為空")
    @Size(min = 8, max = 100, message = "新密碼長度須為 8-100 字元")
    private String newPassword;

    /**
     * 確認新密碼
     */
    @NotBlank(message = "確認新密碼不可為空")
    private String confirmPassword;
}
