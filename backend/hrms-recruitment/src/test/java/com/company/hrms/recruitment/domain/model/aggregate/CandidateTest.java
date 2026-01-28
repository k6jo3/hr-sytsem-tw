package com.company.hrms.recruitment.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.model.valueobject.RecruitmentSource;
import com.company.hrms.recruitment.domain.event.CandidateHiredEvent;

/**
 * 應徵者聚合根測試
 */
@DisplayName("Candidate 應徵者聚合根測試")
class CandidateTest {

    private static final OpeningId OPENING_ID = OpeningId.create();
    private static final String FULL_NAME = "王小明";
    private static final String EMAIL = "wang@email.com";
    private static final String PHONE = "0912-345-678";
    private static final RecruitmentSource SOURCE = RecruitmentSource.JOB_BANK;

    @Nested
    @DisplayName("建立應徵者")
    class CreateCandidateTests {

        @Test
        @DisplayName("應成功建立新應徵者，狀態為 NEW")
        void shouldCreateCandidateWithNewStatus() {
            // When
            Candidate candidate = Candidate.create(OPENING_ID, FULL_NAME, EMAIL, PHONE, SOURCE);

            // Then
            assertNotNull(candidate.getId());
            assertEquals(OPENING_ID, candidate.getOpeningId());
            assertEquals(FULL_NAME, candidate.getFullName());
            assertEquals(EMAIL, candidate.getEmail());
            assertEquals(PHONE, candidate.getPhoneNumber());
            assertEquals(SOURCE, candidate.getSource());
            assertEquals(CandidateStatus.NEW, candidate.getStatus());
            assertNotNull(candidate.getApplicationDate());
        }

        @Test
        @DisplayName("姓名為空時應拋出例外")
        void shouldThrowExceptionWhenNameIsEmpty() {
            assertThrows(IllegalArgumentException.class, () -> Candidate.create(OPENING_ID, "", EMAIL, PHONE, SOURCE));
        }

        @Test
        @DisplayName("Email為空時應拋出例外")
        void shouldThrowExceptionWhenEmailIsEmpty() {
            assertThrows(IllegalArgumentException.class,
                    () -> Candidate.create(OPENING_ID, FULL_NAME, "", PHONE, SOURCE));
        }

        @Test
        @DisplayName("職缺ID為空時應拋出例外")
        void shouldThrowExceptionWhenOpeningIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> Candidate.create(null, FULL_NAME, EMAIL, PHONE, SOURCE));
        }
    }

    @Nested
    @DisplayName("狀態轉換: 履歷篩選")
    class ScreeningTests {

        @Test
        @DisplayName("NEW 狀態可以轉換到 SCREENING")
        void shouldTransitionFromNewToScreening() {
            // Given
            Candidate candidate = Candidate.create(OPENING_ID, FULL_NAME, EMAIL, PHONE, SOURCE);

            // When
            candidate.passScreening();

            // Then
            assertEquals(CandidateStatus.SCREENING, candidate.getStatus());
        }

        @Test
        @DisplayName("非 NEW 狀態無法轉換到 SCREENING")
        void shouldNotTransitionToScreeningFromNonNewStatus() {
            // Given
            Candidate candidate = Candidate.create(OPENING_ID, FULL_NAME, EMAIL, PHONE, SOURCE);
            candidate.passScreening(); // 現在是 SCREENING

            // When & Then
            assertThrows(IllegalStateException.class, () -> candidate.passScreening());
        }
    }

    @Nested
    @DisplayName("狀態轉換: 進入面試")
    class InterviewingTests {

        @Test
        @DisplayName("SCREENING 狀態可以轉換到 INTERVIEWING")
        void shouldTransitionFromScreeningToInterviewing() {
            // Given
            Candidate candidate = createCandidateWithStatus(CandidateStatus.SCREENING);

            // When
            candidate.moveToInterview();

            // Then
            assertEquals(CandidateStatus.INTERVIEWING, candidate.getStatus());
        }

        @Test
        @DisplayName("NEW 狀態無法直接轉換到 INTERVIEWING")
        void shouldNotTransitionToInterviewingFromNew() {
            // Given
            Candidate candidate = Candidate.create(OPENING_ID, FULL_NAME, EMAIL, PHONE, SOURCE);

            // When & Then
            assertThrows(IllegalStateException.class, () -> candidate.moveToInterview());
        }
    }

    @Nested
    @DisplayName("狀態轉換: 發送Offer")
    class OfferTests {

        @Test
        @DisplayName("INTERVIEWING 狀態可以轉換到 OFFERED")
        void shouldTransitionFromInterviewingToOffered() {
            // Given
            Candidate candidate = createCandidateWithStatus(CandidateStatus.INTERVIEWING);

            // When
            candidate.sendOffer();

            // Then
            assertEquals(CandidateStatus.OFFERED, candidate.getStatus());
        }

        @Test
        @DisplayName("非 INTERVIEWING 狀態無法轉換到 OFFERED")
        void shouldNotTransitionToOfferedFromNonInterviewing() {
            // Given
            Candidate candidate = createCandidateWithStatus(CandidateStatus.SCREENING);

            // When & Then
            assertThrows(IllegalStateException.class, () -> candidate.sendOffer());
        }
    }

    @Nested
    @DisplayName("狀態轉換: 錄取")
    class HireTests {

        @Test
        @DisplayName("OFFERED 狀態可以轉換到 HIRED")
        void shouldTransitionFromOfferedToHired() {
            // Given
            Candidate candidate = createCandidateWithStatus(CandidateStatus.OFFERED);

            // When
            candidate.hire();

            // Then
            assertEquals(CandidateStatus.HIRED, candidate.getStatus());
        }

        @Test
        @DisplayName("錄取時應發布 CandidateHiredEvent")
        void shouldPublishCandidateHiredEventWhenHired() {
            // Given
            Candidate candidate = createCandidateWithStatus(CandidateStatus.OFFERED);

            // When
            candidate.hire();

            // Then
            assertEquals(1, candidate.getDomainEvents().size());
            assertTrue(candidate.getDomainEvents()
                    .get(0) instanceof CandidateHiredEvent);
        }

        @Test
        @DisplayName("非 OFFERED 狀態無法錄取")
        void shouldNotHireFromNonOfferedStatus() {
            // Given
            Candidate candidate = createCandidateWithStatus(CandidateStatus.INTERVIEWING);

            // When & Then
            assertThrows(IllegalStateException.class, () -> candidate.hire());
        }
    }

    @Nested
    @DisplayName("狀態轉換: 拒絕")
    class RejectTests {

        @Test
        @DisplayName("任何非終態狀態都可以被拒絕")
        void shouldRejectFromAnyNonFinalStatus() {
            // Given
            Candidate candidate = createCandidateWithStatus(CandidateStatus.SCREENING);

            // When
            candidate.reject("不符合職位要求");

            // Then
            assertEquals(CandidateStatus.REJECTED, candidate.getStatus());
            assertEquals("不符合職位要求", candidate.getRejectionReason());
        }

        @Test
        @DisplayName("已錄取狀態無法被拒絕")
        void shouldNotRejectFromHiredStatus() {
            // Given
            Candidate candidate = createCandidateWithStatus(CandidateStatus.HIRED);

            // When & Then
            assertThrows(IllegalStateException.class, () -> candidate.reject("理由"));
        }

        @Test
        @DisplayName("已拒絕狀態無法再次被拒絕")
        void shouldNotRejectFromRejectedStatus() {
            // Given
            Candidate candidate = createCandidateWithStatus(CandidateStatus.REJECTED);

            // When & Then
            assertThrows(IllegalStateException.class, () -> candidate.reject("理由"));
        }
    }

    // === 測試輔助方法 ===

    /**
     * 建立指定狀態的應徵者（用於測試）
     */
    private Candidate createCandidateWithStatus(CandidateStatus status) {
        Candidate candidate = Candidate.create(OPENING_ID, FULL_NAME, EMAIL, PHONE, SOURCE);

        // 使用狀態轉換方法達到目標狀態
        if (status == CandidateStatus.NEW) {
            return candidate;
        }

        candidate.passScreening();
        if (status == CandidateStatus.SCREENING) {
            return candidate;
        }

        candidate.moveToInterview();
        if (status == CandidateStatus.INTERVIEWING) {
            return candidate;
        }

        candidate.sendOffer();
        if (status == CandidateStatus.OFFERED) {
            return candidate;
        }

        if (status == CandidateStatus.HIRED) {
            candidate.hire();
            candidate.clearDomainEvents(); // 清除事件以便測試
            return candidate;
        }

        if (status == CandidateStatus.REJECTED) {
            // 從 NEW 狀態直接拒絕
            Candidate rejected = Candidate.create(OPENING_ID, FULL_NAME, EMAIL, PHONE, SOURCE);
            rejected.reject("測試用");
            return rejected;
        }

        return candidate;
    }
}
