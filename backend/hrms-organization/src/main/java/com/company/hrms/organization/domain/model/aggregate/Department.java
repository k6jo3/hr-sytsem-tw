package com.company.hrms.organization.domain.model.aggregate;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.DepartmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 部門聚合根
 * 代表組織內的部門，支援多層級結構 (最多5層)
 */
@Getter
@Builder
public class Department {

    /**
     * 部門層級上限
     */
    public static final int MAX_LEVEL = 5;

    /**
     * 部門 ID
     */
    private final DepartmentId id;

    /**
     * 部門代碼
     */
    private String departmentCode;

    /**
     * 部門名稱
     */
    private String departmentName;

    /**
     * 所屬組織 ID
     */
    private UUID organizationId;

    /**
     * 上級部門 ID
     */
    private UUID parentDepartmentId;

    /**
     * 部門層級 (1-5)
     */
    private Integer level;

    /**
     * 部門主管 ID
     */
    private UUID managerId;

    /**
     * 顯示順序
     */
    private Integer displayOrder;

    /**
     * 部門狀態
     */
    private DepartmentStatus status;

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    // ==================== 工廠方法 ====================

    /**
     * 建立一級部門 (直接隸屬於組織)
     * @param departmentCode 部門代碼
     * @param departmentName 部門名稱
     * @param organizationId 組織 ID
     * @return 新的 Department 實例
     */
    public static Department createTopLevel(String departmentCode, String departmentName, UUID organizationId) {
        validateDepartmentCode(departmentCode);
        validateDepartmentName(departmentName);

        if (organizationId == null) {
            throw new DomainException("ORG_ID_REQUIRED", "組織 ID 不可為空");
        }

        return Department.builder()
                .id(DepartmentId.generate())
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .organizationId(organizationId)
                .level(1)
                .displayOrder(0)
                .status(DepartmentStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 建立子部門
     * @param departmentCode 部門代碼
     * @param departmentName 部門名稱
     * @param organizationId 組織 ID
     * @param parentDepartmentId 上級部門 ID
     * @param parentLevel 上級部門層級
     * @return 新的 Department 實例
     */
    public static Department createSubDepartment(String departmentCode, String departmentName,
                                                  UUID organizationId, UUID parentDepartmentId, int parentLevel) {
        validateDepartmentCode(departmentCode);
        validateDepartmentName(departmentName);

        if (organizationId == null) {
            throw new DomainException("ORG_ID_REQUIRED", "組織 ID 不可為空");
        }

        if (parentDepartmentId == null) {
            throw new DomainException("PARENT_DEPT_REQUIRED", "子部門必須指定上級部門");
        }

        int newLevel = parentLevel + 1;
        if (newLevel > MAX_LEVEL) {
            throw new DomainException("DEPT_LEVEL_EXCEEDED", "部門層級不可超過" + MAX_LEVEL + "層");
        }

        return Department.builder()
                .id(DepartmentId.generate())
                .departmentCode(departmentCode)
                .departmentName(departmentName)
                .organizationId(organizationId)
                .parentDepartmentId(parentDepartmentId)
                .level(newLevel)
                .displayOrder(0)
                .status(DepartmentStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 更新部門資訊
     * @param departmentName 部門名稱
     */
    public void updateInfo(String departmentName) {
        if (departmentName != null && !departmentName.isBlank()) {
            validateDepartmentName(departmentName);
            this.departmentName = departmentName;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 指派主管
     * @param employeeId 員工 ID
     */
    public void assignManager(UUID employeeId) {
        this.managerId = employeeId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 移除主管
     */
    public void removeManager() {
        this.managerId = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 調整顯示順序
     * @param displayOrder 新的順序值
     */
    public void reorder(int displayOrder) {
        this.displayOrder = displayOrder;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用部門
     * @throws DomainException 若有啟用中子部門或在職員工則無法停用
     */
    public void deactivate() {
        if (this.status == DepartmentStatus.INACTIVE) {
            throw new DomainException("DEPT_ALREADY_INACTIVE", "部門已停用");
        }
        this.status = DepartmentStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 啟用部門
     * @throws DomainException 若上級部門已停用則無法啟用
     */
    public void activate() {
        if (this.status == DepartmentStatus.ACTIVE) {
            throw new DomainException("DEPT_ALREADY_ACTIVE", "部門已啟用");
        }
        this.status = DepartmentStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 是否為一級部門
     * @return 是否為一級部門
     */
    public boolean isTopLevel() {
        return this.level == 1;
    }

    /**
     * 是否可新增子部門
     * @return 是否可新增
     */
    public boolean canAddSubDepartment() {
        return this.level < MAX_LEVEL;
    }

    /**
     * 是否啟用中
     * @return 是否啟用中
     */
    public boolean isActive() {
        return this.status.isActive();
    }

    /**
     * 是否有主管
     * @return 是否有主管
     */
    public boolean hasManager() {
        return this.managerId != null;
    }

    // ==================== 驗證方法 ====================

    private static void validateDepartmentCode(String code) {
        if (code == null || code.isBlank()) {
            throw new DomainException("DEPT_CODE_REQUIRED", "部門代碼不可為空");
        }
        if (code.length() > 50) {
            throw new DomainException("DEPT_CODE_TOO_LONG", "部門代碼長度不可超過50字元");
        }
    }

    private static void validateDepartmentName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("DEPT_NAME_REQUIRED", "部門名稱不可為空");
        }
        if (name.length() > 255) {
            throw new DomainException("DEPT_NAME_TOO_LONG", "部門名稱長度不可超過255字元");
        }
    }
}
