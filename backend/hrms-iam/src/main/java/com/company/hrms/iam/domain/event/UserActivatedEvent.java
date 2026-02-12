package com.company.hrms.iam.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.iam.domain.model.valueobject.UserId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 使用者啟用事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserActivatedEvent extends DomainEvent {
    private String userId;
    private String username;

    public UserActivatedEvent(UserId userId, String username) {
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
