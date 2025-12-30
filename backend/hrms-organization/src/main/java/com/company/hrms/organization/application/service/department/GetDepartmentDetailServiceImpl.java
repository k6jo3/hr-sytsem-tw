package com.company.hrms.organization.application.service.department;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.department.DepartmentDetailResponse;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.application.service.department.task.LoadDeptStatsTask;
import com.company.hrms.organization.application.service.department.task.LoadDeptTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢部門詳情 Application Service (Pipeline 模式)
 * 
 * <p>
 * 對應 API: GET /api/v1/departments/{id}
 * </p>
 */
@Service("getDepartmentDetailServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetDepartmentDetailServiceImpl
        implements QueryApiService<Object, DepartmentDetailResponse> {

    private final LoadDeptTask loadDeptTask;
    private final LoadDeptStatsTask loadDeptStatsTask;

    @Override
    public DepartmentDetailResponse getResponse(Object request, JWTModel currentUser, String... args)
            throws Exception {

        String departmentId = args[0];
        log.info("查詢部門詳情: id={}", departmentId);

        DepartmentContext context = new DepartmentContext(departmentId);

        BusinessPipeline.start(context)
                .next(loadDeptTask)
                .next(loadDeptStatsTask)
                .execute();

        return buildResponse(context);
    }

    private DepartmentDetailResponse buildResponse(DepartmentContext context) {
        var dept = context.getDepartment();

        return DepartmentDetailResponse.builder()
                .departmentId(dept.getId().getValue().toString())
                .code(dept.getCode())
                .name(dept.getName())
                .nameEn(dept.getNameEn())
                .organizationId(
                        dept.getOrganizationId() != null ? dept.getOrganizationId().getValue().toString() : null)
                .organizationName(context.getOrganizationName())
                .parentId(dept.getParentId() != null ? dept.getParentId().getValue().toString() : null)
                .parentName(context.getParentName())
                .level(dept.getLevel())
                .path(dept.getPath())
                .managerId(dept.getManagerId() != null ? dept.getManagerId().getValue().toString() : null)
                .managerName(context.getManagerName())
                .status(dept.getStatus().name())
                .statusDisplay(dept.getStatus().getDisplayName())
                .sortOrder(dept.getSortOrder())
                .description(dept.getDescription())
                .employeeCount(context.getEmployeeCount())
                .childDepartmentCount(context.getChildDepartmentCount())
                .build();
    }
}
