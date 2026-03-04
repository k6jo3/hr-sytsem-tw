package com.company.hrms.performance.application.factory;

import java.util.Map;
import java.util.UUID;

import com.company.hrms.performance.api.response.GetReviewsResponse;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;

/**
 * 考核 DTO Factory
 */
public class ReviewDtoFactory {

    /**
     * 轉換為摘要 DTO（含名稱查詢結果）
     *
     * @param review       考核記錄
     * @param cycleNameMap cycleId → cycleName 對照表
     */
    public static GetReviewsResponse.ReviewSummary toSummary(
            PerformanceReview review,
            Map<UUID, String> cycleNameMap,
            Map<UUID, String> employeeNameMap) {

        UUID cycleUuid = review.getCycleId().getValue();
        String cycleName = cycleNameMap.getOrDefault(cycleUuid, "未知週期");
        String employeeName = employeeNameMap.getOrDefault(review.getEmployeeId(), "未知員工");

        return GetReviewsResponse.ReviewSummary.builder()
                .reviewId(review.getReviewId().getValue().toString())
                .cycleId(cycleUuid.toString())
                .cycleName(cycleName)
                .employeeId(review.getEmployeeId().toString())
                .employeeName(employeeName)
                .reviewType(review.getReviewType())
                .status(review.getStatus())
                .overallScore(review.getOverallScore())
                .overallRating(review.getOverallRating())
                .build();
    }
}
