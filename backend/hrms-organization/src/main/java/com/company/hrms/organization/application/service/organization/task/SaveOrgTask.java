package com.company.hrms.organization.application.service.organization.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存組織 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveOrgTask implements PipelineTask<OrganizationContext> {

    private final IOrganizationRepository organizationRepository;

    @Override
    public void execute(OrganizationContext context) throws Exception {
        var organization = context.getOrganization();

        organizationRepository.save(organization);

        log.info("組織儲存成功: id={}, code={}",
                organization.getId().getValue(),
                organization.getCode());
    }

    @Override
    public String getName() {
        return "儲存組織";
    }
}
