package com.company.hrms.iam.domain.event;

import java.util.List;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 角色權限更新事件
 */
@Getter
@NoArgsConstructor
public class RolePermissionsUpdatedEvent extends DomainEvent {
    private String roleId;
    private List<String> permissionIds;

    public RolePermissionsUpdatedEvent(String roleId, List<String> permissionIds) {
        this.roleId = roleId;
        this.permissionIds = permissionIds;
    }

    @Override
    public String getAggregateId() {
        return roleId;
    }

    @Override
    public String getAggregateType() {
        return "Role";
    }
}
