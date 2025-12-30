package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 停用部門 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeactivateDeptTask implements PipelineTask<DepartmentContext> {

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var department = context.getDepartment();

        department.deactivate();

        log.info("部門停用: id={}", department.getId().getValue());
    }

    @Override
    public String getName() {
        return "停用部門";
    }
}
