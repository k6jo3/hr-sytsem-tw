package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;

/**
 * 檢查部門可停用 Task
 */
@Component
@RequiredArgsConstructor
public class CheckDeptCanDeactivateTask implements PipelineTask<DepartmentContext> {

    private final IDepartmentRepository departmentRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var department = context.getDepartment();

        // 檢查是否有員工
        int employeeCount = employeeRepository.findByDepartmentId(department.getId()).size();
        if (employeeCount > 0) {
            throw new DomainException("CANNOT_DEACTIVATE_DEPT",
                    "無法停用部門，尚有 " + employeeCount + " 位員工");
        }

        // 檢查是否有子部門
        int childCount = departmentRepository.countByParentId(department.getId());
        if (childCount > 0) {
            throw new DomainException("CANNOT_DEACTIVATE_DEPT",
                    "無法停用部門，尚有 " + childCount + " 個子部門");
        }
    }

    @Override
    public String getName() {
        return "檢查部門可停用";
    }
}
