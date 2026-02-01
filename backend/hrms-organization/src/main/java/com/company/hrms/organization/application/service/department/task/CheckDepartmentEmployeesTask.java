package com.company.hrms.organization.application.service.department.task;

import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;

/**
 * 檢查部門下是否有員工 Task
 */
@Component
@RequiredArgsConstructor
public class CheckDepartmentEmployeesTask implements PipelineTask<DepartmentContext> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        String deptIdStr = context.getDepartmentId();
        DepartmentId deptId = new DepartmentId(deptIdStr);

        // 檢查是否有員工
        List<Employee> employees = employeeRepository.findByDepartmentId(deptId);
        if (!employees.isEmpty()) {
            throw new DomainException("CANNOT_DELETE_DEPT",
                    "無法刪除部門，尚有 " + employees.size() + " 位員工");
        }
    }

    @Override
    public String getName() {
        return "檢查部門員工";
    }
}
