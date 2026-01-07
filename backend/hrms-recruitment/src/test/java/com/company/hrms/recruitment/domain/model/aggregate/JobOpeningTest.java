package com.company.hrms.recruitment.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.recruitment.domain.model.valueobject.JobStatus;
import com.company.hrms.recruitment.domain.model.valueobject.SalaryRange;

/**
 * 職缺聚合根測試
 */
@DisplayName("JobOpening 職缺聚合根測試")
class JobOpeningTest {

    private static final UUID DEPARTMENT_ID = UUID.randomUUID();
    private static final String JOB_TITLE = "前端工程師";
    private static final int NUMBER_OF_POSITIONS = 2;

    @Nested
    @DisplayName("建立職缺")
    class CreateJobOpeningTests {

        @Test
        @DisplayName("應成功建立新職缺，狀態為 DRAFT")
        void shouldCreateJobOpeningWithDraftStatus() {
            // When
            JobOpening job = JobOpening.create(JOB_TITLE, DEPARTMENT_ID, NUMBER_OF_POSITIONS);

            // Then
            assertNotNull(job.getId());
            assertEquals(JOB_TITLE, job.getJobTitle());
            assertEquals(DEPARTMENT_ID, job.getDepartmentId());
            assertEquals(NUMBER_OF_POSITIONS, job.getNumberOfPositions());
            assertEquals(JobStatus.DRAFT, job.getStatus());
            assertEquals(0, job.getFilledPositions());
        }

        @Test
        @DisplayName("職位名稱為空時應拋出例外")
        void shouldThrowExceptionWhenTitleIsEmpty() {
            assertThrows(IllegalArgumentException.class,
                    () -> JobOpening.create("", DEPARTMENT_ID, NUMBER_OF_POSITIONS));
        }

        @Test
        @DisplayName("職位名稱過長時應拋出例外")
        void shouldThrowExceptionWhenTitleTooLong() {
            String longTitle = "A".repeat(101);
            assertThrows(IllegalArgumentException.class,
                    () -> JobOpening.create(longTitle, DEPARTMENT_ID, NUMBER_OF_POSITIONS));
        }

        @Test
        @DisplayName("需求人數小於1時應拋出例外")
        void shouldThrowExceptionWhenPositionsLessThanOne() {
            assertThrows(IllegalArgumentException.class, () -> JobOpening.create(JOB_TITLE, DEPARTMENT_ID, 0));
        }

        @Test
        @DisplayName("部門ID為空時應拋出例外")
        void shouldThrowExceptionWhenDepartmentIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> JobOpening.create(JOB_TITLE, null, NUMBER_OF_POSITIONS));
        }
    }

    @Nested
    @DisplayName("狀態轉換: 發布")
    class PublishTests {

        @Test
        @DisplayName("DRAFT 狀態可以發布為 OPEN")
        void shouldPublishFromDraft() {
            // Given
            JobOpening job = JobOpening.create(JOB_TITLE, DEPARTMENT_ID, NUMBER_OF_POSITIONS);

            // When
            job.publish();

            // Then
            assertEquals(JobStatus.OPEN, job.getStatus());
            assertNotNull(job.getOpenDate());
        }

        @Test
        @DisplayName("非 DRAFT 狀態無法發布")
        void shouldNotPublishFromNonDraftStatus() {
            // Given
            JobOpening job = JobOpening.create(JOB_TITLE, DEPARTMENT_ID, NUMBER_OF_POSITIONS);
            job.publish(); // 現在是 OPEN

            // When & Then
            assertThrows(IllegalStateException.class, () -> job.publish());
        }
    }

    @Nested
    @DisplayName("狀態轉換: 關閉")
    class CloseTests {

        @Test
        @DisplayName("OPEN 狀態可以關閉")
        void shouldCloseFromOpen() {
            // Given
            JobOpening job = createOpenJob();

            // When
            job.close("已招滿");

            // Then
            assertEquals(JobStatus.CLOSED, job.getStatus());
            assertNotNull(job.getCloseDate());
            assertEquals("已招滿", job.getCloseReason());
        }

        @Test
        @DisplayName("DRAFT 狀態無法關閉")
        void shouldNotCloseFromDraft() {
            // Given
            JobOpening job = JobOpening.create(JOB_TITLE, DEPARTMENT_ID, NUMBER_OF_POSITIONS);

            // When & Then
            assertThrows(IllegalStateException.class, () -> job.close("理由"));
        }
    }

    @Nested
    @DisplayName("狀態轉換: 滿額")
    class FillTests {

        @Test
        @DisplayName("已錄取人數等於需求人數時應標記為 FILLED")
        void shouldMarkFilledWhenPositionsFilled() {
            // Given
            JobOpening job = createOpenJob();

            // When
            job.incrementFilledPositions();
            job.incrementFilledPositions();

            // Then
            assertEquals(2, job.getFilledPositions());
            assertEquals(JobStatus.FILLED, job.getStatus());
        }

        @Test
        @DisplayName("已關閉狀態無法增加錄取人數")
        void shouldNotIncrementWhenClosed() {
            // Given
            JobOpening job = createOpenJob();
            job.close("終止招募");

            // When & Then
            assertThrows(IllegalStateException.class, () -> job.incrementFilledPositions());
        }
    }

    @Nested
    @DisplayName("更新職缺資訊")
    class UpdateTests {

        @Test
        @DisplayName("設定薪資範圍")
        void shouldSetSalaryRange() {
            // Given
            JobOpening job = JobOpening.create(JOB_TITLE, DEPARTMENT_ID, NUMBER_OF_POSITIONS);
            SalaryRange range = SalaryRange.of(new BigDecimal("60000"), new BigDecimal("80000"));

            // When
            job.setSalaryRange(range);

            // Then
            assertEquals(range, job.getSalaryRange());
        }

        @Test
        @DisplayName("設定職位要求和職責")
        void shouldSetRequirementsAndResponsibilities() {
            // Given
            JobOpening job = JobOpening.create(JOB_TITLE, DEPARTMENT_ID, NUMBER_OF_POSITIONS);

            // When
            job.setRequirements("React 3年以上經驗");
            job.setResponsibilities("開發前端功能");

            // Then
            assertEquals("React 3年以上經驗", job.getRequirements());
            assertEquals("開發前端功能", job.getResponsibilities());
        }

        @Test
        @DisplayName("CLOSED 狀態無法更新")
        void shouldNotUpdateWhenClosed() {
            // Given
            JobOpening job = createOpenJob();
            job.close("終止");

            // When & Then
            assertThrows(IllegalStateException.class, () -> job.setRequirements("新要求"));
        }
    }

    // === 測試輔助方法 ===

    private JobOpening createOpenJob() {
        JobOpening job = JobOpening.create(JOB_TITLE, DEPARTMENT_ID, NUMBER_OF_POSITIONS);
        job.publish();
        return job;
    }
}
