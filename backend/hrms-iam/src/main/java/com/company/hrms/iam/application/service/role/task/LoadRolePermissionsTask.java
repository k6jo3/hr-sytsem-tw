package com.company.hrms.iam.application.service.role.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.domain.repository.IPermissionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入角色權限 Task
 */
@Component
@RequiredArgsConstructor
public class LoadRolePermissionsTask implements PipelineTask<RoleContext> {

    private final IPermissionRepository permissionRepository;

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();

        List<Permission> permissions = permissionRepository.findByIds(role.getPermissionIds());
        context.setAttribute("permissions", permissions);
    }

    @Override
    public String getName() {
        return "載入角色權限";
    }
}
