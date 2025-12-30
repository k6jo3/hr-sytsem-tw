package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 啟用角色 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivateRoleTask implements PipelineTask<RoleContext> {

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();
        role.activate();
        log.info("角色啟用: roleId={}", role.getId().getValue());
    }

    @Override
    public String getName() {
        return "啟用角色";
    }
}
