package com.company.hrms.organization.application.service.department;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.department.UpdateDepartmentRequest;
import com.company.hrms.organization.api.response.department.DepartmentDetailResponse;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 更新部門服務實作
 */
@Service("updateDepartmentServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateDepartmentServiceImpl
        implements CommandApiService<UpdateDepartmentRequest, DepartmentDetailResponse> {

    private final IDepartmentRepository departmentRepository;

    @Override
    public DepartmentDetailResponse execCommand(UpdateDepartmentRequest request,
                                                 JWTModel currentUser,
                                                 String... args) throws Exception {
        String departmentId = args[0];
        log.info("Updating department: {}", departmentId);

        Department department = departmentRepository.findById(new DepartmentId(departmentId))
                .orElseThrow(() -> new IllegalArgumentException("部門不存在: " + departmentId));

        // 更新部門資訊
        department.update(
                request.getName() != null ? request.getName() : department.getName(),
                request.getNameEn() != null ? request.getNameEn() : department.getNameEn(),
                request.getDescription() != null ? request.getDescription() : department.getDescription()
        );

        // 儲存更新
        departmentRepository.save(department);

        log.info("Department updated successfully: {}", departmentId);

        return buildDepartmentDetailResponse(department);
    }

    private DepartmentDetailResponse buildDepartmentDetailResponse(Department department) {
        return DepartmentDetailResponse.builder()
                .departmentId(department.getId().getValue())
                .code(department.getCode())
                .name(department.getName())
                .nameEn(department.getNameEn())
                .organizationId(department.getOrganizationId().getValue())
                .parentId(department.getParentId() != null ? department.getParentId().getValue() : null)
                .level(department.getLevel())
                .path(department.getPath())
                .managerId(department.getManagerId() != null ? department.getManagerId().getValue() : null)
                .status(department.getStatus().name())
                .statusDisplay(department.getStatus().getDisplayName())
                .sortOrder(department.getSortOrder())
                .description(department.getDescription())
                .build();
    }
}
