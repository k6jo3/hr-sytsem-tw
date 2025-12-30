package com.company.hrms.organization.application.service.organization.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入組織統計資訊 Task
 */
@Component
@RequiredArgsConstructor
public class LoadOrgStatsTask implements PipelineTask<OrganizationContext> {

    private final IDepartmentRepository departmentRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(OrganizationContext context) throws Exception {
        OrganizationId orgId = context.getOrganization().getId();

        int departmentCount = departmentRepository.countByOrganizationId(orgId);
        int employeeCount = employeeRepository.countByOrganizationId(orgId);

        context.setDepartmentCount(departmentCount);
        context.setEmployeeCount(employeeCount);
    }

    @Override
    public String getName() {
        return "載入組織統計資訊";
    }
}
