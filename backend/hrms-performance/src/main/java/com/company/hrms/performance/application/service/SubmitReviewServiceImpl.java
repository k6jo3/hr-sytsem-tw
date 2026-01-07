package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.performance.api.request.SubmitReviewRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.domain.event.PerformanceReviewSubmittedEvent;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 提交考核 Service
 */
@Service("submitReviewServiceImpl")
@RequiredArgsConstructor
@Transactional
public class SubmitReviewServiceImpl implements CommandApiService<SubmitReviewRequest, SuccessResponse> {

        private final IPerformanceReviewRepository reviewRepository;
        private final EventPublisher eventPublisher;

        @Override
        public SuccessResponse execCommand(SubmitReviewRequest req, JWTModel currentUser, String... args)
                        throws Exception {

                // 查詢考核記錄
                PerformanceReview review = reviewRepository.findById(ReviewId.of(req.getReviewId()))
                                .orElseThrow(() -> new IllegalArgumentException("考核記錄不存在"));

                // 提交考核：呼叫 Domain 方法
                review.submitEvaluation(req.getEvaluationItems(), req.getComments());
                reviewRepository.save(review);

                // 發布領域事件 (使用 Event factory method)
                PerformanceReviewSubmittedEvent event = PerformanceReviewSubmittedEvent.create(
                                review.getReviewId(),
                                review.getCycleId(),
                                review.getEmployeeId(),
                                review.getReviewerId(),
                                review.getReviewType().toString(),
                                review.getOverallScore(),
                                review.getOverallRating());

                eventPublisher.publishAll(java.util.Collections.singletonList(event));

                // 發布 Aggregate 內部的 Domain Events
                eventPublisher.publishAll(review.getDomainEvents());
                review.clearDomainEvents();

                return SuccessResponse.of("考核已提交");
        }
}
