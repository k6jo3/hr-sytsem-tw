package com.company.hrms.iam.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

/**
 * 密碼重置請求事件
 */
@Getter
public class PasswordResetRequestedEvent extends DomainEvent {

    private final String userId;
    private final String email;

    public PasswordResetRequestedEvent(String userId, String email) {
        super();
        this.userId = userId;
        this.email = email;
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
