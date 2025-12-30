package com.company.hrms.iam.application.service.role.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.application.service.role.context.RoleContext;
import com.company.hrms.iam.domain.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 檢查角色代碼存在 Task
 */
@Component
@RequiredArgsConstructor
public class CheckRoleCodeExistenceTask implements PipelineTask<RoleContext> {

    private final IRoleRepository roleRepository;

    @Override
    public void execute(RoleContext context) throws Exception {
        var request = context.getCreateRequest();

        if (roleRepository.existsByRoleCodeAndTenantId(request.getRoleCode(), context.getTenantId())) {
            throw new DomainException("ROLE_CODE_EXISTS", "角色代碼已存在");
        }
    }

    @Override
    public String getName() {
        return "檢查角色代碼唯一性";
    }
}
