package com.company.hrms.iam.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

/**
 * 使用者登入領域事件
 */
@Getter
public class UserLoggedInEvent extends DomainEvent {

    private final String userId;
    private final LocalDateTime loginTime;

    public UserLoggedInEvent(String userId) {
        super();
        this.userId = userId;
        this.loginTime = LocalDateTime.now();
    }

    public UserLoggedInEvent(String userId, LocalDateTime loginTime) {
        super();
        this.userId = userId;
        this.loginTime = loginTime;
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
