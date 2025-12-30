package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 停用角色 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeactivateRoleTask implements PipelineTask<RoleContext> {

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();
        role.deactivate();
        log.info("角色停用: roleId={}", role.getId().getValue());
    }

    @Override
    public String getName() {
        return "停用角色";
    }
}
