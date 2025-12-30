package com.company.hrms.organization.application.service.organization.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.OrganizationType;

import lombok.RequiredArgsConstructor;

/**
 * 建立組織聚合根 Task
 */
@Component
@RequiredArgsConstructor
public class CreateOrgAggregateTask implements PipelineTask<OrganizationContext> {

    @Override
    public void execute(OrganizationContext context) throws Exception {
        var request = context.getCreateRequest();

        // 使用工廠方法建立基本組織
        Organization organization = Organization.create(
                request.getCode(),
                request.getName(),
                request.getNameEn(),
                request.getTaxId());

        // 使用 reconstitute 設定完整屬性
        Organization fullOrganization = Organization.reconstitute(
                organization.getId(),
                organization.getCode(),
                organization.getName(),
                organization.getNameEn(),
                parseOrganizationType(request.getType()),
                organization.getStatus(),
                context.getParentId(),
                request.getTaxId(),
                request.getPhone(),
                request.getFax(),
                request.getEmail(),
                request.getAddress(),
                request.getEstablishedDate(),
                request.getDescription());

        context.setOrganization(fullOrganization);
    }

    private OrganizationType parseOrganizationType(String type) {
        if (type == null || type.isBlank()) {
            return OrganizationType.PARENT;
        }
        try {
            return OrganizationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OrganizationType.PARENT;
        }
    }

    @Override
    public String getName() {
        return "建立組織聚合根";
    }
}
