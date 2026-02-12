package com.company.hrms.iam.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 角色建立事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreatedEvent extends DomainEvent {
    private String roleId;
    private String roleCode;
    private String roleName;

    @Override
    public String getAggregateId() {
        return roleId;
    }

    @Override
    public String getAggregateType() {
        return "Role";
    }
}
