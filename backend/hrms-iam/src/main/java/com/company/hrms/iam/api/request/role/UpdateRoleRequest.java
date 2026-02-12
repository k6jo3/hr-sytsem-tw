package com.company.hrms.iam.api.request.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新角色請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {

    /**
     * 角色名稱
     */
    @NotBlank(message = "角色名稱不可為空")
    @Size(min = 2, max = 50, message = "角色名稱長度須為 2-50 字元")
    private String roleName;

    /**
     * 描述
     */
    @Size(max = 200, message = "描述不可超過 200 字元")
    private String description;

    /**
     * 權限 ID 列表
     */
    private java.util.List<String> permissionIds;
}
