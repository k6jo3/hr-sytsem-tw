package com.company.hrms.iam.api.request.auth;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理員重置使用者密碼請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResetPasswordRequest {

    /**
     * 新密碼 (若為空則由系統隨機產生)
     */
    @Size(min = 8, max = 128, message = "密碼長度必須介於 8 到 128 個字元")
    private String newPassword;

    /**
     * 是否強制使用者在下次登入時變更密碼
     */
    @Builder.Default
    private boolean forceChangeOnNextLogin = true;

    /**
     * 是否發送 Email 通知使用者新的臨時密碼
     */
    @Builder.Default
    private boolean sendEmailNotification = true;
}
