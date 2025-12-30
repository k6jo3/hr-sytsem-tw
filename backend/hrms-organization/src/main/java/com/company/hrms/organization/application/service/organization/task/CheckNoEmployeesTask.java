package com.company.hrms.organization.application.service.organization.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;

/**
 * 檢查組織下無員工 Task
 */
@Component
@RequiredArgsConstructor
public class CheckNoEmployeesTask implements PipelineTask<OrganizationContext> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(OrganizationContext context) throws Exception {
        OrganizationId orgId = context.getOrganization().getId();

        int employeeCount = employeeRepository.countByOrganizationId(orgId);

        if (employeeCount > 0) {
            throw new DomainException("CANNOT_DEACTIVATE_ORG",
                    "無法停用組織，尚有 " + employeeCount + " 位在職員工");
        }
    }

    @Override
    public String getName() {
        return "檢查組織下無員工";
    }
}
