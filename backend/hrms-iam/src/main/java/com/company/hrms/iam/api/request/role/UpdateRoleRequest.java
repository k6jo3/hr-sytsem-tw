package com.company.hrms.iam.api.request.role;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @Size(max = 50, message = "角色名稱長度不可超過 50 個字元")
    private String roleName;

    /**
     * 角色描述
     */
    @Size(max = 255, message = "角色描述長度不可超過 255 個字元")
    private String description;

    /**
     * 權限 ID 列表 (若提供，將覆蓋現有權限)
     */
    private List<String> permissionIds;
}
