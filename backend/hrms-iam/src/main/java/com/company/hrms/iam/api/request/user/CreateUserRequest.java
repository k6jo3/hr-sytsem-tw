package com.company.hrms.iam.api.request.user;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 建立使用者請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    /**
     * 使用者名稱
     */
    @NotBlank(message = "使用者名稱不可為空")
    @Size(min = 4, max = 50, message = "使用者名稱長度須為 4-50 字元")
    private String username;

    /**
     * Email
     */
    @NotBlank(message = "Email 不可為空")
    @Email(message = "Email 格式無效")
    private String email;

    /**
     * 密碼 (若為空則由系統產生並發送通知)
     */
    @Size(min = 8, max = 100, message = "密碼長度須為 8-100 字元")
    private String password;

    /**
     * 顯示名稱
     */
    @NotBlank(message = "顯示名稱不可為空")
    @Size(max = 100, message = "顯示名稱長度不可超過 100 字元")
    private String displayName;

    /**
     * 關聯員工 ID (可選)
     */
    private String employeeId;

    /**
     * 租戶 ID (多租戶隔離)
     */
    private String tenantId;

    /**
     * 角色 ID 列表 (可選)
     */
    private List<String> roleIds;
}
