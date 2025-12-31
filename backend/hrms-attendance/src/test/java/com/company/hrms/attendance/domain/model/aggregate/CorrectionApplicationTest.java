package com.company.hrms.attendance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionId;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionType;

@DisplayName("CorrectionApplication 領域測試")
class CorrectionApplicationTest {

    private CorrectionApplication application;

    @BeforeEach
    void setUp() {
        application = new CorrectionApplication(
                new CorrectionId("corr-001"),
                "EMP-001",
                "REC-001",
                LocalDate.now(),
                CorrectionType.FORGET_CHECK_IN,
                LocalTime.of(9, 0),
                null,
                "忘記打卡");
    }

    @Nested
    @DisplayName("建構測試")
    class ConstructorTests {

        @Test
        @DisplayName("應成功建立補卡申請")
        void shouldCreateSuccessfully() {
            assertNotNull(application);
            assertEquals("EMP-001", application.getEmployeeId());
            assertEquals(ApplicationStatus.PENDING, application.getStatus());
        }

        @Test
        @DisplayName("員工ID不可為空")
        void shouldThrowWhenEmployeeIdIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    new CorrectionApplication(
                            new CorrectionId("corr-002"),
                            null,
                            "REC-001",
                            LocalDate.now(),
                            CorrectionType.FORGET_CHECK_IN,
                            LocalTime.of(9, 0),
                            null,
                            "忘記打卡"));
        }

        @Test
        @DisplayName("原因不可為空")
        void shouldThrowWhenReasonIsBlank() {
            assertThrows(IllegalArgumentException.class, () ->
                    new CorrectionApplication(
                            new CorrectionId("corr-002"),
                            "EMP-001",
                            "REC-001",
                            LocalDate.now(),
                            CorrectionType.FORGET_CHECK_IN,
                            LocalTime.of(9, 0),
                            null,
                            ""));
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
            application.reject("不合規定");

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
            application.reject("資料不完整");

            // Then
            assertEquals(ApplicationStatus.REJECTED, application.getStatus());
            assertEquals("資料不完整", application.getRejectionReason());
        }

        @Test
        @DisplayName("已核准申請不可駁回")
        void shouldThrowWhenAlreadyApproved() {
            // Given
            application.approve();

            // Then
            assertThrows(IllegalStateException.class, () -> application.reject("資料不完整"));
        }
    }
}
