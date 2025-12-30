package com.company.hrms.iam.application.service.role.task;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入角色 Task
 */
@Component
@RequiredArgsConstructor
public class LoadRoleTask implements PipelineTask<RoleContext> {

    private final IRoleRepository roleRepository;

    @Override
    public void execute(RoleContext context) throws Exception {
        String roleId = context.getRoleId();

        var role = roleRepository.findById(RoleId.of(roleId))
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_NOT_FOUND",
                        "角色不存在: " + roleId));

        context.setRole(role);
    }

    @Override
    public String getName() {
        return "載入角色";
    }
}
