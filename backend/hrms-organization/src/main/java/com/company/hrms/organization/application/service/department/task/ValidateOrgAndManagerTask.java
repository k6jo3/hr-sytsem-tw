package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.ValidationException;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import lombok.RequiredArgsConstructor;

/**
 * 驗證組織和主管存在 Task
 */
@Component
@RequiredArgsConstructor
public class ValidateOrgAndManagerTask implements PipelineTask<DepartmentContext> {

    private final IOrganizationRepository organizationRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var request = context.getCreateRequest();

        // 驗證組織存在
        OrganizationId orgId = new OrganizationId(request.getOrganizationId());
        if (!organizationRepository.existsById(orgId)) {
            throw new ValidationException("organizationId",
                    "組織不存在: " + request.getOrganizationId());
        }
        context.setOrganizationId(orgId);

        // 驗證主管存在（若指定）
        if (request.getManagerId() != null && !request.getManagerId().isBlank()) {
            EmployeeId managerId = new EmployeeId(request.getManagerId());
            var manager = employeeRepository.findById(managerId)
                    .orElseThrow(() -> new ValidationException("managerId",
                            "主管不存在: " + request.getManagerId()));
            context.setManager(manager);
        }
    }

    @Override
    public String getName() {
        return "驗證組織和主管存在";
    }
}
