package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.DepartmentStatus;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;

import lombok.RequiredArgsConstructor;

/**
 * 建立部門聚合根 Task
 */
@Component
@RequiredArgsConstructor
public class CreateDeptAggregateTask implements PipelineTask<DepartmentContext> {

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var request = context.getCreateRequest();
        var parent = context.getParentDepartment();

        // 計算層級和路徑
        int level = 1;
        String path = "/" + request.getCode();

        if (parent != null) {
            level = parent.getLevel() + 1;
            path = parent.getPath() + "/" + request.getCode();
        }

        // 取得主管 ID
        EmployeeId managerId = null;
        if (context.getManager() != null) {
            managerId = context.getManager().getId();
        }

        // 建立部門
        Department department = Department.reconstitute(
                DepartmentId.generate(),
                request.getCode(),
                request.getName(),
                request.getNameEn(),
                context.getOrganizationId(),
                context.getParentId(),
                level,
                path,
                managerId,
                DepartmentStatus.ACTIVE,
                request.getSortOrder() != null ? request.getSortOrder() : 0,
                request.getDescription());

        context.setDepartment(department);
    }

    @Override
    public String getName() {
        return "建立部門聚合根";
    }
}
