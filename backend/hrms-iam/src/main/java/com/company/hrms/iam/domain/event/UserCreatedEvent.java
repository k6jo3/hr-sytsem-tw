package com.company.hrms.iam.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 使用者已建立領域事件
 * 當新使用者建立成功後發布此事件
 */
@Getter
@AllArgsConstructor
public class UserCreatedEvent {

    /**
     * 使用者 ID
     */
    private final String userId;

    /**
     * 使用者名稱
     */
    private final String username;

    /**
     * Email
     */
    private final String email;

    /**
     * 事件發生時間
     */
    private final LocalDateTime occurredAt;

    public UserCreatedEvent(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.occurredAt = LocalDateTime.now();
    }
}
