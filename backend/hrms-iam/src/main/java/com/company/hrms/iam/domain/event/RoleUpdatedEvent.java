package com.company.hrms.iam.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 角色更新事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdatedEvent extends DomainEvent {
    private String roleId;

    @Override
    public String getAggregateId() {
        return roleId;
    }

    @Override
    public String getAggregateType() {
        return "Role";
    }
}
