package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存部門 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveDeptTask implements PipelineTask<DepartmentContext> {

    private final IDepartmentRepository departmentRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var department = context.getDepartment();

        departmentRepository.save(department);

        log.info("部門儲存成功: id={}, code={}",
                department.getId().getValue(),
                department.getCode());
    }

    @Override
    public String getName() {
        return "儲存部門";
    }
}
