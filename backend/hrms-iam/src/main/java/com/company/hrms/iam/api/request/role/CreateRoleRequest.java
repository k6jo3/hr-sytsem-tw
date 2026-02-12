package com.company.hrms.iam.api.request.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Size(min = 2, max = 50, message = "角色名稱長度須為 2-50 字元")
    private String roleName;

    /**
     * 角色代碼
     */
    @NotBlank(message = "角色代碼不可為空")
    @Size(min = 2, max = 50, message = "角色代碼長度須為 2-50 字元")
    private String roleCode;

    /**
     * 描述
     */
    @Size(max = 200, message = "描述不可超過 200 字元")
    private String description;

    /**
     * 是否為系統角色 (預設 false)
     */
    private boolean isSystemRole;

    /**
     * 權限 ID 列表
     */
    private java.util.List<String> permissionIds;
}
