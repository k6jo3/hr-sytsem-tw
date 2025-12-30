package com.company.hrms.organization.application.service.organization.task;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import lombok.RequiredArgsConstructor;

/**
 * 驗證母組織存在 Task
 */
@Component
@RequiredArgsConstructor
public class ValidateParentOrgTask implements PipelineTask<OrganizationContext> {

    private final IOrganizationRepository organizationRepository;

    @Override
    public void execute(OrganizationContext context) throws Exception {
        var request = context.getCreateRequest();

        if (request.getParentId() != null && !request.getParentId().isBlank()) {
            OrganizationId parentId = new OrganizationId(request.getParentId());

            var parent = organizationRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("PARENT_ORG_NOT_FOUND",
                            "母組織不存在: " + request.getParentId()));

            context.setParentId(parentId);
            context.setParentOrganization(parent);
        }
    }

    @Override
    public String getName() {
        return "驗證母組織存在";
    }

    @Override
    public boolean shouldExecute(OrganizationContext context) {
        var request = context.getCreateRequest();
        return request != null && request.getParentId() != null && !request.getParentId().isBlank();
    }
}
