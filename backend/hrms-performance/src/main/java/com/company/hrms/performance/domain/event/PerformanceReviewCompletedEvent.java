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
 * 考核完成事件
 * 
 * 當 HR 確認最終評等後發布此事件，通知薪資服務進行調薪處理
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewCompletedEvent extends DomainEvent {
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
     * 週期名稱
     */
    private String cycleName;

    /**
     * 員工 ID
     */
    private UUID employeeId;

    /**
     * 最終分數
     */
    private BigDecimal finalScore;

    /**
     * 最終評等
     */
    private String finalRating;

    /**
     * 調整原因
     */
    private String adjustmentReason;

    /**
     * 事件發生時間
     */
    private LocalDateTime occurredAt;

    /**
     * 建立事件
     */
    public static PerformanceReviewCompletedEvent create(
            ReviewId reviewId,
            CycleId cycleId,
            String cycleName,
            UUID employeeId,
            BigDecimal finalScore,
            String finalRating,
            String adjustmentReason) {

        PerformanceReviewCompletedEvent event = new PerformanceReviewCompletedEvent();
        event.eventId = UUID.randomUUID().toString();
        event.reviewId = reviewId;
        event.cycleId = cycleId;
        event.cycleName = cycleName;
        event.employeeId = employeeId;
        event.finalScore = finalScore;
        event.finalRating = finalRating;
        event.adjustmentReason = adjustmentReason;
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
