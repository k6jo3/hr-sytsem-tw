package com.company.hrms.performance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.company.hrms.performance.domain.model.valueobject.CycleStatus;
import com.company.hrms.performance.domain.model.valueobject.CycleType;
import com.company.hrms.performance.domain.model.valueobject.EvaluationItem;
import com.company.hrms.performance.domain.model.valueobject.EvaluationTemplate;
import com.company.hrms.performance.domain.model.valueobject.ScoringSystem;

/**
 * PerformanceCycle 聚合根測試
 */
class PerformanceCycleTest {

    @Test
    void testCreateCycle_Success() {
        // Given
        String cycleName = "2025年度考核";
        CycleType cycleType = CycleType.ANNUAL;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        LocalDate selfEvalDeadline = LocalDate.of(2026, 1, 15);
        LocalDate managerEvalDeadline = LocalDate.of(2026, 1, 31);

        // When
        PerformanceCycle cycle = PerformanceCycle.create(
                cycleName, cycleType, startDate, endDate,
                selfEvalDeadline, managerEvalDeadline);

        // Then
        assertNotNull(cycle);
        assertNotNull(cycle.getCycleId());
        assertEquals(cycleName, cycle.getCycleName());
        assertEquals(cycleType, cycle.getCycleType());
        assertEquals(CycleStatus.DRAFT, cycle.getStatus());
        assertEquals(startDate, cycle.getStartDate());
        assertEquals(endDate, cycle.getEndDate());
        assertNull(cycle.getTemplate()); // 初始時表單為 null
    }

    @Test
    void testCreateCycle_InvalidDateRange_ShouldThrowException() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 12, 31);
        LocalDate endDate = LocalDate.of(2025, 1, 1); // 結束日早於開始日

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            PerformanceCycle.create(
                    "2025年度考核", CycleType.ANNUAL,
                    startDate, endDate, null, null);
        });
    }

    @Test
    void testStartCycle_Success() {
        // Given
        PerformanceCycle cycle = createValidCycle();
        publishTemplate(cycle);

        // When
        cycle.start();

        // Then
        assertEquals(CycleStatus.IN_PROGRESS, cycle.getStatus());
    }

    @Test
    void testStartCycle_TemplateNotPublished_ShouldThrowException() {
        // Given
        PerformanceCycle cycle = createValidCycle();
        // 未發布表單

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            cycle.start();
        });
    }

    @Test
    void testStartCycle_AlreadyStarted_ShouldThrowException() {
        // Given
        PerformanceCycle cycle = createValidCycle();
        publishTemplate(cycle);
        cycle.start();

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            cycle.start();
        });
    }

    @Test
    void testCompleteCycle_Success() {
        // Given
        PerformanceCycle cycle = createValidCycle();
        publishTemplate(cycle);
        cycle.start();

        // When
        cycle.complete();

        // Then
        assertEquals(CycleStatus.COMPLETED, cycle.getStatus());
    }

    @Test
    void testCompleteCycle_NotInProgress_ShouldThrowException() {
        // Given
        PerformanceCycle cycle = createValidCycle();

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            cycle.complete();
        });
    }

    @Test
    void testUpdateCycle_InDraftStatus_Success() {
        // Given
        PerformanceCycle cycle = createValidCycle();
        String newName = "2025年度績效考核";

        // When
        cycle.updateCycleName(newName);

        // Then
        assertEquals(newName, cycle.getCycleName());
    }

    @Test
    void testUpdateCycle_AfterStarted_ShouldThrowException() {
        // Given
        PerformanceCycle cycle = createValidCycle();
        publishTemplate(cycle);
        cycle.start();

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            cycle.updateCycleName("新名稱");
        });
    }

    @Test
    void testSaveTemplate_Success() {
        // Given
        PerformanceCycle cycle = createValidCycle();
        EvaluationTemplate template = createValidTemplate();

        // When
        cycle.saveTemplate(template);

        // Then
        assertEquals(template, cycle.getTemplate());
    }

    @Test
    void testPublishTemplate_Success() {
        // Given
        PerformanceCycle cycle = createValidCycle();
        EvaluationTemplate template = createValidTemplate();
        cycle.saveTemplate(template);

        // When
        cycle.publishTemplate();

        // Then
        assertTrue(cycle.getTemplate().getIsPublished());
    }

    // === Helper Methods ===

    private PerformanceCycle createValidCycle() {
        return PerformanceCycle.create(
                "2025年度考核",
                CycleType.ANNUAL,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2026, 1, 31));
    }

    private EvaluationTemplate createValidTemplate() {
        EvaluationTemplate template = EvaluationTemplate.create(
                "2025年度績效考核表",
                ScoringSystem.FIVE_POINT,
                false);

        template.addEvaluationItem(EvaluationItem.createDefinition(
                "工作品質", 30, "工作成果的品質與完整度", null));
        template.addEvaluationItem(EvaluationItem.createDefinition(
                "專業能力", 30, "專業知識與技能的展現", null));
        template.addEvaluationItem(EvaluationItem.createDefinition(
                "團隊合作", 40, "與團隊成員的協作能力", null));

        return template;
    }

    private void publishTemplate(PerformanceCycle cycle) {
        EvaluationTemplate template = createValidTemplate();
        cycle.saveTemplate(template);
        cycle.publishTemplate();
    }
}
