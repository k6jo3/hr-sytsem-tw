package com.company.hrms.common.domain.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 領域事件基類
 * 領域事件代表領域中已發生的重要事實
 *
 * <p>使用範例：
 * <pre>
 * public class UserCreatedEvent extends DomainEvent {
 *     private final String userId;
 *     private final String email;
 *
 *     public UserCreatedEvent(String userId, String email) {
 *         super();
 *         this.userId = userId;
 *         this.email = email;
 *     }
 * }
 * </pre>
 *
 * <p>事件命名規則：{Aggregate}{PastVerb}Event
 * <ul>
 *   <li>UserCreatedEvent - 使用者已創建</li>
 *   <li>EmployeeTerminatedEvent - 員工已離職</li>
 *   <li>LeaveApprovedEvent - 請假已核准</li>
 * </ul>
 */
public abstract class DomainEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件唯一識別碼
     */
    private final String eventId;

    /**
     * 事件發生時間
     */
    private final LocalDateTime occurredOn;

    /**
     * 事件類型（類別名稱）
     */
    private final String eventType;

    /**
     * 建立領域事件實例
     */
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }

    /**
     * 取得事件識別碼
     * @return 事件唯一識別碼
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * 取得事件發生時間
     * @return 事件發生時間
     */
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    /**
     * 取得事件類型
     * @return 事件類型名稱
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * 取得聚合根識別碼
     * 子類別應覆寫此方法返回對應的聚合根 ID
     *
     * @return 聚合根識別碼
     */
    public abstract String getAggregateId();

    /**
     * 取得聚合類型
     * @return 聚合類型名稱
     */
    public abstract String getAggregateType();

    @Override
    public String toString() {
        return eventType + "{" +
                "eventId='" + eventId + '\'' +
                ", occurredOn=" + occurredOn +
                ", aggregateId='" + getAggregateId() + '\'' +
                '}';
    }
}
