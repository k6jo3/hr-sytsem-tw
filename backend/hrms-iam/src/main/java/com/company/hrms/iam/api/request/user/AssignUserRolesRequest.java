package com.company.hrms.iam.api.request.user;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 指派角色給使用者請求
 */
@Data
public class AssignUserRolesRequest {

    /**
     * 角色 ID 列表
     */
    @NotEmpty(message = "角色列表不可為空")
    private List<String> roleIds;
}
