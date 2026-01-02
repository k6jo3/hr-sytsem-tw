package com.company.hrms.performance.domain.model.aggregate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.model.valueobject.ReviewStatus;
import com.company.hrms.performance.domain.model.valueobject.ReviewType;
import com.company.hrms.performance.domain.model.valueobject.ScoringSystem;

import lombok.Getter;

/**
 * 考核記錄聚合根
 */
@Getter
public class PerformanceReview {
    /**
     * 考核記錄 ID
     */
    private ReviewId reviewId;

    /**
     * 考核週期 ID
     */
    private CycleId cycleId;

    /**
     * 被考核員工 ID
     */
    private UUID employeeId;

    /**
     * 評核者 ID
     */
    private UUID reviewerId;

    /**
     * 評估類型
     */
    private ReviewType reviewType;

    /**
     * 評估項目列表
     */
    private List<EvaluationItem> evaluationItems;

    /**
     * 加權總分
     */
    private BigDecimal overallScore;

    /**
     * 系統計算評等
     */
    private String overallRating;

    /**
     * 最終分數（HR 可調整）
     */
    private BigDecimal finalScore;

    /**
     * 最終評等（HR 確認）
     */
    private String finalRating;

    /**
     * 調整原因
     */
    private String adjustmentReason;

    /**
     * 評語
     */
    private String comments;

    /**
     * 考核狀態
     */
    private ReviewStatus status;

    /**
     * 提交時間
     */
    private LocalDateTime submittedAt;

    /**
     * 確認時間
     */
    private LocalDateTime finalizedAt;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    /**
     * 建立考核記錄
     */
    public static PerformanceReview create(
            CycleId cycleId,
            UUID employeeId,
            UUID reviewerId,
            ReviewType reviewType) {

        validateCycleId(cycleId);
        validateEmployeeId(employeeId);
        validateReviewerId(reviewerId);
        validateReviewType(reviewType);

        PerformanceReview review = new PerformanceReview();
        review.reviewId = ReviewId.create();
        review.cycleId = cycleId;
        review.employeeId = employeeId;
        review.reviewerId = reviewerId;
        review.reviewType = reviewType;
        review.evaluationItems = new ArrayList<>();
        review.status = ReviewStatus.PENDING_SELF;
        review.createdAt = LocalDateTime.now();
        review.updatedAt = LocalDateTime.now();

        return review;
    }

    /**
     * 重建考核記錄（用於從資料庫載入）
     */
    public static PerformanceReview reconstitute(
            ReviewId reviewId,
            CycleId cycleId,
            UUID employeeId,
            UUID reviewerId,
            ReviewType reviewType,
            List<EvaluationItem> evaluationItems,
            BigDecimal overallScore,
            String overallRating,
            BigDecimal finalScore,
            String finalRating,
            String adjustmentReason,
            String comments,
            ReviewStatus status,
            LocalDateTime submittedAt,
            LocalDateTime finalizedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        PerformanceReview review = new PerformanceReview();
        review.reviewId = reviewId;
        review.cycleId = cycleId;
        review.employeeId = employeeId;
        review.reviewerId = reviewerId;
        review.reviewType = reviewType;
        review.evaluationItems = evaluationItems != null ? new ArrayList<>(evaluationItems) : new ArrayList<>();
        review.overallScore = overallScore;
        review.overallRating = overallRating;
        review.finalScore = finalScore;
        review.finalRating = finalRating;
        review.adjustmentReason = adjustmentReason;
        review.comments = comments;
        review.status = status;
        review.submittedAt = submittedAt;
        review.finalizedAt = finalizedAt;
        review.createdAt = createdAt;
        review.updatedAt = updatedAt;

        return review;
    }

    /**
     * 提交評估
     */
    public void submitEvaluation(List<EvaluationItem> items, String comments) {
        // 檢查是否已提交
        if (submittedAt != null) {
            throw new IllegalStateException("已提交過評估，無法重複提交");
        }

        if (status != ReviewStatus.PENDING_SELF && status != ReviewStatus.PENDING_MANAGER) {
            throw new IllegalStateException("當前狀態無法提交評估");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("評估項目不可為空");
        }

        // 驗證項目數量（至少需要 3 個項目）
        if (items.size() < 3) {
            throw new IllegalArgumentException("評估項目不完整，至少需要 3 個項目");
        }

        // 驗證所有項目都已評分
        validateAllItemsScored(items);

        // 計算加權總分
        this.evaluationItems = new ArrayList<>(items);
        this.overallScore = calculateOverallScore(items);
        this.comments = comments;
        this.submittedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // 更新狀態
        if (status == ReviewStatus.PENDING_SELF) {
            this.status = ReviewStatus.PENDING_MANAGER;
        } else if (status == ReviewStatus.PENDING_MANAGER) {
            this.status = ReviewStatus.PENDING_FINALIZE;
        }
    }

    /**
     * 確認最終評等
     */
    public void finalize(BigDecimal finalScore, String finalRating, String adjustmentReason) {
        if (status != ReviewStatus.PENDING_MANAGER && status != ReviewStatus.PENDING_FINALIZE) {
            throw new IllegalStateException("尚未完成評估，無法確認最終評等");
        }
        if (status == ReviewStatus.FINALIZED) {
            throw new IllegalStateException("已確認過最終評等");
        }
        if (finalScore == null) {
            throw new IllegalArgumentException("最終分數不可為空");
        }
        if (finalRating == null || finalRating.isBlank()) {
            throw new IllegalArgumentException("最終評等不可為空");
        }

        this.finalScore = finalScore;
        this.finalRating = finalRating;
        this.adjustmentReason = adjustmentReason;
        this.status = ReviewStatus.FINALIZED;
        this.finalizedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 計算評等（根據評分制度）
     */
    public String calculateRating(BigDecimal score, ScoringSystem scoringSystem) {
        if (score == null) {
            return null;
        }

        switch (scoringSystem) {
            case FIVE_POINT:
                return calculateRatingForFivePoint(score);
            case HUNDRED:
                return calculateRatingForHundred(score);
            case FIVE_GRADE:
                // 五等第制直接由使用者選擇，不計算
                return null;
            default:
                throw new IllegalArgumentException("不支援的評分制度: " + scoringSystem);
        }
    }

    /**
     * 計算加權總分
     */
    private BigDecimal calculateOverallScore(List<EvaluationItem> items) {
        BigDecimal totalScore = BigDecimal.ZERO;

        for (EvaluationItem item : items) {
            BigDecimal weightedScore = item.calculateWeightedScore();
            totalScore = totalScore.add(weightedScore);
        }

        return totalScore.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 五分制評等計算
     * >= 4.5 = A
     * >= 3.5 = B
     * >= 2.5 = C
     * < 2.5 = D
     */
    private String calculateRatingForFivePoint(BigDecimal score) {
        if (score.compareTo(new BigDecimal("4.5")) >= 0) {
            return "A";
        } else if (score.compareTo(new BigDecimal("3.5")) >= 0) {
            return "B";
        } else if (score.compareTo(new BigDecimal("2.5")) >= 0) {
            return "C";
        } else {
            return "D";
        }
    }

    /**
     * 百分制評等計算
     * >= 90 = A
     * >= 70 = B
     * >= 60 = C
     * < 60 = D
     */
    private String calculateRatingForHundred(BigDecimal score) {
        if (score.compareTo(new BigDecimal("90")) >= 0) {
            return "A";
        } else if (score.compareTo(new BigDecimal("70")) >= 0) {
            return "B";
        } else if (score.compareTo(new BigDecimal("60")) >= 0) {
            return "C";
        } else {
            return "D";
        }
    }

    /**
     * 驗證所有項目都已評分
     */
    private void validateAllItemsScored(List<EvaluationItem> items) {
        for (EvaluationItem item : items) {
            if (item.getScore() == null) {
                throw new IllegalArgumentException("所有評估項目都必須評分");
            }
        }
    }

    // === 驗證方法 ===

    private static void validateCycleId(CycleId cycleId) {
        if (cycleId == null) {
            throw new IllegalArgumentException("考核週期 ID 不可為空");
        }
    }

    private static void validateEmployeeId(UUID employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("員工 ID 不可為空");
        }
    }

    private static void validateReviewerId(UUID reviewerId) {
        if (reviewerId == null) {
            throw new IllegalArgumentException("評核者 ID 不可為空");
        }
    }

    private static void validateReviewType(ReviewType reviewType) {
        if (reviewType == null) {
            throw new IllegalArgumentException("評估類型不可為空");
        }
    }
}
