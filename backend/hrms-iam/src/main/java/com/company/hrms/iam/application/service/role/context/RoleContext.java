package com.company.hrms.iam.application.service.role.context;

import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.iam.api.controller.role.HR01RoleCmdController.AssignPermissionsRequest;
import com.company.hrms.iam.api.request.role.CreateRoleRequest;
import com.company.hrms.iam.api.request.role.UpdateRoleRequest;
import com.company.hrms.iam.domain.model.aggregate.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 角色 Pipeline Context
 */
@Getter
@Setter
@NoArgsConstructor
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
