package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 指派權限 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AssignPermissionsTask implements PipelineTask<RoleContext> {

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();
        var request = context.getAssignPermissionsRequest();

        // 清除現有權限
        role.clearPermissions();

        // 指派新權限
        if (request.permissionIds() != null) {
            for (String permissionId : request.permissionIds()) {
                role.assignPermission(PermissionId.of(permissionId));
            }
        }

        log.info("權限指派: roleId={}, permissionCount={}",
                role.getId().getValue(),
                request.permissionIds() != null ? request.permissionIds().size() : 0);
    }

    @Override
    public String getName() {
        return "指派權限";
    }
}
