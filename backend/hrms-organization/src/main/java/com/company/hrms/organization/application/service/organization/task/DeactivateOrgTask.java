package com.company.hrms.organization.application.service.organization.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 停用組織 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeactivateOrgTask implements PipelineTask<OrganizationContext> {

    @Override
    public void execute(OrganizationContext context) throws Exception {
        var organization = context.getOrganization();

        organization.deactivate();

        log.info("組織停用: id={}", organization.getId().getValue());
    }

    @Override
    public String getName() {
        return "停用組織";
    }
}
