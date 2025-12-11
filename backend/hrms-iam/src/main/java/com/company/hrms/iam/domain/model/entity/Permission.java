package com.company.hrms.iam.domain.model.entity;

import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Permission 實體
 * 代表系統權限
 */
@Getter
@Builder
public class Permission {

    /**
     * 權限 ID
     */
    private final PermissionId id;

    /**
     * 權限代碼 (格式: resource:action, 如 user:create)
     */
    private final String permissionCode;

    /**
     * 權限名稱
     */
    private String permissionName;

    /**
     * 權限描述
     */
    private String description;

    /**
     * 資源名稱 (如 user, role, employee)
     */
    private final String resource;

    /**
     * 操作名稱 (如 create, read, update, delete)
     */
    private final String action;

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    // ==================== 工廠方法 ====================

    /**
     * 建立新權限
     * @param permissionCode 權限代碼 (格式: resource:action)
     * @param permissionName 權限名稱
     * @param description 描述
     * @return Permission
     */
    public static Permission create(String permissionCode, String permissionName, String description) {
        if (permissionCode == null || !permissionCode.contains(":")) {
            throw new IllegalArgumentException("Permission code must be in format 'resource:action'");
        }

        String[] parts = permissionCode.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Permission code must be in format 'resource:action'");
        }

        return Permission.builder()
                .id(PermissionId.generate())
                .permissionCode(permissionCode)
                .permissionName(permissionName)
                .description(description)
                .resource(parts[0])
                .action(parts[1])
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 從持久層重建 Permission
     * @param id 權限 ID
     * @param permissionCode 權限代碼
     * @param permissionName 權限名稱
     * @param description 描述
     * @param resource 資源
     * @param action 操作
     * @param createdAt 建立時間
     * @return Permission
     */
    public static Permission reconstitute(String id, String permissionCode, String permissionName,
                                          String description, String resource, String action,
                                          LocalDateTime createdAt) {
        return Permission.builder()
                .id(PermissionId.of(id))
                .permissionCode(permissionCode)
                .permissionName(permissionName)
                .description(description)
                .resource(resource)
                .action(action)
                .createdAt(createdAt)
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 更新權限資訊
     * @param permissionName 新的權限名稱
     * @param description 新的描述
     */
    public void update(String permissionName, String description) {
        if (permissionName != null && !permissionName.isBlank()) {
            this.permissionName = permissionName;
        }
        if (description != null) {
            this.description = description;
        }
    }

    /**
     * 檢查是否為指定資源的權限
     * @param resource 資源名稱
     * @return 是否為該資源的權限
     */
    public boolean isForResource(String resource) {
        return this.resource.equals(resource);
    }

    /**
     * 檢查是否為指定操作的權限
     * @param action 操作名稱
     * @return 是否為該操作的權限
     */
    public boolean isForAction(String action) {
        return this.action.equals(action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
