package com.company.hrms.organization.application.service.department;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.response.department.DepartmentDetailResponse;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.application.service.department.task.CheckDeptCanDeactivateTask;
import com.company.hrms.organization.application.service.department.task.DeactivateDeptTask;
import com.company.hrms.organization.application.service.department.task.LoadDeptTask;
import com.company.hrms.organization.application.service.department.task.SaveDeptTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 停用部門 Application Service (Pipeline 模式)
 */
@Service("deactivateDepartmentServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeactivateDepartmentServiceImpl
        implements CommandApiService<Object, DepartmentDetailResponse> {

    private final LoadDeptTask loadDeptTask;
    private final CheckDeptCanDeactivateTask checkDeptCanDeactivateTask;
    private final DeactivateDeptTask deactivateDeptTask;
    private final SaveDeptTask saveDeptTask;

    @Override
    public DepartmentDetailResponse execCommand(Object request,
            JWTModel currentUser, String... args) throws Exception {

        String departmentId = args[0];
        log.info("停用部門: id={}", departmentId);

        DepartmentContext context = new DepartmentContext(departmentId);

        BusinessPipeline.start(context)
                .next(loadDeptTask)
                .next(checkDeptCanDeactivateTask)
                .next(deactivateDeptTask)
                .next(saveDeptTask)
                .execute();

        log.info("部門停用成功: id={}", departmentId);
        return buildResponse(context);
    }

    private DepartmentDetailResponse buildResponse(DepartmentContext context) {
        var dept = context.getDepartment();

        return DepartmentDetailResponse.builder()
                .departmentId(dept.getId().getValue().toString())
                .code(dept.getCode())
                .name(dept.getName())
                .status(dept.getStatus().name())
                .statusDisplay(dept.getStatus().getDisplayName())
                .build();
    }
}
