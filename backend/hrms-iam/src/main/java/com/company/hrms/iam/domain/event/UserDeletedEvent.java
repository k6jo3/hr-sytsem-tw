package com.company.hrms.iam.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.iam.domain.model.valueobject.UserId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 使用者刪除事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedEvent extends DomainEvent {
    private String userId;

    public UserDeletedEvent(UserId userId) {
        super();
        this.userId = userId.getValue();
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
