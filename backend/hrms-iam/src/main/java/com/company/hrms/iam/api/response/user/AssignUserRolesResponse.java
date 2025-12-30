package com.company.hrms.iam.api.response.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指派角色回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignUserRolesResponse {

    /**
     * 使用者 ID
     */
    private String userId;

    /**
     * 指派後的角色列表
     */
    private List<RoleInfo> roles;

    /**
     * 角色資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        private String roleId;
        private String roleName;
        private String displayName;
    }
}
