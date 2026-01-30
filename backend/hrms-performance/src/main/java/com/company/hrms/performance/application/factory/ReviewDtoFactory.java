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
                .cycleName("Cycle-" + review.getCycleId()) // Note: 暫時使用 ID，實際名稱需由 Service 層組裝
                .employeeId(review.getEmployeeId().toString())
                .employeeName("Emp-" + review.getEmployeeId()) // Note: 暫時使用 ID，實際名稱需由 Service 層組裝
                .reviewType(review.getReviewType())
                .status(review.getStatus())
                .overallScore(review.getOverallScore())
                .overallRating(review.getOverallRating())
                .build();
    }
}
