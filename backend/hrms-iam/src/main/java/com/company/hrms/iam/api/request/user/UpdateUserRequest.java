package com.company.hrms.iam.api.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新使用者請求 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    /**
     * Email
     */
    @Email(message = "Email 格式無效")
    private String email;

    /**
     * 顯示名稱
     */
    @Size(max = 100, message = "顯示名稱長度不可超過 100 字元")
    private String displayName;
}
