package com.company.hrms.performance.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 考核評估提交事件
 * 
 * 當員工提交自評或主管提交主管評時發布此事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewSubmittedEvent extends DomainEvent {
    /**
     * 事件 ID
     */
    private String eventId;

    /**
     * 考核記錄 ID
     */
    private ReviewId reviewId;

    /**
     * 週期 ID
     */
    private CycleId cycleId;

    /**
     * 員工 ID
     */
    private UUID employeeId;

    /**
     * 評核者 ID
     */
    private UUID reviewerId;

    /**
     * 評估類型 (SELF/MANAGER)
     */
    private String reviewType;

    /**
     * 加權總分
     */
    private BigDecimal overallScore;

    /**
     * 評等
     */
    private String overallRating;

    /**
     * 事件發生時間
     */
    private LocalDateTime occurredAt;

    /**
     * 建立事件
     */
    public static PerformanceReviewSubmittedEvent create(
            ReviewId reviewId,
            CycleId cycleId,
            UUID employeeId,
            UUID reviewerId,
            String reviewType,
            BigDecimal overallScore,
            String overallRating) {

        PerformanceReviewSubmittedEvent event = new PerformanceReviewSubmittedEvent();
        event.eventId = UUID.randomUUID().toString();
        event.reviewId = reviewId;
        event.cycleId = cycleId;
        event.employeeId = employeeId;
        event.reviewerId = reviewerId;
        event.reviewType = reviewType;
        event.overallScore = overallScore;
        event.overallRating = overallRating;
        event.occurredAt = LocalDateTime.now();

        return event;
    }

    @Override
    public String getAggregateType() {
        return "PerformanceReview";
    }

    @Override
    public String getAggregateId() {
        return reviewId.getValue().toString();
    }
}
