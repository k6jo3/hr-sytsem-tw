package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入部門 Task
 */
@Component
@RequiredArgsConstructor
public class LoadDeptTask implements PipelineTask<DepartmentContext> {

    private final IDepartmentRepository departmentRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        String deptId = context.getDepartmentId();

        var department = departmentRepository.findById(new DepartmentId(deptId))
                .orElseThrow(() -> new EntityNotFoundException("Department", deptId));

        context.setDepartment(department);
    }

    @Override
    public String getName() {
        return "載入部門";
    }
}
