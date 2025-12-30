package com.company.hrms.organization.application.service.organization.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;

import lombok.RequiredArgsConstructor;

/**
 * 更新組織聚合根 Task
 */
@Component
@RequiredArgsConstructor
public class UpdateOrgAggregateTask implements PipelineTask<OrganizationContext> {

    @Override
    public void execute(OrganizationContext context) throws Exception {
        var organization = context.getOrganization();
        var request = context.getUpdateRequest();

        organization.update(
                request.getName(),
                request.getNameEn(),
                request.getTaxId(),
                request.getAddress(),
                request.getPhone());
    }

    @Override
    public String getName() {
        return "更新組織聚合根";
    }
}
