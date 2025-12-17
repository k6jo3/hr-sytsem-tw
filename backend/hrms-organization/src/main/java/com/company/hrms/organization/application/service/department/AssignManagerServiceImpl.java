package com.company.hrms.organization.application.service.department;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.department.AssignManagerRequest;
import com.company.hrms.organization.api.response.department.DepartmentDetailResponse;
import com.company.hrms.organization.domain.event.DepartmentManagerChangedEvent;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 指派部門主管服務實作
 */
@Service("assignManagerServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AssignManagerServiceImpl
        implements CommandApiService<AssignManagerRequest, DepartmentDetailResponse> {

    private final IDepartmentRepository departmentRepository;
    private final IEmployeeRepository employeeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public DepartmentDetailResponse execCommand(AssignManagerRequest request,
                                                 JWTModel currentUser,
                                                 String... args) throws Exception {
        String departmentId = args[0];
        log.info("Assigning manager {} to department: {}", request.getManagerId(), departmentId);

        Department department = departmentRepository.findById(new DepartmentId(departmentId))
                .orElseThrow(() -> new IllegalArgumentException("部門不存在: " + departmentId));

        // 驗證員工存在
        EmployeeId newManagerId = new EmployeeId(request.getManagerId());
        Employee newManager = employeeRepository.findById(newManagerId)
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + request.getManagerId()));

        // 記錄舊主管
        String oldManagerId = department.getManagerId() != null
                ? department.getManagerId().getValue()
                : null;
        String oldManagerName = null;
        if (oldManagerId != null) {
            oldManagerName = employeeRepository.findById(department.getManagerId())
                    .map(Employee::getFullName)
                    .orElse(null);
        }

        // 指派新主管
        department.assignManager(newManagerId);

        // 儲存更新
        departmentRepository.save(department);

        // 發布領域事件
        eventPublisher.publishEvent(new DepartmentManagerChangedEvent(
                departmentId,
                department.getCode(),
                department.getName(),
                oldManagerId,
                oldManagerName,
                request.getManagerId(),
                newManager.getFullName()
        ));

        log.info("Manager assigned successfully: {} -> {}", departmentId, request.getManagerId());

        return DepartmentDetailResponse.builder()
                .departmentId(department.getId().getValue())
                .code(department.getCode())
                .name(department.getName())
                .nameEn(department.getNameEn())
                .organizationId(department.getOrganizationId().getValue())
                .parentId(department.getParentId() != null ? department.getParentId().getValue() : null)
                .level(department.getLevel())
                .path(department.getPath())
                .managerId(newManagerId.getValue())
                .managerName(newManager.getFullName())
                .status(department.getStatus().name())
                .statusDisplay(department.getStatus().getDisplayName())
                .sortOrder(department.getSortOrder())
                .description(department.getDescription())
                .build();
    }
}
