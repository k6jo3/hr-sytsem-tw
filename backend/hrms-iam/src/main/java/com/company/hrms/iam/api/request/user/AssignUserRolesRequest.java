package com.company.hrms.iam.api.request.user;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指派使用者角色請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignUserRolesRequest {

    /**
     * 角色 ID 列表
     */
    @NotEmpty(message = "角色列表不可為空")
    private List<String> roleIds;
}
