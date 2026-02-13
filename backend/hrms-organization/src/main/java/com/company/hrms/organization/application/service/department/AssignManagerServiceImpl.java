package com.company.hrms.organization.application.service.department;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.department.AssignManagerRequest;
import com.company.hrms.organization.api.response.department.DepartmentDetailResponse;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.application.service.department.task.AssignManagerTask;
import com.company.hrms.organization.application.service.department.task.LoadDeptStatsTask;
import com.company.hrms.organization.application.service.department.task.LoadDeptTask;
import com.company.hrms.organization.application.service.department.task.PublishDepartmentManagerChangedEventTask;
import com.company.hrms.organization.application.service.department.task.SaveDeptTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 指派部門主管 Application Service (Pipeline 模式)
 */
@Service("assignManagerServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssignManagerServiceImpl
                implements CommandApiService<AssignManagerRequest, DepartmentDetailResponse> {

        private final LoadDeptTask loadDeptTask;
        private final AssignManagerTask assignManagerTask;
        private final SaveDeptTask saveDeptTask;
        private final LoadDeptStatsTask loadDeptStatsTask;
        private final PublishDepartmentManagerChangedEventTask publishDepartmentManagerChangedEventTask;

        @Override
        public DepartmentDetailResponse execCommand(AssignManagerRequest request,
                        JWTModel currentUser, String... args) throws Exception {

                String departmentId = args[0];
                log.info("指派部門主管: departmentId={}, managerId={}", departmentId, request.getManagerId());

                DepartmentContext context = new DepartmentContext(departmentId, request);

                BusinessPipeline.start(context)
                                .next(loadDeptTask)
                                .next(assignManagerTask)
                                .next(saveDeptTask)
                                .next(loadDeptStatsTask)
                                .next(publishDepartmentManagerChangedEventTask)
                                .execute();

                log.info("部門主管指派成功: departmentId={}", departmentId);
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
                                                dept.getOrganizationId() != null
                                                                ? dept.getOrganizationId().getValue().toString()
                                                                : null)
                                .organizationName(context.getOrganizationName())
                                .parentId(dept.getParentId() != null ? dept.getParentId().getValue().toString() : null)
                                .parentName(context.getParentName())
                                .level(dept.getLevel())
                                .path(dept.getPath())
                                .managerId(dept.getManagerId() != null ? dept.getManagerId().getValue().toString()
                                                : null)
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
