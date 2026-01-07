package com.company.hrms.recruitment.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewStatus;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewType;
import com.company.hrms.recruitment.domain.model.valueobject.OverallRating;

/**
 * 面試聚合根測試
 */
@DisplayName("Interview 面試聚合根測試")
class InterviewTest {

    private static final CandidateId CANDIDATE_ID = CandidateId.create();
    private static final String CANDIDATE_NAME = "王小明";
    private static final int INTERVIEW_ROUND = 1;
    private static final InterviewType INTERVIEW_TYPE = InterviewType.TECHNICAL;
    private static final LocalDateTime INTERVIEW_DATE = LocalDateTime.now().plusDays(3);
    private static final String LOCATION = "會議室 A";
    private static final UUID INTERVIEWER_ID = UUID.randomUUID();

    @Nested
    @DisplayName("安排面試")
    class ScheduleTests {

        @Test
        @DisplayName("應成功安排面試，狀態為 SCHEDULED")
        void shouldScheduleInterview() {
            // When
            Interview interview = Interview.schedule(
                    CANDIDATE_ID, CANDIDATE_NAME, INTERVIEW_ROUND,
                    INTERVIEW_TYPE, INTERVIEW_DATE, LOCATION,
                    List.of(INTERVIEWER_ID));

            // Then
            assertNotNull(interview.getId());
            assertEquals(CANDIDATE_ID, interview.getCandidateId());
            assertEquals(CANDIDATE_NAME, interview.getCandidateName());
            assertEquals(INTERVIEW_ROUND, interview.getInterviewRound());
            assertEquals(INTERVIEW_TYPE, interview.getInterviewType());
            assertEquals(INTERVIEW_DATE, interview.getInterviewDate());
            assertEquals(InterviewStatus.SCHEDULED, interview.getStatus());
            assertTrue(interview.getDomainEvents().size() > 0);
        }

        @Test
        @DisplayName("沒有面試官時應拋出例外")
        void shouldThrowExceptionWhenNoInterviewers() {
            assertThrows(IllegalArgumentException.class,
                    () -> Interview.schedule(CANDIDATE_ID, CANDIDATE_NAME, INTERVIEW_ROUND,
                            INTERVIEW_TYPE, INTERVIEW_DATE, LOCATION, List.of()));
        }

        @Test
        @DisplayName("面試輪次小於1時應拋出例外")
        void shouldThrowExceptionWhenRoundLessThanOne() {
            assertThrows(IllegalArgumentException.class, () -> Interview.schedule(CANDIDATE_ID, CANDIDATE_NAME, 0,
                    INTERVIEW_TYPE, INTERVIEW_DATE, LOCATION, List.of(INTERVIEWER_ID)));
        }
    }

    @Nested
    @DisplayName("提交評估")
    class EvaluationTests {

        @Test
        @DisplayName("面試官可以提交評估")
        void shouldAddEvaluation() {
            // Given
            Interview interview = createScheduledInterview();

            // When
            interview.addEvaluation(
                    INTERVIEWER_ID, 4, 4, 5,
                    OverallRating.HIRE, "表現優秀", "技術扎實", null);

            // Then
            assertEquals(1, interview.getEvaluations().size());
            assertEquals(InterviewStatus.COMPLETED, interview.getStatus()); // 所有面試官都評估完成
        }

        @Test
        @DisplayName("非面試官無法提交評估")
        void shouldNotAllowNonInterviewerToEvaluate() {
            // Given
            Interview interview = createScheduledInterview();
            UUID otherPerson = UUID.randomUUID();

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> interview.addEvaluation(otherPerson, 4, 4, 5,
                    OverallRating.HIRE, "評論", null, null));
        }

        @Test
        @DisplayName("同一面試官無法重複評估")
        void shouldNotAllowDuplicateEvaluation() {
            // Given
            Interview interview = createScheduledInterview();
            interview.addEvaluation(INTERVIEWER_ID, 4, 4, 5,
                    OverallRating.HIRE, "評論", null, null);

            // When & Then
            assertThrows(IllegalStateException.class, () -> interview.addEvaluation(INTERVIEWER_ID, 3, 3, 3,
                    OverallRating.NO_HIRE, "第二次評論", null, null));
        }

        @Test
        @DisplayName("分數超出範圍應拋出例外")
        void shouldThrowExceptionWhenScoreOutOfRange() {
            // Given
            Interview interview = createScheduledInterview();

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> interview.addEvaluation(INTERVIEWER_ID, 10, 4, 5,
                    OverallRating.HIRE, "評論", null, null));
        }
    }

    @Nested
    @DisplayName("取消面試")
    class CancelTests {

        @Test
        @DisplayName("已排程面試可以取消")
        void shouldCancelScheduledInterview() {
            // Given
            Interview interview = createScheduledInterview();
            interview.clearDomainEvents();

            // When
            interview.cancel();

            // Then
            assertEquals(InterviewStatus.CANCELLED, interview.getStatus());
        }

        @Test
        @DisplayName("已完成面試無法取消")
        void shouldNotCancelCompletedInterview() {
            // Given
            Interview interview = createScheduledInterview();
            interview.complete();

            // When & Then
            assertThrows(IllegalStateException.class, () -> interview.cancel());
        }
    }

    @Nested
    @DisplayName("重新排程")
    class RescheduleTests {

        @Test
        @DisplayName("可以重新排程面試")
        void shouldRescheduleInterview() {
            // Given
            Interview interview = createScheduledInterview();
            LocalDateTime newDate = LocalDateTime.now().plusDays(5);

            // When
            interview.reschedule(newDate, "新會議室");

            // Then
            assertEquals(newDate, interview.getInterviewDate());
            assertEquals("新會議室", interview.getLocation());
        }
    }

    // === 測試輔助方法 ===

    private Interview createScheduledInterview() {
        return Interview.schedule(
                CANDIDATE_ID, CANDIDATE_NAME, INTERVIEW_ROUND,
                INTERVIEW_TYPE, INTERVIEW_DATE, LOCATION,
                List.of(INTERVIEWER_ID));
    }
}
