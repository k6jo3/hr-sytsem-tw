package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.performance.api.request.StartCycleRequest;
import com.company.hrms.performance.api.response.GetReviewsResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢考核詳情 Service
 */
@Service("getReviewDetailServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetReviewDetailServiceImpl
        implements QueryApiService<StartCycleRequest, GetReviewsResponse.ReviewSummary> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    public GetReviewsResponse.ReviewSummary getResponse(StartCycleRequest req, JWTModel currentUser, String... args)
            throws Exception {

        PerformanceReview review = reviewRepository.findById(ReviewId.of(req.getCycleId()))
                .orElseThrow(() -> new IllegalArgumentException("考核記錄不存在"));

        return GetReviewsResponse.ReviewSummary.builder()
                .reviewId(review.getReviewId().getValue().toString())
                .cycleId(review.getCycleId().getValue().toString())
                .cycleName("") // TODO: 從 Cycle 查詢
                .employeeId(review.getEmployeeId().toString())
                .employeeName("") // TODO: 從 Organization 查詢
                .reviewType(review.getReviewType())
                .status(review.getStatus())
                .overallScore(review.getOverallScore())
                .overallRating(review.getOverallRating())
                .build();
    }
}
