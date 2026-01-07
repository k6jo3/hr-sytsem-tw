package com.company.hrms.performance.application.factory;

import com.company.hrms.performance.api.response.GetReviewsResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;

/**
 * 考核 DTO Factory
 */
public class ReviewDtoFactory {

    public static GetReviewsResponse.ReviewSummary toSummary(PerformanceReview review) {
        return GetReviewsResponse.ReviewSummary.builder()
                .reviewId(review.getReviewId().getValue().toString())
                .cycleId(review.getCycleId().getValue().toString())
                .cycleName("") // TODO: 需關聯查詢或從 View 取得
                .employeeId(review.getEmployeeId().toString())
                .employeeName("") // TODO: 需關聯查詢或從 View 取得
                .reviewType(review.getReviewType())
                .status(review.getStatus())
                .overallScore(review.getOverallScore())
                .overallRating(review.getOverallRating())
                .build();
    }
}
