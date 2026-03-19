package com.company.hrms.training.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.training.domain.model.valueobject.CourseCategory;
import com.company.hrms.training.domain.model.valueobject.CourseStatus;
import com.company.hrms.training.domain.model.valueobject.CourseType;
import com.company.hrms.training.domain.model.valueobject.DeliveryMode;

/**
 * 訓練課程 Aggregate 擴展測試
 * 覆蓋建立驗證、狀態轉換、canEnroll 邏輯、更新限制等
 */
class TrainingCourseExtendedTest {

    private TrainingCourse createDraftCourse() {
        return TrainingCourse.create(
                "C001", "Spring Boot 實戰",
                CourseType.INTERNAL, DeliveryMode.OFFLINE,
                new BigDecimal("16"),
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                "admin");
    }

    // === 建立驗證 ===

    @Nested
    @DisplayName("建立課程驗證")
    class CreateValidationTests {

        @Test
        @DisplayName("建立課程 - 初始狀態為 DRAFT")
        void create_shouldSetDraftStatus() {
            TrainingCourse course = createDraftCourse();
            assertEquals(CourseStatus.DRAFT, course.getStatus());
            assertEquals(0, course.getCurrentEnrollments());
            assertNotNull(course.getId());
        }

        @Test
        @DisplayName("建立課程 - 應產生 CourseCreatedEvent")
        void create_shouldRegisterEvent() {
            TrainingCourse course = createDraftCourse();
            assertFalse(course.getDomainEvents().isEmpty());
        }

        @Test
        @DisplayName("建立課程 - 名稱為空應拋出例外")
        void create_emptyName_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    TrainingCourse.create("C001", "", CourseType.INTERNAL, DeliveryMode.ONLINE,
                            new BigDecimal("8"), LocalDate.now().plusDays(5), LocalDate.now().plusDays(6), "admin"));
        }

        @Test
        @DisplayName("建立課程 - 名稱超過 255 字元應拋出例外")
        void create_tooLongName_shouldThrow() {
            String longName = "A".repeat(256);
            assertThrows(IllegalArgumentException.class, () ->
                    TrainingCourse.create("C001", longName, CourseType.INTERNAL, DeliveryMode.ONLINE,
                            new BigDecimal("8"), LocalDate.now().plusDays(5), LocalDate.now().plusDays(6), "admin"));
        }

        @Test
        @DisplayName("建立課程 - 結束日期早於開始日期應拋出例外")
        void create_endBeforeStart_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    TrainingCourse.create("C001", "課程", CourseType.INTERNAL, DeliveryMode.ONLINE,
                            new BigDecimal("8"), LocalDate.now().plusDays(10), LocalDate.now().plusDays(5), "admin"));
        }

        @Test
        @DisplayName("建立課程 - 開始日期為過去應拋出例外")
        void create_pastStartDate_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    TrainingCourse.create("C001", "課程", CourseType.INTERNAL, DeliveryMode.ONLINE,
                            new BigDecimal("8"), LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), "admin"));
        }

        @Test
        @DisplayName("建立課程 - 時數為零應拋出例外")
        void create_zeroDuration_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    TrainingCourse.create("C001", "課程", CourseType.INTERNAL, DeliveryMode.ONLINE,
                            BigDecimal.ZERO, LocalDate.now().plusDays(5), LocalDate.now().plusDays(6), "admin"));
        }

        @Test
        @DisplayName("建立課程 - 時數為負數應拋出例外")
        void create_negativeDuration_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    TrainingCourse.create("C001", "課程", CourseType.INTERNAL, DeliveryMode.ONLINE,
                            new BigDecimal("-1"), LocalDate.now().plusDays(5), LocalDate.now().plusDays(6), "admin"));
        }

        @Test
        @DisplayName("建立課程 - 日期為 null 應拋出例外")
        void create_nullDates_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    TrainingCourse.create("C001", "課程", CourseType.INTERNAL, DeliveryMode.ONLINE,
                            new BigDecimal("8"), null, LocalDate.now().plusDays(6), "admin"));
        }
    }

    // === 發布 ===

    @Nested
    @DisplayName("發布課程")
    class PublishTests {

        @Test
        @DisplayName("發布 - DRAFT 轉為 OPEN")
        void publish_fromDraft_shouldSucceed() {
            TrainingCourse course = createDraftCourse();
            course.publish();
            assertEquals(CourseStatus.OPEN, course.getStatus());
        }

        @Test
        @DisplayName("發布 - 非 DRAFT 狀態不可發布")
        void publish_fromOpen_shouldThrow() {
            TrainingCourse course = createDraftCourse();
            course.publish();
            assertThrows(IllegalStateException.class, course::publish);
        }
    }

    // === 關閉 ===

    @Nested
    @DisplayName("關閉課程")
    class CloseTests {

        @Test
        @DisplayName("關閉 - OPEN 轉為 CLOSED")
        void close_fromOpen_shouldSucceed() {
            TrainingCourse course = createDraftCourse();
            course.publish();
            course.close("報名截止");
            assertEquals(CourseStatus.CLOSED, course.getStatus());
        }

        @Test
        @DisplayName("關閉 - 非 OPEN 狀態不可關閉")
        void close_fromDraft_shouldThrow() {
            TrainingCourse course = createDraftCourse();
            assertThrows(IllegalStateException.class, () -> course.close("不能關閉"));
        }
    }

    // === canEnroll ===

    @Nested
    @DisplayName("報名判斷")
    class CanEnrollTests {

        @Test
        @DisplayName("canEnroll - OPEN 且無限制應可報名")
        void canEnroll_openNoLimit_shouldReturnTrue() {
            TrainingCourse course = createDraftCourse();
            course.publish();
            assertTrue(course.canEnroll());
        }

        @Test
        @DisplayName("canEnroll - DRAFT 不可報名")
        void canEnroll_draft_shouldReturnFalse() {
            TrainingCourse course = createDraftCourse();
            assertFalse(course.canEnroll());
        }

        @Test
        @DisplayName("canEnroll - 已達人數上限不可報名")
        void canEnroll_full_shouldReturnFalse() {
            TrainingCourse course = createDraftCourse();
            course.publish();
            course.setMaxParticipants(1);
            course.incrementEnrollmentCount();
            assertFalse(course.canEnroll());
        }
    }

    // === 更新限制 ===

    @Nested
    @DisplayName("更新課程限制")
    class UpdateTests {

        @Test
        @DisplayName("更新 - DRAFT 狀態可更新")
        void updateInfo_draft_shouldSucceed() {
            TrainingCourse course = createDraftCourse();
            course.updateInfo("新名稱", "描述", 30, "會議室 A");
            assertEquals("新名稱", course.getCourseName());
        }

        @Test
        @DisplayName("更新 - COMPLETED 狀態不可更新")
        void updateInfo_completed_shouldThrow() {
            TrainingCourse course = createDraftCourse();
            course.publish();
            course.close("截止");
            // 完成需要 endDate 在過去，使用 reconstitute 建立
            TrainingCourse completedCourse = TrainingCourse.reconstitute(
                    course.getId(), "C001", "課程", CourseType.INTERNAL, DeliveryMode.ONLINE,
                    CourseCategory.TECHNICAL, "描述", "講師", null,
                    new BigDecimal("8"), 30, 5, 10,
                    LocalDate.now().minusDays(5), LocalDate.now().minusDays(1),
                    null, null, "教室", null, false, null, null, null,
                    CourseStatus.COMPLETED, "admin", null, null);

            assertThrows(IllegalStateException.class, () ->
                    completedCourse.updateInfo("新名稱", null, null, null));
        }

        @Test
        @DisplayName("更新 - maxParticipants 不可少於已報名人數")
        void updateInfo_maxLessThanCurrent_shouldThrow() {
            TrainingCourse course = createDraftCourse();
            course.publish();
            course.incrementEnrollmentCount();
            course.incrementEnrollmentCount();

            assertThrows(IllegalArgumentException.class, () ->
                    course.updateInfo(null, null, 1, null));
        }

        @Test
        @DisplayName("設定報名截止日晚於開始日應拋出例外")
        void setEnrollmentDeadline_afterStartDate_shouldThrow() {
            TrainingCourse course = createDraftCourse();
            assertThrows(IllegalArgumentException.class, () ->
                    course.setEnrollmentDeadline(course.getStartDate().plusDays(1)));
        }

        @Test
        @DisplayName("CLOSED 狀態不可設定屬性")
        void setCategory_closed_shouldThrow() {
            TrainingCourse course = createDraftCourse();
            course.publish();
            course.close("截止");
            assertThrows(IllegalStateException.class, () ->
                    course.setCategory(CourseCategory.TECHNICAL));
        }
    }
}
