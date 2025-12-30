package com.company.hrms.organization.application.service.organization.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.ResourceAlreadyExistsException;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import lombok.RequiredArgsConstructor;

/**
 * 檢查組織代碼是否存在 Task
 */
@Component
@RequiredArgsConstructor
public class CheckOrgCodeExistenceTask implements PipelineTask<OrganizationContext> {

    private final IOrganizationRepository organizationRepository;

    @Override
    public void execute(OrganizationContext context) throws Exception {
        var request = context.getCreateRequest();

        if (organizationRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("ORG_CODE_EXISTS",
                    "組織代碼已存在: " + request.getCode());
        }
    }

    @Override
    public String getName() {
        return "檢查組織代碼唯一性";
    }
}
