package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;

import lombok.RequiredArgsConstructor;

/**
 * 建立角色聚合根 Task
 */
@Component
@RequiredArgsConstructor
public class CreateRoleAggregateTask implements PipelineTask<RoleContext> {

    @Override
    public void execute(RoleContext context) throws Exception {
        var request = context.getCreateRequest();

        // 建立角色
        Role role = Role.create(
                request.getRoleName(),
                request.getRoleCode(),
                request.getDescription(),
                context.getTenantId());

        // 指派權限
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            for (String permissionId : request.getPermissionIds()) {
                role.assignPermission(PermissionId.of(permissionId));
            }
        }

        context.setRole(role);
    }

    @Override
    public String getName() {
        return "建立角色聚合根";
    }
}
