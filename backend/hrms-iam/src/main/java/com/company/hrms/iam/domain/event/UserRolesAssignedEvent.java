package com.company.hrms.iam.domain.event;

import java.util.List;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 使用者角色指派事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesAssignedEvent extends DomainEvent {
    /** 使用者 ID */
    private String userId;

    /** 指派的角色 ID 列表 */
    private List<String> roleIds;

    @Override
    public String getAggregateId() {
        return userId;
    }

    @Override
    public String getAggregateType() {
        return "User";
    }
}
