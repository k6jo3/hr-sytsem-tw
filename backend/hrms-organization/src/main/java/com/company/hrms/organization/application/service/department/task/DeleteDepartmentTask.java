package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 刪除部門 Task
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteDepartmentTask implements PipelineTask<DepartmentContext> {

    private final IDepartmentRepository departmentRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        String deptIdStr = context.getDepartmentId();
        DepartmentId deptId = new DepartmentId(deptIdStr);

        // 刪除部門（軟刪除）
        departmentRepository.delete(deptId);

        log.info("部門刪除成功: departmentId={}", deptIdStr);
    }

    @Override
    public String getName() {
        return "刪除部門";
    }
}
