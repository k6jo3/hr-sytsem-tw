package com.company.hrms.organization.domain.model.aggregate;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.DepartmentStatus;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 部門聚合根
 * 代表組織內的部門，支援多層級結構 (最多5層)
 */
@Getter
@Builder
@EqualsAndHashCode(of = "id")
public class Department {

    public static final int MAX_LEVEL = 5;

    private final DepartmentId id;
    private String code;
    private String name;
    private String nameEn;
    private OrganizationId organizationId;
    private DepartmentId parentId;
    private Integer level;
    private String path;
    private EmployeeId managerId;
    private DepartmentStatus status;
    private Integer sortOrder;
    private String description;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== 工廠方法 ====================

    /**
     * 建立部門
     */
    public static Department create(UUID orgId, String code, String name, String parentIdStr) {
        validateCode(code);
        validateName(name);

        if (orgId == null) {
            throw new DomainException("ORG_ID_REQUIRED", "組織 ID 不可為空");
        }

        return Department.builder()
                .id(DepartmentId.generate())
                .code(code)
                .name(name)
                .organizationId(new OrganizationId(orgId.toString()))
                .parentId(parentIdStr != null ? new DepartmentId(parentIdStr) : null)
                .level(parentIdStr == null ? 1 : 2)
                .path("/" + code)
                .status(DepartmentStatus.ACTIVE)
                .sortOrder(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 從持久層還原
     */
    public static Department reconstitute(
            DepartmentId id,
            String code,
            String name,
            String nameEn,
            OrganizationId organizationId,
            DepartmentId parentId,
            Integer level,
            String path,
            EmployeeId managerId,
            DepartmentStatus status,
            Integer sortOrder,
            String description) {

        return Department.builder()
                .id(id)
                .code(code)
                .name(name)
                .nameEn(nameEn)
                .organizationId(organizationId)
                .parentId(parentId)
                .level(level)
                .path(path)
                .managerId(managerId)
                .status(status)
                .sortOrder(sortOrder)
                .description(description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 更新部門資訊
     */
    public void update(String name, String description) {
        if (this.status == DepartmentStatus.INACTIVE) {
            throw new DomainException("DEPT_DEACTIVATED", "已停用部門無法更新");
        }
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 指派主管
     */
    public void assignManager(UUID employeeId) {
        this.managerId = employeeId != null ? new EmployeeId(employeeId.toString()) : null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 移動到新父部門
     */
    public void moveTo(UUID newParentId) {
        this.parentId = newParentId != null ? new DepartmentId(newParentId.toString()) : null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新排序順序
     */
    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用部門
     */
    public void deactivate() {
        if (this.status == DepartmentStatus.INACTIVE) {
            throw new DomainException("ALREADY_DEACTIVATED", "部門已停用");
        }
        this.status = DepartmentStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 啟用部門
     */
    public void activate() {
        if (this.status == DepartmentStatus.ACTIVE) {
            throw new DomainException("ALREADY_ACTIVE", "部門已啟用");
        }
        this.status = DepartmentStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == DepartmentStatus.ACTIVE;
    }

    public boolean isTopLevel() {
        return this.level == 1;
    }

    public boolean canAddSubDepartment() {
        return this.level < MAX_LEVEL;
    }

    // ==================== 驗證方法 ====================

    private static void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new DomainException("DEPT_CODE_REQUIRED", "部門代碼不可為空");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("DEPT_NAME_REQUIRED", "部門名稱不可為空");
        }
    }
}
