package com.company.hrms.iam.api.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重設密碼請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    /**
     * 當前密碼 (自行修改密碼時需要)
     */
    private String currentPassword;

    /**
     * 新密碼
     */
    @NotBlank(message = "新密碼不可為空")
    @Size(min = 8, max = 128, message = "密碼長度須為 8-128 個字元")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "密碼須包含大寫字母、小寫字母、數字及特殊字元"
    )
    private String newPassword;

    /**
     * 確認新密碼
     */
    @NotBlank(message = "確認密碼不可為空")
    private String confirmPassword;
}
