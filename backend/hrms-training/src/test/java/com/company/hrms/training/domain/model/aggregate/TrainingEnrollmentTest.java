package com.company.hrms.training.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;

/**
 * 訓練報名 Aggregate 單元測試
 * 覆蓋報名申請、核准、拒絕、取消、出席確認、完成訓練等生命週期
 */
class TrainingEnrollmentTest {

    private TrainingEnrollment enrollment;

    @BeforeEach
    void setUp() {
        enrollment = TrainingEnrollment.create(
                "course-001",
                "Java 進階課程",
                "emp-001",
                "王大明",
                "mgr-001",
                "李主管",
                new BigDecimal("8"),
                new BigDecimal("5000"),
                "提升技術能力",
                "備註");
    }

    // === 建立報名 ===

    @Test
    @DisplayName("建立報名 - 初始狀態為 REGISTERED")
    void create_shouldSetStatusToRegistered() {
        assertEquals(EnrollmentStatus.REGISTERED, enrollment.getStatus());
        assertEquals("course-001", enrollment.getCourseId());
        assertEquals("emp-001", enrollment.getEmployeeId());
        assertEquals("提升技術能力", enrollment.getReason());
        assertNotNull(enrollment.getId());
        assertNotNull(enrollment.getCreatedAt());
    }

    @Test
    @DisplayName("建立報名 - 應產生 EnrollmentCreatedEvent")
    void create_shouldRegisterEvent() {
        assertFalse(enrollment.getDomainEvents().isEmpty(),
                "建立報名應產生領域事件");
    }

    // === 核准 ===

    @Nested
    @DisplayName("核准報名")
    class ApproveTests {

        @Test
        @DisplayName("核准 - 從 REGISTERED 狀態轉為 APPROVED")
        void approve_fromRegistered_shouldSucceed() {
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");

            assertEquals(EnrollmentStatus.APPROVED, enrollment.getStatus());
            assertEquals("mgr-001", enrollment.getApprovedBy());
            assertNotNull(enrollment.getApprovedAt());
        }

        @Test
        @DisplayName("核准 - 已核准的報名不可再次核准")
        void approve_fromApproved_shouldThrow() {
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");

            assertThrows(IllegalStateException.class, () ->
                    enrollment.approve("mgr-002", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室"));
        }

        @Test
        @DisplayName("核准 - 已取消的報名不可核准")
        void approve_fromCancelled_shouldThrow() {
            enrollment.cancel("emp-001", "時間衝突", "Java 進階課程");

            assertThrows(IllegalStateException.class, () ->
                    enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室"));
        }
    }

    // === 拒絕 ===

    @Nested
    @DisplayName("拒絕報名")
    class RejectTests {

        @Test
        @DisplayName("拒絕 - 從 REGISTERED 狀態轉為 REJECTED")
        void reject_fromRegistered_shouldSucceed() {
            enrollment.reject("mgr-001", "名額已滿", "王大明", "wang@company.com", "Java 進階課程");

            assertEquals(EnrollmentStatus.REJECTED, enrollment.getStatus());
            assertEquals("mgr-001", enrollment.getRejectedBy());
            assertEquals("名額已滿", enrollment.getRejectReason());
            assertNotNull(enrollment.getRejectedAt());
        }

        @Test
        @DisplayName("拒絕 - 已核准的報名不可拒絕")
        void reject_fromApproved_shouldThrow() {
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");

            assertThrows(IllegalStateException.class, () ->
                    enrollment.reject("mgr-001", "名額已滿", "王大明", "wang@company.com", "Java 進階課程"));
        }
    }

    // === 取消 ===

    @Nested
    @DisplayName("取消報名")
    class CancelTests {

        @Test
        @DisplayName("取消 - 從 REGISTERED 狀態取消")
        void cancel_fromRegistered_shouldSucceed() {
            enrollment.cancel("emp-001", "時間衝突", "Java 進階課程");

            assertEquals(EnrollmentStatus.CANCELLED, enrollment.getStatus());
            assertEquals("emp-001", enrollment.getCancelledBy());
            assertEquals("時間衝突", enrollment.getCancelReason());
            assertNotNull(enrollment.getCancelledAt());
        }

        @Test
        @DisplayName("取消 - 從 APPROVED 狀態取消")
        void cancel_fromApproved_shouldSucceed() {
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");
            enrollment.cancel("emp-001", "臨時出差", "Java 進階課程");

            assertEquals(EnrollmentStatus.CANCELLED, enrollment.getStatus());
        }

        @Test
        @DisplayName("取消 - 已拒絕的報名不可取消")
        void cancel_fromRejected_shouldThrow() {
            enrollment.reject("mgr-001", "名額已滿", "王大明", "wang@company.com", "Java 進階課程");

            assertThrows(IllegalStateException.class, () ->
                    enrollment.cancel("emp-001", "不需要了", "Java 進階課程"));
        }

        @Test
        @DisplayName("取消 - 已完成的報名不可取消")
        void cancel_fromCompleted_shouldThrow() {
            // 走完整流程：核准 -> 出席 -> 完成
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");
            enrollment.confirmAttendance(true, new BigDecimal("8"), null);
            enrollment.complete(new BigDecimal("8"), new BigDecimal("85"), true, "很好", "王大明", "Java 進階課程", "TECHNICAL");

            assertThrows(IllegalStateException.class, () ->
                    enrollment.cancel("emp-001", "想取消", "Java 進階課程"));
        }
    }

    // === 出席確認 ===

    @Nested
    @DisplayName("出席確認")
    class AttendanceTests {

        @Test
        @DisplayName("出席確認 - 確認出席轉為 ATTENDED")
        void confirmAttendance_attended_shouldSucceed() {
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");
            enrollment.confirmAttendance(true, new BigDecimal("8"), "準時到達");

            assertEquals(EnrollmentStatus.ATTENDED, enrollment.getStatus());
            assertTrue(enrollment.isAttendance());
            assertEquals(new BigDecimal("8"), enrollment.getAttendedHours());
            assertNotNull(enrollment.getAttendedAt());
            assertEquals("準時到達", enrollment.getRemarks());
        }

        @Test
        @DisplayName("出席確認 - 未出席轉為 NO_SHOW")
        void confirmAttendance_noShow_shouldSucceed() {
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");
            enrollment.confirmAttendance(false, null, null);

            assertEquals(EnrollmentStatus.NO_SHOW, enrollment.getStatus());
            assertFalse(enrollment.isAttendance());
            assertEquals(BigDecimal.ZERO, enrollment.getAttendedHours());
        }

        @Test
        @DisplayName("出席確認 - 已完成的報名不可再確認出席")
        void confirmAttendance_fromCompleted_shouldThrow() {
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");
            enrollment.confirmAttendance(true, new BigDecimal("8"), null);
            enrollment.complete(new BigDecimal("8"), new BigDecimal("90"), true, null, "王大明", "Java 進階課程", "TECHNICAL");

            assertThrows(IllegalStateException.class, () ->
                    enrollment.confirmAttendance(true, new BigDecimal("8"), null));
        }
    }

    // === 完成訓練 ===

    @Nested
    @DisplayName("完成訓練")
    class CompleteTests {

        @Test
        @DisplayName("完成 - 從 ATTENDED 轉為 COMPLETED")
        void complete_fromAttended_shouldSucceed() {
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");
            enrollment.confirmAttendance(true, new BigDecimal("8"), null);
            enrollment.complete(new BigDecimal("8"), new BigDecimal("92"), true, "表現優異", "王大明", "Java 進階課程", "TECHNICAL");

            assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus());
            assertEquals(new BigDecimal("92"), enrollment.getScore());
            assertTrue(enrollment.getPassed());
            assertEquals("表現優異", enrollment.getFeedback());
            assertNotNull(enrollment.getCompletedAt());
        }

        @Test
        @DisplayName("完成 - 未出席不可完成")
        void complete_fromRegistered_shouldThrow() {
            assertThrows(IllegalStateException.class, () ->
                    enrollment.complete(new BigDecimal("8"), null, true, null, "王大明", "Java 進階課程", "TECHNICAL"));
        }

        @Test
        @DisplayName("完成 - passed 為 null 時預設為 true")
        void complete_passedNull_shouldDefaultToTrue() {
            enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");
            enrollment.confirmAttendance(true, new BigDecimal("8"), null);
            enrollment.complete(new BigDecimal("8"), new BigDecimal("60"), null, null, "王大明", "Java 進階課程", "TECHNICAL");

            assertTrue(enrollment.getPassed());
        }
    }

    // === 完整生命週期 ===

    @Test
    @DisplayName("完整流程 - REGISTERED -> APPROVED -> ATTENDED -> COMPLETED")
    void fullLifecycle_shouldSucceed() {
        assertEquals(EnrollmentStatus.REGISTERED, enrollment.getStatus());

        enrollment.approve("mgr-001", "Java 進階課程", "王大明", "wang@company.com", "2026-04-01", "台北教室");
        assertEquals(EnrollmentStatus.APPROVED, enrollment.getStatus());

        enrollment.confirmAttendance(true, new BigDecimal("8"), null);
        assertEquals(EnrollmentStatus.ATTENDED, enrollment.getStatus());

        enrollment.complete(new BigDecimal("8"), new BigDecimal("95"), true, "優秀", "王大明", "Java 進階課程", "TECHNICAL");
        assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus());
    }
}
