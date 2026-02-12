package com.company.hrms.iam.application.service.role.context;

import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.iam.api.request.role.AssignPermissionsRequest;
import com.company.hrms.iam.api.request.role.CreateRoleRequest;
import com.company.hrms.iam.api.request.role.UpdateRoleRequest;
import com.company.hrms.iam.domain.model.aggregate.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 角色 Pipeline Context
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleContext extends PipelineContext {

    // === 輸入 ===
    private CreateRoleRequest createRequest;
    private UpdateRoleRequest updateRequest;
    private AssignPermissionsRequest assignPermissionsRequest;
    private String roleId;
    private String tenantId;

    // === 中間數據 ===
    private Role role;
    private List<Role> roles;

    // === 建構子 ===

    public RoleContext(CreateRoleRequest request, String tenantId) {
        this.createRequest = request;
        this.tenantId = tenantId;
    }

    public RoleContext(String roleId) {
        this.roleId = roleId;
    }

    public RoleContext(String roleId, UpdateRoleRequest request) {
        this.roleId = roleId;
        this.updateRequest = request;
    }

    public RoleContext(String roleId, AssignPermissionsRequest request) {
        this.roleId = roleId;
        this.assignPermissionsRequest = request;
    }
}
