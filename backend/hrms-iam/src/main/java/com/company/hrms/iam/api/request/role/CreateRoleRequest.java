package com.company.hrms.iam.api.request.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 建立角色請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {

    /**
     * 角色名稱
     */
    @NotBlank(message = "角色名稱不可為空")
    @Size(max = 50, message = "角色名稱長度不可超過 50 個字元")
    private String roleName;

    /**
     * 角色代碼 (如 ADMIN, HR_ADMIN)
     */
    @NotBlank(message = "角色代碼不可為空")
    @Size(max = 50, message = "角色代碼長度不可超過 50 個字元")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色代碼格式不正確，必須以大寫字母開頭，只能包含大寫字母、數字和底線")
    private String roleCode;

    /**
     * 角色描述
     */
    @Size(max = 255, message = "角色描述長度不可超過 255 個字元")
    private String description;

    /**
     * 權限 ID 列表
     */
    private List<String> permissionIds;
}
