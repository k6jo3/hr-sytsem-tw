package com.company.hrms.iam.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.iam.domain.model.valueobject.UserId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 使用者停用事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDeactivatedEvent extends DomainEvent {
    private String userId;
    private String username;

    public UserDeactivatedEvent(UserId userId, String username) {
        super();
        this.userId = userId.getValue();
        this.username = username;
    }

    @Override
    public String getAggregateId() {
        return userId;
    }

    @Override
    public String getAggregateType() {
        return "User";
    }
}
