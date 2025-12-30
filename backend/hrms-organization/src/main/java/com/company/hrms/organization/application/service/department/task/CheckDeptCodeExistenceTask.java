package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.ResourceAlreadyExistsException;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 檢查部門代碼存在 Task
 */
@Component
@RequiredArgsConstructor
public class CheckDeptCodeExistenceTask implements PipelineTask<DepartmentContext> {

    private final IDepartmentRepository departmentRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var request = context.getCreateRequest();

        if (departmentRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("DEPT_CODE_EXISTS",
                    "部門代碼已存在: " + request.getCode());
        }
    }

    @Override
    public String getName() {
        return "檢查部門代碼唯一性";
    }
}
