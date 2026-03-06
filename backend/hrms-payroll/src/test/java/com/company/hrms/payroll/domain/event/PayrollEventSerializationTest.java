package com.company.hrms.payroll.domain.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.event.EventSerializationTestHelper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 薪資事件序列化合約測試
 *
 * <p>驗證 PayrollRunStartedEvent / PayslipGeneratedEvent 序列化後的 JSON
 * 包含下游消費者（Reporting、Notification）所需的必要欄位。
 */
@DisplayName("薪資事件序列化合約測試")
class PayrollEventSerializationTest {

    private static final String RUN_ID = "run-2026-03";
    private static final String ORG_ID = "660e8400-e29b-41d4-a716-446655440000";
    private static final String PAYSLIP_ID = "slip-001";
    private static final String EMPLOYEE_ID = "550e8400-e29b-41d4-a716-446655440000";

    @Nested
    @DisplayName("PayrollRunStartedEvent 序列化")
    class PayrollRunStartedSerialization {

        @Test
        @DisplayName("序列化 JSON 包含消費者需要的所有欄位")
        void serializedJson_containsAllRequiredFields() {
            // Arrange
            var event = new PayrollRunStartedEvent(
                    RUN_ID, ORG_ID, LocalDateTime.of(2026, 3, 1, 10, 0));

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — 業務欄位
            assertThat(json.has("runId")).as("runId 必須存在").isTrue();
            assertThat(json.has("organizationId")).as("organizationId 必須存在").isTrue();
            assertThat(json.has("startedAt")).as("startedAt 必須存在").isTrue();

            // Assert — 基類欄位（DomainEvent）
            assertThat(json.has("eventId")).as("eventId 必須存在").isTrue();
            assertThat(json.has("occurredOn")).as("occurredOn 必須存在").isTrue();
            assertThat(json.has("eventType")).as("eventType 必須存在").isTrue();
        }

        @Test
        @DisplayName("欄位值正確性驗證")
        void fieldValues_areCorrect() {
            // Arrange
            var event = new PayrollRunStartedEvent(
                    RUN_ID, ORG_ID, LocalDateTime.of(2026, 3, 1, 10, 0));

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("runId").asText()).isEqualTo(RUN_ID);
            assertThat(json.get("organizationId").asText()).isEqualTo(ORG_ID);
            assertThat(json.get("eventType").asText()).isEqualTo("PayrollRunStartedEvent");
        }

        @Test
        @DisplayName("時間格式為 ISO-8601")
        void startedAt_isIsoFormat() {
            // Arrange
            var event = new PayrollRunStartedEvent(
                    RUN_ID, ORG_ID, LocalDateTime.of(2026, 3, 1, 10, 30, 0));

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            String dateStr = json.get("startedAt").asText();
            assertThat(dateStr).contains("2026-03-01");
            assertThat(LocalDateTime.parse(dateStr)).isEqualTo(LocalDateTime.of(2026, 3, 1, 10, 30, 0));
        }
    }

    @Nested
    @DisplayName("PayslipGeneratedEvent 序列化")
    class PayslipGeneratedSerialization {

        @Test
        @DisplayName("序列化 JSON 包含消費者需要的所有欄位")
        void serializedJson_containsAllRequiredFields() {
            // Arrange
            var event = new PayslipGeneratedEvent(
                    PAYSLIP_ID, EMPLOYEE_ID, RUN_ID,
                    LocalDateTime.of(2026, 3, 5, 14, 0));

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.has("payslipId")).as("payslipId 必須存在").isTrue();
            assertThat(json.has("employeeId")).as("employeeId 必須存在").isTrue();
            assertThat(json.has("runId")).as("runId 必須存在").isTrue();
            assertThat(json.has("occurredAt")).as("occurredAt 必須存在").isTrue();
        }

        @Test
        @DisplayName("欄位值正確性驗證")
        void fieldValues_areCorrect() {
            // Arrange
            var event = new PayslipGeneratedEvent(
                    PAYSLIP_ID, EMPLOYEE_ID, RUN_ID,
                    LocalDateTime.of(2026, 3, 5, 14, 0));

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("payslipId").asText()).isEqualTo(PAYSLIP_ID);
            assertThat(json.get("employeeId").asText()).isEqualTo(EMPLOYEE_ID);
            assertThat(json.get("runId").asText()).isEqualTo(RUN_ID);
        }

        @Test
        @DisplayName("往返序列化一致性")
        void roundTrip_preservesData() {
            // Arrange
            var original = new PayslipGeneratedEvent(
                    PAYSLIP_ID, EMPLOYEE_ID, RUN_ID,
                    LocalDateTime.of(2026, 3, 5, 14, 0));

            // Act
            var deserialized = EventSerializationTestHelper.roundTrip(
                    original, PayslipGeneratedEvent.class);

            // Assert
            assertThat(deserialized.getPayslipId()).isEqualTo(original.getPayslipId());
            assertThat(deserialized.getEmployeeId()).isEqualTo(original.getEmployeeId());
            assertThat(deserialized.getRunId()).isEqualTo(original.getRunId());
            assertThat(deserialized.getOccurredAt()).isEqualTo(original.getOccurredAt());
        }
    }
}
