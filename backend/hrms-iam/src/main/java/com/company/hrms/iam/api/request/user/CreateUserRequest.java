package com.company.hrms.iam.api.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增使用者請求 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    /**
     * 使用者名稱 (登入帳號)
     */
    @NotBlank(message = "使用者名稱不可為空")
    @Size(min = 3, max = 50, message = "使用者名稱長度必須在 3-50 字元之間")
    private String username;

    /**
     * Email
     */
    @NotBlank(message = "Email 不可為空")
    @Email(message = "Email 格式無效")
    private String email;

    /**
     * 密碼
     */
    @NotBlank(message = "密碼不可為空")
    @Size(min = 8, max = 100, message = "密碼長度必須在 8-100 字元之間")
    private String password;

    /**
     * 顯示名稱
     */
    @NotBlank(message = "顯示名稱不可為空")
    @Size(max = 100, message = "顯示名稱長度不可超過 100 字元")
    private String displayName;
}
