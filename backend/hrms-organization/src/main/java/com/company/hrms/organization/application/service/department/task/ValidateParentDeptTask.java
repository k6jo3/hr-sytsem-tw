package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.exception.ValidationException;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 驗證父部門存在 Task
 */
@Component
@RequiredArgsConstructor
public class ValidateParentDeptTask implements PipelineTask<DepartmentContext> {

    private final IDepartmentRepository departmentRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var request = context.getCreateRequest();

        if (request.getParentId() != null && !request.getParentId().isBlank()) {
            DepartmentId parentId = new DepartmentId(request.getParentId());

            Department parent = departmentRepository.findById(parentId)
                    .orElseThrow(() -> new ValidationException("parentId",
                            "父部門不存在: " + request.getParentId()));

            // 驗證層級
            if (parent.getLevel() >= Department.MAX_LEVEL) {
                throw new DomainException("MAX_LEVEL_EXCEEDED",
                        "已達最大部門層級限制 (" + Department.MAX_LEVEL + " 層)");
            }

            context.setParentId(parentId);
            context.setParentDepartment(parent);
        }
    }

    @Override
    public String getName() {
        return "驗證父部門存在";
    }

    @Override
    public boolean shouldExecute(DepartmentContext context) {
        var request = context.getCreateRequest();
        return request != null && request.getParentId() != null && !request.getParentId().isBlank();
    }
}
