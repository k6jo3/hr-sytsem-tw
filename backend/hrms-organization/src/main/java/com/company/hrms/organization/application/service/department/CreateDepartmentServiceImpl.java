package com.company.hrms.organization.application.service.department;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.department.CreateDepartmentRequest;
import com.company.hrms.organization.api.response.department.CreateDepartmentResponse;
import com.company.hrms.organization.domain.event.DepartmentCreatedEvent;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 新增部門服務實作
 */
@Service("createDepartmentServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateDepartmentServiceImpl
        implements CommandApiService<CreateDepartmentRequest, CreateDepartmentResponse> {

    private final IDepartmentRepository departmentRepository;
    private final IOrganizationRepository organizationRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final int MAX_LEVEL = 5;

    @Override
    public CreateDepartmentResponse execCommand(CreateDepartmentRequest request,
                                                 JWTModel currentUser,
                                                 String... args) throws Exception {
        log.info("Creating department: {}", request.getCode());

        // 驗證組織存在
        OrganizationId organizationId = new OrganizationId(request.getOrganizationId());
        if (!organizationRepository.existsById(organizationId)) {
            throw new IllegalArgumentException("組織不存在: " + request.getOrganizationId());
        }

        // 驗證部門代碼唯一性
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("部門代碼已存在: " + request.getCode());
        }

        // 計算層級和路徑
        int level = 1;
        String path = "";
        DepartmentId parentId = null;

        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            parentId = new DepartmentId(request.getParentId());
            Department parent = departmentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("父部門不存在: " + request.getParentId()));

            level = parent.getLevel() + 1;
            if (level > MAX_LEVEL) {
                throw new IllegalArgumentException("部門層級不可超過 " + MAX_LEVEL + " 層");
            }

            path = parent.getPath();
        }

        // 建立部門
        EmployeeId managerId = request.getManagerId() != null && !request.getManagerId().isEmpty()
                ? new EmployeeId(request.getManagerId())
                : null;

        Department department = Department.create(
                request.getCode(),
                request.getName(),
                request.getNameEn(),
                organizationId,
                parentId,
                level,
                managerId,
                request.getSortOrder() != null ? request.getSortOrder() : 0,
                request.getDescription()
        );

        // 設定路徑
        department.setPath(path.isEmpty()
                ? "/" + department.getId().getValue()
                : path + "/" + department.getId().getValue());

        // 儲存部門
        departmentRepository.save(department);

        // 發布領域事件
        eventPublisher.publishEvent(new DepartmentCreatedEvent(
                department.getId().getValue(),
                department.getCode(),
                department.getName(),
                department.getOrganizationId().getValue()
        ));

        log.info("Department created successfully: {}", department.getId().getValue());

        return CreateDepartmentResponse.success(
                department.getId().getValue(),
                department.getCode(),
                department.getName(),
                department.getLevel()
        );
    }
}
