package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.FinalizeReviewRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;
import com.company.hrms.performance.domain.service.RatingCalculator;

import lombok.RequiredArgsConstructor;

/**
 * 確認最終評等 Service
 */
@Service("finalizeReviewServiceImpl")
@RequiredArgsConstructor
@Transactional
public class FinalizeReviewServiceImpl implements CommandApiService<FinalizeReviewRequest, SuccessResponse> {

    private final IPerformanceReviewRepository reviewRepository;
    private final RatingCalculator ratingCalculator;
    private final EventPublisher eventPublisher;

    @Override
    public SuccessResponse execCommand(FinalizeReviewRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // 查詢考核記錄
        PerformanceReview review = reviewRepository.findById(ReviewId.of(req.getReviewId()))
                .orElseThrow(() -> new IllegalArgumentException("考核記錄不存在"));

        // TODO: 確認最終評等 (需要 Domain 方法支援)
        // review.finalizeReview(req.getFinalRating(), req.getAdjustmentReason());
        reviewRepository.save(review);

        // 發布領域事件 (假設 PerformanceReviewCompletedEvent 已存在)
        // PerformanceReviewCompletedEvent event =
        // PerformanceReviewCompletedEvent.create(...);
        // eventPublisher.publishAll(java.util.Collections.singletonList(event));

        // 發布 Aggregate 內部的 Domain Events
        eventPublisher.publishAll(review.getDomainEvents());
        review.clearDomainEvents();

        return SuccessResponse.of("考核評等已確認");
    }
}
