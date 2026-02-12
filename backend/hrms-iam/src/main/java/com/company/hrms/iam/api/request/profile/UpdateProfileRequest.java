package com.company.hrms.iam.api.request.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新個人資料請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    /**
     * Email (可選)
     */
    @Email(message = "Email 格式不正確")
    private String email;

    /**
     * 顯示名稱 (可選)
     */
    @Size(min = 1, max = 100, message = "顯示名稱長度須為 1-100 字元")
    private String displayName;
}
