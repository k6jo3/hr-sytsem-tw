package com.company.hrms.performance.domain.event;

import java.time.LocalDateTime;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.performance.domain.model.valueobject.CycleId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 考核週期啟動事件
 * 
 * 當考核週期啟動時發布此事件，通知相關服務
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceCycleStartedEvent extends DomainEvent {
    /**
     * 事件 ID
     */
    private String eventId;

    /**
     * 週期 ID
     */
    private CycleId cycleId;

    /**
     * 週期名稱
     */
    private String cycleName;

    /**
     * 考核類型
     */
    private String cycleType;

    /**
     * 自評截止日
     */
    private String selfEvalDeadline;

    /**
     * 主管評截止日
     */
    private String managerEvalDeadline;

    /**
     * 事件發生時間
     */
    private LocalDateTime occurredAt;

    /**
     * 建立事件
     */
    public static PerformanceCycleStartedEvent create(
            CycleId cycleId,
            String cycleName,
            String cycleType,
            String selfEvalDeadline,
            String managerEvalDeadline) {

        PerformanceCycleStartedEvent event = new PerformanceCycleStartedEvent();
        event.eventId = java.util.UUID.randomUUID().toString();
        event.cycleId = cycleId;
        event.cycleName = cycleName;
        event.cycleType = cycleType;
        event.selfEvalDeadline = selfEvalDeadline;
        event.managerEvalDeadline = managerEvalDeadline;
        event.occurredAt = LocalDateTime.now();

        return event;
    }

    @Override
    public String getAggregateType() {
        return "PerformanceCycle";
    }

    @Override
    public String getAggregateId() {
        return cycleId.getValue().toString();
    }
}
