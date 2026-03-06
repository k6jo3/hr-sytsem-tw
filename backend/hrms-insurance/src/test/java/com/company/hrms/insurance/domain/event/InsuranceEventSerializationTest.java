package com.company.hrms.insurance.domain.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.event.EventSerializationTestHelper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 保險事件序列化合約測試
 *
 * <p>驗證 InsuranceEnrollmentCompletedEvent / InsuranceWithdrawalCompletedEvent
 * 序列化後的 JSON 包含下游消費者（Payroll、Reporting）所需的必要欄位。
 */
@DisplayName("保險事件序列化合約測試")
class InsuranceEventSerializationTest {

    private static final String EMPLOYEE_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String TENANT_ID = "tenant-001";
    private static final String ENROLLMENT_ID = "enr-001";

    @Nested
    @DisplayName("InsuranceEnrollmentCompletedEvent 序列化")
    class EnrollmentCompletedSerialization {

        @Test
        @DisplayName("序列化 JSON 包含 Payroll 服務需要的所有欄位")
        void serializedJson_containsAllRequiredFields() {
            // Arrange
            var enrollment = InsuranceEnrollmentCompletedEvent.EnrollmentDetail.builder()
                    .enrollmentId(ENROLLMENT_ID)
                    .insuranceType("LABOR")
                    .monthlySalary(new BigDecimal("45800"))
                    .enrollDate(LocalDate.of(2026, 1, 15))
                    .build();

            var fees = InsuranceEnrollmentCompletedEvent.FeeDetail.builder()
                    .laborEmployeeFee(new BigDecimal("1051"))
                    .laborEmployerFee(new BigDecimal("3206"))
                    .healthEmployeeFee(new BigDecimal("372"))
                    .healthEmployerFee(new BigDecimal("744"))
                    .pensionEmployerFee(new BigDecimal("2748"))
                    .build();

            var event = InsuranceEnrollmentCompletedEvent.create(
                    EMPLOYEE_ID, LocalDate.of(2026, 1, 15),
                    List.of(enrollment), fees, TENANT_ID);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — 頂層欄位
            assertThat(json.has("eventId")).as("eventId 必須存在").isTrue();
            assertThat(json.has("eventType")).as("eventType 必須存在").isTrue();
            assertThat(json.has("timestamp")).as("timestamp 必須存在").isTrue();
            assertThat(json.has("tenantId")).as("tenantId 必須存在").isTrue();
            assertThat(json.has("payload")).as("payload 必須存在").isTrue();

            // Assert — payload 欄位
            JsonNode payload = json.get("payload");
            assertThat(payload.has("employeeId")).as("payload.employeeId 必須存在").isTrue();
            assertThat(payload.has("enrollDate")).as("payload.enrollDate 必須存在").isTrue();
            assertThat(payload.has("enrollments")).as("payload.enrollments 必須存在").isTrue();
            assertThat(payload.has("fees")).as("payload.fees 必須存在").isTrue();
        }

        @Test
        @DisplayName("欄位值正確性驗證")
        void fieldValues_areCorrect() {
            // Arrange
            var enrollment = InsuranceEnrollmentCompletedEvent.EnrollmentDetail.builder()
                    .enrollmentId(ENROLLMENT_ID)
                    .insuranceType("LABOR")
                    .monthlySalary(new BigDecimal("45800"))
                    .enrollDate(LocalDate.of(2026, 1, 15))
                    .build();

            var fees = InsuranceEnrollmentCompletedEvent.FeeDetail.builder()
                    .laborEmployeeFee(new BigDecimal("1051"))
                    .laborEmployerFee(new BigDecimal("3206"))
                    .healthEmployeeFee(new BigDecimal("372"))
                    .healthEmployerFee(new BigDecimal("744"))
                    .pensionEmployerFee(new BigDecimal("2748"))
                    .build();

            var event = InsuranceEnrollmentCompletedEvent.create(
                    EMPLOYEE_ID, LocalDate.of(2026, 1, 15),
                    List.of(enrollment), fees, TENANT_ID);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("eventType").asText()).isEqualTo("InsuranceEnrollmentCompleted");
            assertThat(json.get("tenantId").asText()).isEqualTo(TENANT_ID);
            assertThat(json.get("payload").get("employeeId").asText()).isEqualTo(EMPLOYEE_ID);

            // 驗證 enrollment 細節
            JsonNode enrollments = json.get("payload").get("enrollments");
            assertThat(enrollments.isArray()).isTrue();
            assertThat(enrollments).hasSize(1);
            assertThat(enrollments.get(0).get("insuranceType").asText()).isEqualTo("LABOR");
        }

        @Test
        @DisplayName("日期格式為 ISO-8601")
        void enrollDate_isIsoFormat() {
            // Arrange
            var event = InsuranceEnrollmentCompletedEvent.create(
                    EMPLOYEE_ID, LocalDate.of(2026, 1, 15),
                    List.of(), InsuranceEnrollmentCompletedEvent.FeeDetail.builder().build(),
                    TENANT_ID);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            String dateStr = json.get("payload").get("enrollDate").asText();
            assertThat(dateStr).matches("\\d{4}-\\d{2}-\\d{2}");
            assertThat(LocalDate.parse(dateStr)).isEqualTo(LocalDate.of(2026, 1, 15));
        }

        @Test
        @DisplayName("往返序列化一致性")
        void roundTrip_preservesData() {
            // Arrange
            var enrollment = InsuranceEnrollmentCompletedEvent.EnrollmentDetail.builder()
                    .enrollmentId(ENROLLMENT_ID)
                    .insuranceType("HEALTH")
                    .monthlySalary(new BigDecimal("45800"))
                    .enrollDate(LocalDate.of(2026, 2, 1))
                    .build();

            var fees = InsuranceEnrollmentCompletedEvent.FeeDetail.builder()
                    .laborEmployeeFee(new BigDecimal("1051"))
                    .laborEmployerFee(new BigDecimal("3206"))
                    .healthEmployeeFee(new BigDecimal("372"))
                    .healthEmployerFee(new BigDecimal("744"))
                    .pensionEmployerFee(new BigDecimal("2748"))
                    .build();

            var original = InsuranceEnrollmentCompletedEvent.create(
                    EMPLOYEE_ID, LocalDate.of(2026, 2, 1),
                    List.of(enrollment), fees, TENANT_ID);

            // Act
            var deserialized = EventSerializationTestHelper.roundTrip(
                    original, InsuranceEnrollmentCompletedEvent.class);

            // Assert
            assertThat(deserialized.getPayload().getEmployeeId())
                    .isEqualTo(original.getPayload().getEmployeeId());
            assertThat(deserialized.getPayload().getEnrollDate())
                    .isEqualTo(original.getPayload().getEnrollDate());
            assertThat(deserialized.getEventType()).isEqualTo("InsuranceEnrollmentCompleted");
        }
    }

    @Nested
    @DisplayName("InsuranceWithdrawalCompletedEvent 序列化")
    class WithdrawalCompletedSerialization {

        @Test
        @DisplayName("序列化 JSON 包含消費者需要的所有欄位")
        void serializedJson_containsAllRequiredFields() {
            // Arrange
            var event = InsuranceWithdrawalCompletedEvent.create(
                    EMPLOYEE_ID, ENROLLMENT_ID, "LABOR",
                    LocalDate.of(2026, 3, 31), "離職", TENANT_ID);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — 頂層欄位
            assertThat(json.has("eventId")).as("eventId 必須存在").isTrue();
            assertThat(json.has("eventType")).as("eventType 必須存在").isTrue();
            assertThat(json.has("timestamp")).as("timestamp 必須存在").isTrue();

            // Assert — payload 欄位
            JsonNode payload = json.get("payload");
            assertThat(payload.has("employeeId")).as("employeeId 必須存在").isTrue();
            assertThat(payload.has("enrollmentId")).as("enrollmentId 必須存在").isTrue();
            assertThat(payload.has("insuranceType")).as("insuranceType 必須存在").isTrue();
            assertThat(payload.has("withdrawDate")).as("withdrawDate 必須存在").isTrue();
            assertThat(payload.has("reason")).as("reason 必須存在").isTrue();
        }

        @Test
        @DisplayName("欄位值正確性驗證")
        void fieldValues_areCorrect() {
            // Arrange
            var event = InsuranceWithdrawalCompletedEvent.create(
                    EMPLOYEE_ID, ENROLLMENT_ID, "HEALTH",
                    LocalDate.of(2026, 3, 31), "自願離職", TENANT_ID);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("eventType").asText()).isEqualTo("InsuranceWithdrawalCompleted");
            assertThat(json.get("payload").get("employeeId").asText()).isEqualTo(EMPLOYEE_ID);
            assertThat(json.get("payload").get("insuranceType").asText()).isEqualTo("HEALTH");
            assertThat(json.get("payload").get("reason").asText()).isEqualTo("自願離職");
        }

        @Test
        @DisplayName("往返序列化一致性")
        void roundTrip_preservesData() {
            // Arrange
            var original = InsuranceWithdrawalCompletedEvent.create(
                    EMPLOYEE_ID, ENROLLMENT_ID, "LABOR",
                    LocalDate.of(2026, 3, 31), "離職", TENANT_ID);

            // Act
            var deserialized = EventSerializationTestHelper.roundTrip(
                    original, InsuranceWithdrawalCompletedEvent.class);

            // Assert
            assertThat(deserialized.getPayload().getEmployeeId())
                    .isEqualTo(original.getPayload().getEmployeeId());
            assertThat(deserialized.getPayload().getWithdrawDate())
                    .isEqualTo(original.getPayload().getWithdrawDate());
            assertThat(deserialized.getPayload().getInsuranceType())
                    .isEqualTo(original.getPayload().getInsuranceType());
        }
    }
}
