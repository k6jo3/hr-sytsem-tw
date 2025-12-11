package com.company.hrms.iam.infrastructure.mapper;

import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.model.valueobject.RoleStatus;
import com.company.hrms.iam.infrastructure.po.RolePO;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Role Mapper
 * PO 與 Domain Object 之間的轉換
 */
@Component
public class RoleMapper {

    /**
     * PO 轉換為 Domain Object
     */
    public Role toDomain(RolePO po, List<String> permissionIds) {
        if (po == null) {
            return null;
        }

        List<PermissionId> permissions = permissionIds != null
                ? permissionIds.stream()
                    .map(PermissionId::of)
                    .collect(Collectors.toList())
                : List.of();

        return Role.reconstitute(
                po.getRoleId(),
                po.getRoleName(),
                po.getRoleCode(),
                po.getDescription(),
                po.getTenantId(),
                po.getIsSystemRole() != null && po.getIsSystemRole(),
                RoleStatus.valueOf(po.getStatus()),
                permissions,
                toLocalDateTime(po.getCreatedAt()),
                toLocalDateTime(po.getUpdatedAt())
        );
    }

    /**
     * Domain Object 轉換為 PO
     */
    public RolePO toPO(Role role) {
        if (role == null) {
            return null;
        }

        return RolePO.builder()
                .roleId(role.getId().getValue())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .description(role.getDescription())
                .tenantId(role.getTenantId())
                .isSystemRole(role.isSystemRole())
                .status(role.getStatus().name())
                .createdAt(toTimestamp(role.getCreatedAt()))
                .updatedAt(toTimestamp(role.getUpdatedAt()))
                .build();
    }

    /**
     * Timestamp 轉換為 LocalDateTime
     */
    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    /**
     * LocalDateTime 轉換為 Timestamp
     */
    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }
}
