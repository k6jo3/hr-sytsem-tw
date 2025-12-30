package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 刪除角色 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteRoleTask implements PipelineTask<RoleContext> {

    private final IRoleRepository roleRepository;

    @Override
    public void execute(RoleContext context) throws Exception {
        var role = context.getRole();
        roleRepository.deleteById(role.getId());
        log.info("角色刪除: roleId={}", role.getId().getValue());
    }

    @Override
    public String getName() {
        return "刪除角色";
    }
}
