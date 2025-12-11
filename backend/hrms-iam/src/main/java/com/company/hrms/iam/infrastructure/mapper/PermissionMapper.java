package com.company.hrms.iam.infrastructure.mapper;

import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.infrastructure.po.PermissionPO;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Permission Mapper
 * PO 與 Domain Object 之間的轉換
 */
@Component
public class PermissionMapper {

    /**
     * PO 轉換為 Domain Object
     */
    public Permission toDomain(PermissionPO po) {
        if (po == null) {
            return null;
        }

        return Permission.reconstitute(
                po.getPermissionId(),
                po.getPermissionCode(),
                po.getPermissionName(),
                po.getDescription(),
                po.getResource(),
                po.getAction(),
                toLocalDateTime(po.getCreatedAt())
        );
    }

    /**
     * Domain Object 轉換為 PO
     */
    public PermissionPO toPO(Permission permission) {
        if (permission == null) {
            return null;
        }

        return PermissionPO.builder()
                .permissionId(permission.getId().getValue())
                .permissionCode(permission.getPermissionCode())
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .resource(permission.getResource())
                .action(permission.getAction())
                .createdAt(toTimestamp(permission.getCreatedAt()))
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
