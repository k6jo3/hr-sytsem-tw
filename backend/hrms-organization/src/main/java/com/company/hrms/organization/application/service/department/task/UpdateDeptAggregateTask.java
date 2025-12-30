package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;

import lombok.RequiredArgsConstructor;

/**
 * 更新部門聚合根 Task
 */
@Component
@RequiredArgsConstructor
public class UpdateDeptAggregateTask implements PipelineTask<DepartmentContext> {

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var department = context.getDepartment();
        var request = context.getUpdateRequest();

        department.update(request.getName(), request.getDescription());
    }

    @Override
    public String getName() {
        return "更新部門聚合根";
    }
}
