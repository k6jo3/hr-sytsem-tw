package com.company.hrms.performance.api.response;

import java.math.BigDecimal;
import java.util.List;

import com.company.hrms.performance.domain.model.valueobject.ReviewStatus;
import com.company.hrms.performance.domain.model.valueobject.ReviewType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 考核列表回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetReviewsResponse {
    private List<ReviewSummary> reviews;
    private int totalCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewSummary {
        private String reviewId;
        private String cycleId;
        private String cycleName;
        private String employeeId;
        private String employeeName;
        private ReviewType reviewType;
        private ReviewStatus status;
        private BigDecimal overallScore;
        private String overallRating;
    }
}
