package com.company.hrms.organization.application.service.department;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.model.PageResponse;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.department.DepartmentListItemResponse;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得子部門列表服務實作
 */
@Service("getSubDepartmentsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetSubDepartmentsServiceImpl
        implements QueryApiService<Object, PageResponse<DepartmentListItemResponse>> {

    private final IDepartmentRepository departmentRepository;

    @Override
    public PageResponse<DepartmentListItemResponse> getResponse(Object request, JWTModel currentUser, String... args)
            throws Exception {

        String parentId = args[0];
        log.info("Getting sub-departments for parentId: {}", parentId);

        List<Department> subDepts = departmentRepository.findByParentId(new DepartmentId(parentId));

        List<DepartmentListItemResponse> items = subDepts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<DepartmentListItemResponse>builder()
                .items(items)
                .total((long) items.size())
                .page(1)
                .size(items.size())
                .totalPages(1)
                .build();
    }

    private DepartmentListItemResponse toResponse(Department department) {
        return DepartmentListItemResponse.builder()
                .departmentId(department.getId().getValue().toString())
                .code(department.getCode())
                .name(department.getName())
                .level(department.getLevel())
                .sortOrder(department.getSortOrder())
                .organizationId(
                        department.getOrganizationId() != null ? department.getOrganizationId().toString() : null)
                .parentId(department.getParentId() != null ? department.getParentId().toString() : null)
                .managerId(department.getManagerId() != null ? department.getManagerId().toString() : null)
                .status(department.getStatus().name())
                .statusDisplay(department.getStatus().getDisplayName())
                .build();
    }
}
