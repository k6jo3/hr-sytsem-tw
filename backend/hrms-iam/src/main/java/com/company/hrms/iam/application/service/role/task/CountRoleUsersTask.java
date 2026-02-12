package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 統計角色使用者數量 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CountRoleUsersTask implements PipelineTask<RoleContext> {

    private final IRoleRepository roleRepository;

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();
        if (role == null) {
            log.warn("Role not found in context, skipping user count.");
            return;
        }

        RoleId roleId = role.getId();
        int userCount = roleRepository.countUsersByRole(roleId);

        context.setAttribute("userCount", userCount);
        log.debug("Role user count: {}", userCount);
    }

    @Override
    public String getName() {
        return "統計角色使用者數量";
    }
}
