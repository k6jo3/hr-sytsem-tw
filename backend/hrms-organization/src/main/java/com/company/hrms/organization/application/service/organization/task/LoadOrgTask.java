package com.company.hrms.organization.application.service.organization.task;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入組織 Task
 */
@Component
@RequiredArgsConstructor
public class LoadOrgTask implements PipelineTask<OrganizationContext> {

    private final IOrganizationRepository organizationRepository;

    @Override
    public void execute(OrganizationContext context) throws Exception {
        String orgId = context.getOrganizationId();

        var organization = organizationRepository.findById(new OrganizationId(orgId))
                .orElseThrow(() -> new ResourceNotFoundException("ORG_NOT_FOUND",
                        "組織不存在: " + orgId));

        context.setOrganization(organization);
    }

    @Override
    public String getName() {
        return "載入組織";
    }
}
