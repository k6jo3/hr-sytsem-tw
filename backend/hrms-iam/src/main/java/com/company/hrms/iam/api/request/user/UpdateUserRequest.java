package com.company.hrms.iam.api.request.user;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新使用者請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    /**
     * Email (可選)
     */
    @Email(message = "Email 格式無效")
    private String email;

    /**
     * 顯示名稱 (可選)
     */
    @Size(max = 100, message = "顯示名稱長度不可超過 100 字元")
    private String displayName;

    /**
     * 姓 (可選)
     */
    @Size(max = 50, message = "姓的長度不可超過 50 字元")
    private String firstName;

    /**
     * 名 (可選)
     */
    @Size(max = 50, message = "名的長度不可超過 50 字元")
    private String lastName;

    /**
     * 角色列表 (可選)
     */
    private List<String> roles;

    /**
     * 員工編號（關聯員工，可選）
     */
    private String employeeId;

    /**
     * 是否需要變更密碼
     */
    private Boolean mustChangePassword;
}
