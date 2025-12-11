package com.company.hrms.iam.api.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理員重設使用者密碼請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResetPasswordRequest {

    /**
     * 新密碼 (若不提供則產生臨時密碼)
     */
    @Size(min = 8, max = 128, message = "密碼長度須為 8-128 個字元")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "密碼須包含大寫字母、小寫字母、數字及特殊字元"
    )
    private String newPassword;

    /**
     * 是否要求使用者下次登入時變更密碼
     */
    private boolean mustChangePassword = true;

    /**
     * 是否發送通知給使用者
     */
    private boolean sendNotification = true;
}
