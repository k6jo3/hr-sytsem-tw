package com.company.hrms.attendance.domain.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.event.EventSerializationTestHelper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 考勤事件序列化合約測試
 *
 * <p>驗證 LeaveApprovedEvent / OvertimeApprovedEvent 序列化後的 JSON
 * 包含下游消費者（Payroll、Reporting）所需的必要欄位。
 */
@DisplayName("考勤事件序列化合約測試")
class AttendanceEventSerializationTest {

    private static final String APPLICATION_ID = "leave-app-001";
    private static final String MANAGER_ID = "550e8400-e29b-41d4-a716-446655440003";

    @Nested
    @DisplayName("LeaveApprovedEvent 序列化")
    class LeaveApprovedSerialization {

        @Test
        @DisplayName("序列化 JSON 包含消費者需要的所有欄位")
        void serializedJson_containsAllRequiredFields() {
            // Arrange
            var event = new LeaveApprovedEvent(APPLICATION_ID, MANAGER_ID);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — 業務欄位
            assertThat(json.has("applicationId")).as("applicationId 必須存在").isTrue();
            assertThat(json.has("approvedBy")).as("approvedBy 必須存在").isTrue();

            // Assert — 基類欄位（DomainEvent）
            assertThat(json.has("eventId")).as("eventId 必須存在").isTrue();
            assertThat(json.has("occurredOn")).as("occurredOn 必須存在").isTrue();
            assertThat(json.has("eventType")).as("eventType 必須存在").isTrue();
        }

        @Test
        @DisplayName("欄位值正確性驗證")
        void fieldValues_areCorrect() {
            // Arrange
            var event = new LeaveApprovedEvent(APPLICATION_ID, MANAGER_ID);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("applicationId").asText()).isEqualTo(APPLICATION_ID);
            assertThat(json.get("approvedBy").asText()).isEqualTo(MANAGER_ID);
            assertThat(json.get("eventType").asText()).isEqualTo("LeaveApprovedEvent");
        }

        @Test
        @DisplayName("aggregateType 為 LeaveApplication")
        void aggregateType_isLeaveApplication() {
            // Arrange
            var event = new LeaveApprovedEvent(APPLICATION_ID, MANAGER_ID);

            // Act & Assert
            assertThat(event.getAggregateType()).isEqualTo("LeaveApplication");
            assertThat(event.getAggregateId()).isEqualTo(APPLICATION_ID);
        }
    }

    @Nested
    @DisplayName("OvertimeApprovedEvent 序列化")
    class OvertimeApprovedSerialization {

        private static final String OT_APPLICATION_ID = "ot-app-001";

        @Test
        @DisplayName("序列化 JSON 包含消費者需要的所有欄位")
        void serializedJson_containsAllRequiredFields() {
            // Arrange
            var event = new OvertimeApprovedEvent(OT_APPLICATION_ID, MANAGER_ID);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — 業務欄位
            assertThat(json.has("applicationId")).as("applicationId 必須存在").isTrue();
            assertThat(json.has("approvedBy")).as("approvedBy 必須存在").isTrue();

            // Assert — 基類欄位
            assertThat(json.has("eventId")).as("eventId 必須存在").isTrue();
            assertThat(json.has("occurredOn")).as("occurredOn 必須存在").isTrue();
            assertThat(json.has("eventType")).as("eventType 必須存在").isTrue();
        }

        @Test
        @DisplayName("欄位值正確性驗證")
        void fieldValues_areCorrect() {
            // Arrange
            var event = new OvertimeApprovedEvent(OT_APPLICATION_ID, MANAGER_ID);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("applicationId").asText()).isEqualTo(OT_APPLICATION_ID);
            assertThat(json.get("approvedBy").asText()).isEqualTo(MANAGER_ID);
            assertThat(json.get("eventType").asText()).isEqualTo("OvertimeApprovedEvent");
        }

        @Test
        @DisplayName("aggregateType 為 OvertimeApplication")
        void aggregateType_isOvertimeApplication() {
            // Arrange
            var event = new OvertimeApprovedEvent(OT_APPLICATION_ID, MANAGER_ID);

            // Act & Assert
            assertThat(event.getAggregateType()).isEqualTo("OvertimeApplication");
            assertThat(event.getAggregateId()).isEqualTo(OT_APPLICATION_ID);
        }
    }
}
