package com.company.hrms.iam.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

/**
 * 使用者登出領域事件
 */
@Getter
public class UserLoggedOutEvent extends DomainEvent {

    private final String userId;
    private final String username;
    private final LocalDateTime logoutAt;

    public UserLoggedOutEvent(String userId, String username) {
        super();
        this.userId = userId;
        this.username = username;
        this.logoutAt = LocalDateTime.now();
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
