package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入部門統計資訊 Task
 */
@Component
@RequiredArgsConstructor
public class LoadDeptStatsTask implements PipelineTask<DepartmentContext> {

    private final IDepartmentRepository departmentRepository;
    private final IEmployeeRepository employeeRepository;
    private final IOrganizationRepository organizationRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var department = context.getDepartment();

        // 載入員工數和子部門數
        int employeeCount = employeeRepository.findByDepartmentId(department.getId()).size();
        int childCount = departmentRepository.countByParentId(department.getId());

        context.setEmployeeCount(employeeCount);
        context.setChildDepartmentCount(childCount);

        // 載入組織名稱
        if (department.getOrganizationId() != null) {
            organizationRepository.findById(department.getOrganizationId())
                    .ifPresent(org -> context.setOrganizationName(org.getName()));
        }

        // 載入父部門名稱
        if (department.getParentId() != null) {
            departmentRepository.findById(department.getParentId())
                    .ifPresent(parent -> context.setParentName(parent.getName()));
        }

        // 載入主管名稱
        if (department.getManagerId() != null) {
            employeeRepository.findById(department.getManagerId())
                    .ifPresent(manager -> context.setManagerName(manager.getFirstName() + " " + manager.getLastName()));
        }
    }

    @Override
    public String getName() {
        return "載入部門統計資訊";
    }
}
