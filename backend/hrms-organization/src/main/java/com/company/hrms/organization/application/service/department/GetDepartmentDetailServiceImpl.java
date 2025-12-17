package com.company.hrms.organization.application.service.department;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.department.DepartmentDetailResponse;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.aggregate.Organization;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 取得部門詳情服務實作
 */
@Service("getDepartmentDetailServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetDepartmentDetailServiceImpl
        implements QueryApiService<Void, DepartmentDetailResponse> {

    private final IDepartmentRepository departmentRepository;
    private final IOrganizationRepository organizationRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public DepartmentDetailResponse getResponse(Void request,
                                                JWTModel currentUser,
                                                String... args) throws Exception {
        String departmentId = args[0];
        log.info("Getting department detail: {}", departmentId);

        Department department = departmentRepository.findById(new DepartmentId(departmentId))
                .orElseThrow(() -> new IllegalArgumentException("部門不存在: " + departmentId));

        // 取得組織名稱
        String organizationName = organizationRepository.findById(department.getOrganizationId())
                .map(Organization::getName)
                .orElse(null);

        // 取得父部門名稱
        String parentName = null;
        if (department.getParentId() != null) {
            parentName = departmentRepository.findById(department.getParentId())
                    .map(Department::getName)
                    .orElse(null);
        }

        // 取得主管姓名
        String managerName = null;
        if (department.getManagerId() != null) {
            managerName = employeeRepository.findById(department.getManagerId())
                    .map(Employee::getFullName)
                    .orElse(null);
        }

        // 計算員工數和子部門數
        int childDepartmentCount = departmentRepository.countByParentId(department.getId());

        return DepartmentDetailResponse.builder()
                .departmentId(department.getId().getValue())
                .code(department.getCode())
                .name(department.getName())
                .nameEn(department.getNameEn())
                .organizationId(department.getOrganizationId().getValue())
                .organizationName(organizationName)
                .parentId(department.getParentId() != null ? department.getParentId().getValue() : null)
                .parentName(parentName)
                .level(department.getLevel())
                .path(department.getPath())
                .managerId(department.getManagerId() != null ? department.getManagerId().getValue() : null)
                .managerName(managerName)
                .status(department.getStatus().name())
                .statusDisplay(department.getStatus().getDisplayName())
                .sortOrder(department.getSortOrder())
                .description(department.getDescription())
                .childDepartmentCount(childDepartmentCount)
                .build();
    }
}
