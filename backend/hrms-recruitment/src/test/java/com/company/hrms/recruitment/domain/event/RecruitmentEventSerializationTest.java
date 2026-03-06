package com.company.hrms.recruitment.domain.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.event.EventSerializationTestHelper;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.OfferId;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 招募事件序列化合約測試
 *
 * <p>驗證 CandidateHiredEvent / OfferSentEvent 序列化後的 JSON
 * 包含下游消費者（Organization 建員工、Notification 通知）所需的必要欄位。
 */
@DisplayName("招募事件序列化合約測試")
class RecruitmentEventSerializationTest {

    private static final UUID CANDIDATE_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID OPENING_UUID = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
    private static final UUID OFFER_UUID = UUID.fromString("770e8400-e29b-41d4-a716-446655440000");
    private static final UUID DEPT_UUID = UUID.fromString("880e8400-e29b-41d4-a716-446655440000");

    @Nested
    @DisplayName("CandidateHiredEvent 序列化")
    class CandidateHiredSerialization {

        @Test
        @DisplayName("序列化 JSON 包含 Organization 服務建立員工所需的欄位")
        void serializedJson_containsFieldsForOrganizationService() {
            // Arrange
            var event = CandidateHiredEvent.create(
                    CandidateId.of(CANDIDATE_UUID),
                    "王大明",
                    "wang@example.com",
                    "0912345678",
                    OpeningId.of(OPENING_UUID));

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — Organization 服務建立員工需要的欄位
            assertThat(json.has("candidateId")).as("candidateId 必須存在").isTrue();
            assertThat(json.has("fullName")).as("fullName 必須存在").isTrue();
            assertThat(json.has("email")).as("email 必須存在").isTrue();
            assertThat(json.has("phoneNumber")).as("phoneNumber 必須存在").isTrue();
            assertThat(json.has("openingId")).as("openingId 必須存在").isTrue();

            // Assert — 基類欄位
            assertThat(json.has("eventId")).as("eventId 必須存在").isTrue();
            assertThat(json.has("occurredOn")).as("occurredOn 必須存在").isTrue();
            assertThat(json.has("eventType")).as("eventType 必須存在").isTrue();
        }

        @Test
        @DisplayName("完整版事件包含職位與薪資資訊")
        void fullEvent_containsJobAndSalaryInfo() {
            // Arrange
            var event = CandidateHiredEvent.createFull(
                    CandidateId.of(CANDIDATE_UUID),
                    "王大明",
                    "wang@example.com",
                    "0912345678",
                    OpeningId.of(OPENING_UUID),
                    "資深工程師",
                    DEPT_UUID,
                    "研發部",
                    new BigDecimal("85000"),
                    LocalDate.of(2026, 4, 1),
                    "https://resume.example.com/wang",
                    null, null, null, null);

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("fullName").asText()).isEqualTo("王大明");
            assertThat(json.get("jobTitle").asText()).isEqualTo("資深工程師");
            assertThat(json.get("departmentName").asText()).isEqualTo("研發部");
            assertThat(json.has("offeredSalary")).as("offeredSalary 必須存在").isTrue();
            assertThat(json.has("expectedStartDate")).as("expectedStartDate 必須存在").isTrue();
        }

        @Test
        @DisplayName("aggregateType 為 Candidate")
        void aggregateType_isCandidate() {
            // Arrange
            var event = CandidateHiredEvent.create(
                    CandidateId.of(CANDIDATE_UUID),
                    "王大明", "wang@example.com", "0912345678",
                    OpeningId.of(OPENING_UUID));

            // Act & Assert
            assertThat(event.getAggregateType()).isEqualTo("Candidate");
            assertThat(event.getAggregateId()).isEqualTo(CANDIDATE_UUID.toString());
        }
    }

    @Nested
    @DisplayName("OfferSentEvent 序列化")
    class OfferSentSerialization {

        @Test
        @DisplayName("序列化 JSON 包含 Notification 服務需要的欄位")
        void serializedJson_containsFieldsForNotificationService() {
            // Arrange
            var event = OfferSentEvent.create(
                    OfferId.of(OFFER_UUID),
                    CandidateId.of(CANDIDATE_UUID),
                    "李小美",
                    "資深設計師",
                    new BigDecimal("75000"),
                    LocalDate.of(2026, 4, 15));

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert — Notification 服務寄送 offer 通知需要的欄位
            assertThat(json.has("offerId")).as("offerId 必須存在").isTrue();
            assertThat(json.has("candidateId")).as("candidateId 必須存在").isTrue();
            assertThat(json.has("candidateName")).as("candidateName 必須存在").isTrue();
            assertThat(json.has("offeredPosition")).as("offeredPosition 必須存在").isTrue();
            assertThat(json.has("offeredSalary")).as("offeredSalary 必須存在").isTrue();
            assertThat(json.has("expiryDate")).as("expiryDate 必須存在").isTrue();
        }

        @Test
        @DisplayName("欄位值正確性驗證")
        void fieldValues_areCorrect() {
            // Arrange
            var event = OfferSentEvent.create(
                    OfferId.of(OFFER_UUID),
                    CandidateId.of(CANDIDATE_UUID),
                    "李小美",
                    "資深設計師",
                    new BigDecimal("75000"),
                    LocalDate.of(2026, 4, 15));

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            assertThat(json.get("candidateName").asText()).isEqualTo("李小美");
            assertThat(json.get("offeredPosition").asText()).isEqualTo("資深設計師");
            assertThat(json.get("eventType").asText()).isEqualTo("OfferSentEvent");
        }

        @Test
        @DisplayName("日期格式為 ISO-8601")
        void expiryDate_isIsoFormat() {
            // Arrange
            var event = OfferSentEvent.create(
                    OfferId.of(OFFER_UUID),
                    CandidateId.of(CANDIDATE_UUID),
                    "李小美",
                    "資深設計師",
                    new BigDecimal("75000"),
                    LocalDate.of(2026, 4, 15));

            // Act
            JsonNode json = EventSerializationTestHelper.toJsonNode(event);

            // Assert
            String dateStr = json.get("expiryDate").asText();
            assertThat(dateStr).matches("\\d{4}-\\d{2}-\\d{2}");
            assertThat(LocalDate.parse(dateStr)).isEqualTo(LocalDate.of(2026, 4, 15));
        }

        @Test
        @DisplayName("aggregateType 為 Offer")
        void aggregateType_isOffer() {
            // Arrange
            var event = OfferSentEvent.create(
                    OfferId.of(OFFER_UUID),
                    CandidateId.of(CANDIDATE_UUID),
                    "李小美", "資深設計師",
                    new BigDecimal("75000"),
                    LocalDate.of(2026, 4, 15));

            // Act & Assert
            assertThat(event.getAggregateType()).isEqualTo("Offer");
            assertThat(event.getAggregateId()).isEqualTo(OFFER_UUID.toString());
        }
    }
}
