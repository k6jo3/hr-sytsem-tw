package com.company.hrms.iam.application.service.role.task;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 指派權限給角色 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AssignPermissionsTask implements PipelineTask<RoleContext> {

    private final IRoleRepository roleRepository;

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();
        var request = context.getAssignPermissionsRequest();

        List<PermissionId> permissionIds = request.getPermissionIds().stream()
                .map(PermissionId::new)
                .collect(Collectors.toList());

        log.info("正在指派權限給角色: roleId={}, permissionsCount={}",
                role.getId().getValue(), permissionIds.size());

        role.updatePermissions(permissionIds);

        roleRepository.update(role);

        log.info("權限指派成功: roleId={}", role.getId().getValue());
    }

    @Override
    public String getName() {
        return "指派角色權限";
    }
}
