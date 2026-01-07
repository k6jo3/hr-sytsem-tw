package com.company.hrms.performance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;
import com.company.hrms.performance.domain.model.valueobject.ReviewStatus;
import com.company.hrms.performance.domain.model.valueobject.ReviewType;
import com.company.hrms.performance.domain.model.valueobject.ScoringSystem;

/**
 * PerformanceReview 聚合根測試
 */
class PerformanceReviewTest {

    @Test
    void testCreateReview_Success() {
        // Given
        CycleId cycleId = CycleId.create();
        UUID employeeId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();

        // When
        PerformanceReview review = PerformanceReview.create(
                cycleId, employeeId, reviewerId, ReviewType.SELF);

        // Then
        assertNotNull(review);
        assertNotNull(review.getReviewId());
        assertEquals(cycleId, review.getCycleId());
        assertEquals(employeeId, review.getEmployeeId());
        assertEquals(ReviewType.SELF, review.getReviewType());
        assertEquals(ReviewStatus.PENDING_SELF, review.getStatus());
        assertNull(review.getOverallScore());
        assertNull(review.getOverallRating());
    }

    @Test
    void testSubmitSelfEvaluation_Success() {
        // Given
        PerformanceReview review = createSelfReview();
        List<EvaluationItem> items = createEvaluationItems();

        // When
        review.submitEvaluation(items, "整體表現良好");

        // Then
        assertEquals(ReviewStatus.PENDING_MANAGER, review.getStatus());
        assertNotNull(review.getOverallScore());
        assertEquals(3, items.size());
        assertNotNull(review.getSubmittedAt());
    }

    @Test
    void testCalculateOverallScore_Success() {
        // Given
        PerformanceReview review = createSelfReview();
        List<EvaluationItem> items = new ArrayList<>();
        items.add(EvaluationItem.createWithScore(
                UUID.randomUUID(), "工作品質", 30, 4, "良好"));
        items.add(EvaluationItem.createWithScore(
                UUID.randomUUID(), "專業能力", 30, 5, "優秀"));
        items.add(EvaluationItem.createWithScore(
                UUID.randomUUID(), "團隊合作", 40, 3, "達標"));

        // When
        review.submitEvaluation(items, null);

        // Then
        // 計算: (4*30 + 5*30 + 3*40) / 100 = (120 + 150 + 120) / 100 = 3.9
        assertEquals(new BigDecimal("3.90"), review.getOverallScore());
    }

    @Test
    void testSubmitEvaluation_IncompleteItems_ShouldThrowException() {
        // Given
        PerformanceReview review = createSelfReview();
        List<EvaluationItem> items = new ArrayList<>();
        // 只有 2 個項目，不完整
        items.add(EvaluationItem.createWithScore(
                UUID.randomUUID(), "工作品質", 30, 4, null));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            review.submitEvaluation(items, null);
        });
    }

    @Test
    void testSubmitEvaluation_AlreadySubmitted_ShouldThrowException() {
        // Given
        PerformanceReview review = createSelfReview();
        List<EvaluationItem> items = createEvaluationItems();
        review.submitEvaluation(items, null);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            review.submitEvaluation(items, null);
        });
    }

    @Test
    void testFinalizeReview_Success() {
        // Given
        PerformanceReview selfReview = createSelfReview();
        selfReview.submitEvaluation(createEvaluationItems(), null);

        PerformanceReview managerReview = createManagerReview(
                selfReview.getCycleId(),
                selfReview.getEmployeeId());
        managerReview.submitEvaluation(createEvaluationItems(), null);

        // When
        BigDecimal finalScore = new BigDecimal("3.8");
        String finalRating = "B";
        selfReview.finalize(finalScore, finalRating, null);

        // Then
        assertEquals(ReviewStatus.FINALIZED, selfReview.getStatus());
        assertEquals(finalScore, selfReview.getFinalScore());
        assertEquals(finalRating, selfReview.getFinalRating());
        assertNotNull(selfReview.getFinalizedAt());
    }

    @Test
    void testFinalizeReview_NotSubmitted_ShouldThrowException() {
        // Given
        PerformanceReview review = createSelfReview();

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            review.finalize(new BigDecimal("4.0"), "A", null);
        });
    }

    @Test
    void testFinalizeReview_AlreadyFinalized_ShouldThrowException() {
        // Given
        PerformanceReview review = createSelfReview();
        review.submitEvaluation(createEvaluationItems(), null);
        review.finalize(new BigDecimal("4.0"), "A", null);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            review.finalize(new BigDecimal("3.5"), "B", null);
        });
    }

    @Test
    void testCalculateRating_FivePointSystem() {
        // Given
        PerformanceReview review = createSelfReview();

        // When & Then
        // >= 4.5 = A
        assertEquals("A", review.calculateRating(new BigDecimal("4.5"), ScoringSystem.FIVE_POINT));
        assertEquals("A", review.calculateRating(new BigDecimal("5.0"), ScoringSystem.FIVE_POINT));

        // >= 3.5 = B
        assertEquals("B", review.calculateRating(new BigDecimal("3.5"), ScoringSystem.FIVE_POINT));
        assertEquals("B", review.calculateRating(new BigDecimal("4.4"), ScoringSystem.FIVE_POINT));

        // >= 2.5 = C
        assertEquals("C", review.calculateRating(new BigDecimal("2.5"), ScoringSystem.FIVE_POINT));
        assertEquals("C", review.calculateRating(new BigDecimal("3.4"), ScoringSystem.FIVE_POINT));

        // < 2.5 = D
        assertEquals("D", review.calculateRating(new BigDecimal("2.4"), ScoringSystem.FIVE_POINT));
        assertEquals("D", review.calculateRating(new BigDecimal("1.0"), ScoringSystem.FIVE_POINT));
    }

    @Test
    void testCalculateRating_HundredSystem() {
        // Given
        PerformanceReview review = createSelfReview();

        // When & Then
        assertEquals("A", review.calculateRating(new BigDecimal("90"), ScoringSystem.HUNDRED));
        assertEquals("B", review.calculateRating(new BigDecimal("75"), ScoringSystem.HUNDRED));
        assertEquals("C", review.calculateRating(new BigDecimal("65"), ScoringSystem.HUNDRED));
        assertEquals("D", review.calculateRating(new BigDecimal("50"), ScoringSystem.HUNDRED));
    }

    // === Helper Methods ===

    private PerformanceReview createSelfReview() {
        return PerformanceReview.create(
                CycleId.create(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                ReviewType.SELF);
    }

    private PerformanceReview createManagerReview(CycleId cycleId, UUID employeeId) {
        return PerformanceReview.create(
                cycleId,
                employeeId,
                UUID.randomUUID(),
                ReviewType.MANAGER);
    }

    private List<EvaluationItem> createEvaluationItems() {
        List<EvaluationItem> items = new ArrayList<>();
        items.add(EvaluationItem.createWithScore(
                UUID.randomUUID(), "工作品質", 30, 4, "良好"));
        items.add(EvaluationItem.createWithScore(
                UUID.randomUUID(), "專業能力", 30, 4, "良好"));
        items.add(EvaluationItem.createWithScore(
                UUID.randomUUID(), "團隊合作", 40, 4, "良好"));
        return items;
    }
}
