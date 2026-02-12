package com.company.hrms.iam.domain.event;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.iam.domain.model.valueobject.UserId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 使用者建立事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent extends DomainEvent {
    private String userId;
    private String username;
    private String email;
    private String displayName;
    private String employeeId;

    public UserCreatedEvent(UserId userId, String username, String email, String displayName, String employeeId) {
        super();
        this.userId = userId.getValue();
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.employeeId = employeeId;
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
