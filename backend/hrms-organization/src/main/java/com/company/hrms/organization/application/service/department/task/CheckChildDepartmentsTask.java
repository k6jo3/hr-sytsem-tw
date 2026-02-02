package com.company.hrms.organization.application.service.department.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;

/**
 * 檢查部門下是否有子部門 Task
 */
@Component
@RequiredArgsConstructor
public class CheckChildDepartmentsTask implements PipelineTask<DepartmentContext> {

    private final IDepartmentRepository departmentRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        String deptIdStr = context.getDepartmentId();
        DepartmentId deptId = new DepartmentId(deptIdStr);

        // 檢查是否有子部門
        int childCount = departmentRepository.countByParentId(deptId);
        if (childCount > 0) {
            throw new DomainException("CANNOT_DELETE_DEPT",
                    "無法刪除部門，尚有 " + childCount + " 個子部門");
        }
    }

    @Override
    public String getName() {
        return "檢查子部門";
    }
}
