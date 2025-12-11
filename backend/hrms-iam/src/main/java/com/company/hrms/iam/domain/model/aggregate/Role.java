package com.company.hrms.iam.domain.model.aggregate;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.model.valueobject.RoleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Role 聚合根
 * IAM 領域的角色聚合根，封裝角色相關的業務邏輯
 */
@Getter
@Builder
public class Role {

    /**
     * 角色 ID
     */
    private final RoleId id;

    /**
     * 角色名稱
     */
    private String roleName;

    /**
     * 角色代碼 (如 ADMIN, HR_ADMIN, EMPLOYEE)
     */
    private final String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 租戶 ID (NULL 表示系統角色)
     */
    private final String tenantId;

    /**
     * 是否為系統內建角色
     */
    private final boolean systemRole;

    /**
     * 角色狀態
     */
    private RoleStatus status;

    /**
     * 權限列表
     */
    @Builder.Default
    private List<PermissionId> permissionIds = new ArrayList<>();

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
     * 建立新角色 (租戶角色)
     * @param roleName 角色名稱
     * @param roleCode 角色代碼
     * @param description 描述
     * @param tenantId 租戶 ID
     * @return 新的 Role 實例
     */
    public static Role create(String roleName, String roleCode, String description, String tenantId) {
        validateRoleCode(roleCode);
        validateRoleName(roleName);

        return Role.builder()
                .id(RoleId.generate())
                .roleName(roleName)
                .roleCode(roleCode.toUpperCase())
                .description(description)
                .tenantId(tenantId)
                .systemRole(false)
                .status(RoleStatus.ACTIVE)
                .permissionIds(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 建立系統角色
     * @param roleName 角色名稱
     * @param roleCode 角色代碼
     * @param description 描述
     * @return 新的系統 Role 實例
     */
    public static Role createSystemRole(String roleName, String roleCode, String description) {
        validateRoleCode(roleCode);
        validateRoleName(roleName);

        return Role.builder()
                .id(RoleId.generate())
                .roleName(roleName)
                .roleCode(roleCode.toUpperCase())
                .description(description)
                .tenantId(null)
                .systemRole(true)
                .status(RoleStatus.ACTIVE)
                .permissionIds(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 從持久層重建 Role
     */
    public static Role reconstitute(String id, String roleName, String roleCode, String description,
                                    String tenantId, boolean systemRole, RoleStatus status,
                                    List<PermissionId> permissionIds,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        return Role.builder()
                .id(RoleId.of(id))
                .roleName(roleName)
                .roleCode(roleCode)
                .description(description)
                .tenantId(tenantId)
                .systemRole(systemRole)
                .status(status)
                .permissionIds(permissionIds != null ? new ArrayList<>(permissionIds) : new ArrayList<>())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 更新角色資訊
     * @param roleName 新的角色名稱
     * @param description 新的描述
     */
    public void update(String roleName, String description) {
        if (this.systemRole) {
            throw new DomainException("SYSTEM_ROLE_IMMUTABLE", "系統角色不可修改");
        }

        if (roleName != null && !roleName.isBlank()) {
            validateRoleName(roleName);
            this.roleName = roleName;
        }
        if (description != null) {
            this.description = description;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 啟用角色
     */
    public void activate() {
        if (this.status == RoleStatus.DELETED) {
            throw new DomainException("ROLE_DELETED", "無法啟用已刪除的角色");
        }
        this.status = RoleStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用角色
     */
    public void deactivate() {
        if (this.systemRole) {
            throw new DomainException("SYSTEM_ROLE_IMMUTABLE", "系統角色不可停用");
        }
        this.status = RoleStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 刪除角色 (軟刪除)
     */
    public void delete() {
        if (this.systemRole) {
            throw new DomainException("SYSTEM_ROLE_IMMUTABLE", "系統角色不可刪除");
        }
        this.status = RoleStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 指派權限
     * @param permissionId 權限 ID
     */
    public void assignPermission(PermissionId permissionId) {
        Objects.requireNonNull(permissionId, "Permission ID cannot be null");
        if (!this.permissionIds.contains(permissionId)) {
            this.permissionIds.add(permissionId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 批量指派權限
     * @param permissionIds 權限 ID 列表
     */
    public void assignPermissions(List<PermissionId> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        for (PermissionId permissionId : permissionIds) {
            assignPermission(permissionId);
        }
    }

    /**
     * 移除權限
     * @param permissionId 權限 ID
     */
    public void removePermission(PermissionId permissionId) {
        if (this.permissionIds.remove(permissionId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 清除所有權限
     */
    public void clearPermissions() {
        if (!this.permissionIds.isEmpty()) {
            this.permissionIds.clear();
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 檢查是否擁有指定權限
     * @param permissionId 權限 ID
     * @return 是否擁有
     */
    public boolean hasPermission(PermissionId permissionId) {
        return this.permissionIds.contains(permissionId);
    }

    /**
     * 檢查角色是否啟用
     * @return 是否啟用
     */
    public boolean isActive() {
        return this.status == RoleStatus.ACTIVE;
    }

    /**
     * 取得權限 ID 列表 (不可變)
     * @return 權限 ID 列表
     */
    public List<PermissionId> getPermissionIds() {
        return Collections.unmodifiableList(this.permissionIds);
    }

    /**
     * 取得權限數量
     * @return 權限數量
     */
    public int getPermissionCount() {
        return this.permissionIds.size();
    }

    // ==================== 驗證方法 ====================

    private static void validateRoleCode(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            throw new DomainException("ROLE_CODE_REQUIRED", "角色代碼不可為空");
        }
        if (!roleCode.matches("^[A-Z][A-Z0-9_]*$")) {
            throw new DomainException("INVALID_ROLE_CODE", "角色代碼格式不正確，必須以大寫字母開頭，只能包含大寫字母、數字和底線");
        }
        if (roleCode.length() > 50) {
            throw new DomainException("ROLE_CODE_TOO_LONG", "角色代碼長度不可超過 50 個字元");
        }
    }

    private static void validateRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new DomainException("ROLE_NAME_REQUIRED", "角色名稱不可為空");
        }
        if (roleName.length() > 50) {
            throw new DomainException("ROLE_NAME_TOO_LONG", "角色名稱長度不可超過 50 個字元");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
