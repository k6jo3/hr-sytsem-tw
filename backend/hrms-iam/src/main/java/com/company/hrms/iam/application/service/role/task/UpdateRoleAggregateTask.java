package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;

import lombok.RequiredArgsConstructor;

/**
 * 更新角色聚合根 Task
 */
@Component
@RequiredArgsConstructor
public class UpdateRoleAggregateTask implements PipelineTask<RoleContext> {

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();
        var request = context.getUpdateRequest();

        // 更新基本資訊
        role.update(request.getRoleName(), request.getDescription());

        // 更新權限
        if (request.getPermissionIds() != null) {
            role.clearPermissions();
            for (String permissionId : request.getPermissionIds()) {
                role.assignPermission(PermissionId.of(permissionId));
            }
        }
    }

    @Override
    public String getName() {
        return "更新角色聚合根";
    }
}
