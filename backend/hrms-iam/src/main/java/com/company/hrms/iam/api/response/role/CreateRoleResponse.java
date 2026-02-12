package com.company.hrms.iam.api.response.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 建立角色回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleResponse {

    /**
     * 角色 ID
     */
    private String roleId;

    /**
     * 角色名稱
     */
    private String roleName;

    /**
     * 角色代碼
     */
    private String roleCode;
}
