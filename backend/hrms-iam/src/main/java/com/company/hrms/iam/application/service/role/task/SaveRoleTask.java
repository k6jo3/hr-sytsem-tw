package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存角色 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveRoleTask implements PipelineTask<RoleContext> {

    private final IRoleRepository roleRepository;

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();

        roleRepository.save(role);

        log.info("角色儲存成功: id={}, code={}", role.getId().getValue(), role.getRoleCode());
    }

    @Override
    public String getName() {
        return "儲存角色";
    }
}
