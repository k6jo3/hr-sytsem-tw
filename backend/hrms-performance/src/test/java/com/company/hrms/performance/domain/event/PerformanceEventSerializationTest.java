package com.company.hrms.performance.domain.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.event.EventSerializationTestHelper;
import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 績效事件序列化合約測試
 *
 * <p>驗證 PerformanceReviewCompletedEvent 序列化後的 JSON
 * 包含下游消費者（Payroll 調薪、Reporting 統計）所需的必要欄位。
 */
@DisplayName("績效事件序列化合約測試")
class PerformanceEventSerializationTest {

    private static final UUID EMPLOYEE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID REVIEW_UUID = UUID.fromString("880e8400-e29b-41d4-a716-446655440000");
    private static final UUID CYCLE_UUID = UUID.fromString("990e8400-e29b-41d4-a716-446655440000");

    @Nested
    @DisplayName("PerformanceReviewCompletedEvent 序列化")
    class ReviewCompletedSerialization {

        @Test
        @DisplayName("序列化 JSON 包含 Payroll 服務需要的欄位")
        void serializedJson_containsFieldsForPayrollService() {
            // Arrange
            var event = PerformanceReviewCompletedEvent.create(
                    ReviewId.of(REVIEW_UUID),
                    CycleId.of(CYCLE_UUID),
                    "2026年度考核",
                    EMPLOYEE_ID,
                    new BigDecimal("4.5"),
                    "A",
                    "表現優異");

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — Payroll 服務依賴的欄位
            assertThat(json.has("employeeId")).as("employeeId 必須存在（Payroll 調薪用）").isTrue();
            assertThat(json.has("finalScore")).as("finalScore 必須存在").isTrue();
            assertThat(json.has("finalRating")).as("finalRating 必須存在").isTrue();

            // Assert — Reporting 服務依賴的欄位
            assertThat(json.has("reviewId")).as("reviewId 必須存在").isTrue();
            assertThat(json.has("cycleId")).as("cycleId 必須存在").isTrue();
            assertThat(json.has("cycleName")).as("cycleName 必須存在").isTrue();
        }

        @Test
        @DisplayName("欄位值正確性驗證")
        void fieldValues_areCorrect() {
            // Arrange
            var event = PerformanceReviewCompletedEvent.create(
                    ReviewId.of(REVIEW_UUID),
                    CycleId.of(CYCLE_UUID),
                    "2026年度考核",
                    EMPLOYEE_ID,
                    new BigDecimal("4.5"),
                    "A",
                    "表現優異");

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("employeeId").asText()).isEqualTo(EMPLOYEE_ID.toString());
            assertThat(json.get("finalRating").asText()).isEqualTo("A");
            assertThat(json.get("cycleName").asText()).isEqualTo("2026年度考核");
            assertThat(json.get("adjustmentReason").asText()).isEqualTo("表現優異");
        }

        @Test
        @DisplayName("finalScore 為數值型別")
        void finalScore_isNumeric() {
            // Arrange
            var event = PerformanceReviewCompletedEvent.create(
                    ReviewId.of(REVIEW_UUID),
                    CycleId.of(CYCLE_UUID),
                    "2026年度考核",
                    EMPLOYEE_ID,
                    new BigDecimal("4.5"),
                    "A",
                    null);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — 消費者需要以數值解析分數
            assertThat(json.get("finalScore").isNumber()).as("finalScore 應為數值型別").isTrue();
            assertThat(json.get("finalScore").decimalValue())
                    .isEqualByComparingTo(new BigDecimal("4.5"));
        }

        @Test
        @DisplayName("aggregateType 為 PerformanceReview")
        void aggregateType_isPerformanceReview() {
            // Arrange
            var event = PerformanceReviewCompletedEvent.create(
                    ReviewId.of(REVIEW_UUID),
                    CycleId.of(CYCLE_UUID),
                    "2026年度考核",
                    EMPLOYEE_ID,
                    new BigDecimal("4.5"),
                    "A",
                    null);

            // Act & Assert
            assertThat(event.getAggregateType()).isEqualTo("PerformanceReview");
            assertThat(event.getAggregateId()).isEqualTo(REVIEW_UUID.toString());
        }
    }
}
