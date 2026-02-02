package com.company.hrms.organization.application.service.department;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.application.service.department.task.CheckChildDepartmentsTask;
import com.company.hrms.organization.application.service.department.task.CheckDepartmentEmployeesTask;
import com.company.hrms.organization.application.service.department.task.DeleteDepartmentTask;
import com.company.hrms.organization.application.service.department.task.LoadDeptTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 刪除部門 Application Service
 * 
 * <p>
 * 對應 API: DELETE /api/v1/departments/{id}
 * </p>
 * 
 * <p>
 * 業務邏輯:
 * </p>
 * <ol>
 * <li>驗證部門存在</li>
 * <li>檢查部門下是否有在職員工</li>
 * <li>檢查部門下是否有子部門</li>
 * <li>刪除部門（軟刪除）</li>
 * </ol>
 */
@Service("deleteDepartmentServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeleteDepartmentServiceImpl implements CommandApiService<Object, Void> {

    private final LoadDeptTask loadDeptTask;
    private final CheckDepartmentEmployeesTask checkDepartmentEmployeesTask;
    private final CheckChildDepartmentsTask checkChildDepartmentsTask;
    private final DeleteDepartmentTask deleteDepartmentTask;

    @Override
    public Void execCommand(Object request, JWTModel currentUser, String... args)
            throws Exception {
        String departmentId = args[0];
        log.info("刪除部門: departmentId={}", departmentId);

        DepartmentContext context = new DepartmentContext(departmentId);

        BusinessPipeline.start(context)
                .next(loadDeptTask)
                .next(checkDepartmentEmployeesTask)
                .next(checkChildDepartmentsTask)
                .next(deleteDepartmentTask)
                .execute();

        return null;
    }
}
