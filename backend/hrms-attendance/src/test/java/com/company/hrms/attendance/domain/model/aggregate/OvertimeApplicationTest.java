package com.company.hrms.attendance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;

@DisplayName("OvertimeApplication 領域測試")
class OvertimeApplicationTest {

    private OvertimeApplication application;

    @BeforeEach
    void setUp() {
        application = new OvertimeApplication(
                new OvertimeId("ot-001"),
                "EMP-001",
                LocalDate.now(),
                2.0,
                OvertimeType.WORKDAY,
                "專案趕工");
    }

    @Nested
    @DisplayName("建構測試")
    class ConstructorTests {

        @Test
        @DisplayName("應成功建立加班申請")
        void shouldCreateSuccessfully() {
            assertNotNull(application);
            assertEquals("EMP-001", application.getEmployeeId());
            assertEquals(ApplicationStatus.PENDING, application.getStatus());
            assertEquals(2.0, application.getHours());
            assertEquals(OvertimeType.WORKDAY, application.getOvertimeType());
        }

        @Test
        @DisplayName("時數為零時應拋出例外")
        void shouldThrowWhenHoursIsZero() {
            assertThrows(IllegalArgumentException.class, () ->
                    new OvertimeApplication(
                            new OvertimeId("ot-002"),
                            "EMP-001",
                            LocalDate.now(),
                            0.0,
                            OvertimeType.WORKDAY,
                            "專案趕工"));
        }

        @Test
        @DisplayName("時數為負數時應拋出例外")
        void shouldThrowWhenHoursIsNegative() {
            assertThrows(IllegalArgumentException.class, () ->
                    new OvertimeApplication(
                            new OvertimeId("ot-002"),
                            "EMP-001",
                            LocalDate.now(),
                            -1.0,
                            OvertimeType.WORKDAY,
                            "專案趕工"));
        }

        @Test
        @DisplayName("時數為null時應拋出例外")
        void shouldThrowWhenHoursIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    new OvertimeApplication(
                            new OvertimeId("ot-002"),
                            "EMP-001",
                            LocalDate.now(),
                            null,
                            OvertimeType.WORKDAY,
                            "專案趕工"));
        }
    }

    @Nested
    @DisplayName("核准測試")
    class ApprovalTests {

        @Test
        @DisplayName("應成功核准待審核申請")
        void shouldApproveSuccessfully() {
            // When
            application.approve();

            // Then
            assertEquals(ApplicationStatus.APPROVED, application.getStatus());
        }

        @Test
        @DisplayName("已核准申請不可再次核准")
        void shouldThrowWhenAlreadyApproved() {
            // Given
            application.approve();

            // Then
            assertThrows(IllegalStateException.class, () -> application.approve());
        }

        @Test
        @DisplayName("已駁回申請不可核准")
        void shouldThrowWhenAlreadyRejected() {
            // Given
            application.reject("不需要加班");

            // Then
            assertThrows(IllegalStateException.class, () -> application.approve());
        }
    }

    @Nested
    @DisplayName("駁回測試")
    class RejectionTests {

        @Test
        @DisplayName("應成功駁回待審核申請")
        void shouldRejectSuccessfully() {
            // When
            application.reject("加班時數超過上限");

            // Then
            assertEquals(ApplicationStatus.REJECTED, application.getStatus());
            assertEquals("加班時數超過上限", application.getRejectionReason());
        }

        @Test
        @DisplayName("已核准申請不可駁回")
        void shouldThrowWhenAlreadyApproved() {
            // Given
            application.approve();

            // Then
            assertThrows(IllegalStateException.class, () -> application.reject("理由"));
        }

        @Test
        @DisplayName("已駁回申請不可再次駁回")
        void shouldThrowWhenAlreadyRejected() {
            // Given
            application.reject("第一次駁回");

            // Then
            assertThrows(IllegalStateException.class, () -> application.reject("第二次駁回"));
        }
    }

    @Nested
    @DisplayName("加班類型測試")
    class OvertimeTypeTests {

        @Test
        @DisplayName("應支援平日加班")
        void shouldSupportWorkdayOvertime() {
            OvertimeApplication app = new OvertimeApplication(
                    new OvertimeId("ot-002"),
                    "EMP-001",
                    LocalDate.now(),
                    2.0,
                    OvertimeType.WORKDAY,
                    "專案趕工");
            assertEquals(OvertimeType.WORKDAY, app.getOvertimeType());
        }

        @Test
        @DisplayName("應支援休息日加班")
        void shouldSupportRestDayOvertime() {
            OvertimeApplication app = new OvertimeApplication(
                    new OvertimeId("ot-003"),
                    "EMP-001",
                    LocalDate.now(),
                    4.0,
                    OvertimeType.REST_DAY,
                    "緊急支援");
            assertEquals(OvertimeType.REST_DAY, app.getOvertimeType());
        }

        @Test
        @DisplayName("應支援國定假日加班")
        void shouldSupportHolidayOvertime() {
            OvertimeApplication app = new OvertimeApplication(
                    new OvertimeId("ot-004"),
                    "EMP-001",
                    LocalDate.now(),
                    8.0,
                    OvertimeType.HOLIDAY,
                    "系統維護");
            assertEquals(OvertimeType.HOLIDAY, app.getOvertimeType());
        }
    }
}
