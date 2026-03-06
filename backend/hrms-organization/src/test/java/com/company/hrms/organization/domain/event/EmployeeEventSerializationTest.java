package com.company.hrms.organization.domain.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.event.EventSerializationTestHelper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 員工事件序列化合約測試
 *
 * <p>驗證 EmployeeCreatedEvent / EmployeeTerminatedEvent 序列化後的 JSON
 * 包含下游消費者（Insurance、Reporting、Payroll）所需的必要欄位。
 *
 * <p>這是「生產者端合約測試」：確保事件結構不會被意外改動導致消費者解析失敗。
 */
@DisplayName("員工事件序列化合約測試")
class EmployeeEventSerializationTest {

    private static final UUID EMPLOYEE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID ORG_ID = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
    private static final UUID DEPT_ID = UUID.fromString("770e8400-e29b-41d4-a716-446655440000");

    @Nested
    @DisplayName("EmployeeCreatedEvent 序列化")
    class EmployeeCreatedEventSerialization {

        @Test
        @DisplayName("序列化 JSON 包含消費者需要的所有欄位")
        void serializedJson_containsAllRequiredFields() {
            // Arrange
            EmployeeCreatedEvent event = new EmployeeCreatedEvent(
                    EMPLOYEE_ID, "EMP001", "王大明", "wang@company.com",
                    ORG_ID, DEPT_ID, "資深工程師", LocalDate.of(2026, 1, 15)
            );

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — 下游消費者依賴的欄位
            assertThat(json.has("employeeId")).as("employeeId 必須存在").isTrue();
            assertThat(json.has("employeeNumber")).as("employeeNumber 必須存在").isTrue();
            assertThat(json.has("fullName")).as("fullName 必須存在").isTrue();
            assertThat(json.has("companyEmail")).as("companyEmail 必須存在").isTrue();
            assertThat(json.has("organizationId")).as("organizationId 必須存在").isTrue();
            assertThat(json.has("departmentId")).as("departmentId 必須存在").isTrue();
            assertThat(json.has("jobTitle")).as("jobTitle 必須存在").isTrue();
            assertThat(json.has("hireDate")).as("hireDate 必須存在").isTrue();

            // 基類欄位
            assertThat(json.has("eventId")).as("eventId 必須存在").isTrue();
            assertThat(json.has("occurredAt")).as("occurredAt 必須存在").isTrue();
            assertThat(json.has("eventType")).as("eventType 必須存在").isTrue();
        }

        @Test
        @DisplayName("欄位值正確性驗證")
        void fieldValues_areCorrect() {
            // Arrange
            EmployeeCreatedEvent event = new EmployeeCreatedEvent(
                    EMPLOYEE_ID, "EMP001", "王大明", "wang@company.com",
                    ORG_ID, DEPT_ID, "資深工程師", LocalDate.of(2026, 1, 15)
            );

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("employeeId").asText()).isEqualTo(EMPLOYEE_ID.toString());
            assertThat(json.get("employeeNumber").asText()).isEqualTo("EMP001");
            assertThat(json.get("fullName").asText()).isEqualTo("王大明");
            assertThat(json.get("eventType").asText()).isEqualTo("EmployeeCreatedEvent");
        }
    }

    @Nested
    @DisplayName("EmployeeTerminatedEvent 序列化")
    class EmployeeTerminatedEventSerialization {

        @Test
        @DisplayName("序列化 JSON 包含 Insurance 服務需要的欄位")
        void serializedJson_containsFieldsForInsuranceService() {
            // Arrange
            EmployeeTerminatedEvent event = new EmployeeTerminatedEvent(
                    EMPLOYEE_ID, "EMP001", "王大明", "wang@company.com",
                    ORG_ID, DEPT_ID, LocalDate.of(2026, 3, 31), "自願離職"
            );

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — Insurance 服務監聽器依賴的欄位
            assertThat(json.has("employeeId")).as("employeeId 必須存在（Insurance 退保用）").isTrue();
            assertThat(json.has("terminationDate")).as("terminationDate 必須存在（Insurance 退保日計算用）").isTrue();

            // 完整欄位驗證
            assertThat(json.has("employeeNumber")).isTrue();
            assertThat(json.has("fullName")).isTrue();
            assertThat(json.has("organizationId")).isTrue();
            assertThat(json.has("departmentId")).isTrue();
            assertThat(json.has("terminationReason")).isTrue();
        }

        @Test
        @DisplayName("日期格式為 ISO-8601")
        void terminationDate_isIsoFormat() {
            // Arrange
            EmployeeTerminatedEvent event = new EmployeeTerminatedEvent(
                    EMPLOYEE_ID, "EMP001", "王大明", "wang@company.com",
                    ORG_ID, DEPT_ID, LocalDate.of(2026, 3, 31), "自願離職"
            );

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — 消費者用 LocalDate.parse() 解析，必須是 yyyy-MM-dd
            String dateStr = json.get("terminationDate").asText();
            assertThat(dateStr).matches("\\d{4}-\\d{2}-\\d{2}");
            assertThat(LocalDate.parse(dateStr)).isEqualTo(LocalDate.of(2026, 3, 31));
        }
    }
}
