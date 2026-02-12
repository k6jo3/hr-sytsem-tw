package com.company.hrms.iam.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

/**
 * 密碼重置完成事件
 */
@Getter
public class PasswordResetCompletedEvent extends DomainEvent {

    private final String userId;

    public PasswordResetCompletedEvent(String userId) {
        super();
        this.userId = userId;
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
