package com.company.hrms.attendance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;

/**
 * LeaveApplication 聚合根單元測試
 * 遵循 TDD 原則，測試請假申請業務邏輯
 */
@DisplayName("LeaveApplication 聚合根測試")
class LeaveApplicationTest {

    private static final String EMPLOYEE_ID = "EMP-001";
    private static final LeaveTypeId LEAVE_TYPE_ID = new LeaveTypeId("annual-leave");

    // ==================== Constructor Tests ====================

    @Nested
    @DisplayName("建立請假申請")
    class ConstructorTests {

        @Test
        @DisplayName("應成功建立請假申請")
        void shouldCreateLeaveApplication() {
            // When
            LeaveApplication application = createTestApplication();

            // Then
            assertNotNull(application.getId());
            assertEquals(EMPLOYEE_ID, application.getEmployeeId());
            assertEquals(ApplicationStatus.PENDING, application.getStatus());
        }

        @Test
        @DisplayName("結束日期早於開始日期時應拋出例外")
        void shouldThrowExceptionWhenEndDateBeforeStartDate() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> new LeaveApplication(
                    new ApplicationId("app-001"),
                    EMPLOYEE_ID,
                    LEAVE_TYPE_ID,
                    LocalDate.now().plusDays(5),
                    LocalDate.now(), // 結束日期早於開始日期
                    LeavePeriodType.FULL_DAY,
                    LeavePeriodType.FULL_DAY,
                    "測試"));
        }
    }

    // ==================== Approval Tests ====================

    @Nested
    @DisplayName("請假審核")
    class ApprovalTests {

        @Test
        @DisplayName("應成功核准請假申請")
        void shouldApproveApplication() {
            // Given
            LeaveApplication application = createTestApplication();

            // When
            application.approve();

            // Then
            assertEquals(ApplicationStatus.APPROVED, application.getStatus());
        }

        @Test
        @DisplayName("應成功拒絕請假申請")
        void shouldRejectApplication() {
            // Given
            LeaveApplication application = createTestApplication();

            // When
            application.reject("假期餘額不足");

            // Then
            assertEquals(ApplicationStatus.REJECTED, application.getStatus());
            assertEquals("假期餘額不足", application.getRejectionReason());
        }

        @Test
        @DisplayName("非待審核狀態核准應拋出例外")
        void shouldThrowExceptionWhenApprovingNonPendingApplication() {
            // Given
            LeaveApplication application = createTestApplication();
            application.approve();

            // When & Then
            assertThrows(IllegalStateException.class, application::approve);
        }
    }

    // ==================== Cancellation Tests ====================

    @Nested
    @DisplayName("請假取消")
    class CancellationTests {

        @Test
        @DisplayName("應成功取消待審核的請假申請")
        void shouldCancelPendingApplication() {
            // Given
            LeaveApplication application = createTestApplication();

            // When
            application.cancel();

            // Then
            assertEquals(ApplicationStatus.CANCELLED, application.getStatus());
        }

        @Test
        @DisplayName("已核准的申請取消應拋出例外")
        void shouldThrowExceptionWhenCancellingApprovedApplication() {
            // Given
            LeaveApplication application = createTestApplication();
            application.approve();

            // When & Then
            assertThrows(IllegalStateException.class, application::cancel);
        }
    }

    // ==================== Helper Methods ====================

    private LeaveApplication createTestApplication() {
        return new LeaveApplication(
                new ApplicationId(java.util.UUID.randomUUID().toString()),
                EMPLOYEE_ID,
                LEAVE_TYPE_ID,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                LeavePeriodType.FULL_DAY,
                LeavePeriodType.FULL_DAY,
                "年度休假");
    }
}
