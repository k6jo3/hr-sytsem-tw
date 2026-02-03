package com.company.hrms.attendance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;

/**
 * AttendanceRecord 聚合根單元測試
 * 遵循 TDD 原則，測試所有打卡業務邏輯
 */
@DisplayName("AttendanceRecord 聚合根測試")
class AttendanceRecordTest {

    private static final String EMPLOYEE_ID = "EMP-001";
    private static final LocalDate TODAY = LocalDate.now();

    // ==================== Constructor Tests ====================

    @Nested
    @DisplayName("建立打卡記錄")
    class ConstructorTests {

        @Test
        @DisplayName("應成功建立打卡記錄，並產生唯一 ID")
        void shouldCreateRecordWithUniqueId() {
            // When
            AttendanceRecord record = createTestRecord();

            // Then
            assertNotNull(record.getId());
            assertNotNull(record.getId().getValue());
            assertEquals(EMPLOYEE_ID, record.getEmployeeId());
            assertEquals(TODAY, record.getDate());
        }

        @Test
        @DisplayName("員工 ID 為空時應拋出例外")
        void shouldThrowExceptionWhenEmployeeIdIsBlank() {
            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> new AttendanceRecord(new RecordId("rec-001"), "", TODAY));
        }
    }

    // ==================== Check-In Tests ====================

    @Nested
    @DisplayName("上班打卡")
    class CheckInTests {

        @Test
        @DisplayName("準時打卡應標記為非遲到")
        void shouldMarkAsNotLateWhenOnTime() {
            // Given
            AttendanceRecord record = createTestRecord();
            Shift shift = createRegularShift(); // 09:00 開始
            LocalDateTime checkInTime = LocalDateTime.of(TODAY, LocalTime.of(8, 55)); // 08:55

            // When
            record.checkIn(checkInTime, shift);

            // Then
            assertEquals(checkInTime, record.getCheckInTime());
            assertFalse(record.isLate());
            assertEquals(0, record.getLateMinutes());
        }

        @Test
        @DisplayName("遲到打卡應計算遲到分鐘數")
        void shouldCalculateLateMinutes() {
            // Given
            AttendanceRecord record = createTestRecord();
            Shift shift = createRegularShift(); // 09:00 開始
            LocalDateTime checkInTime = LocalDateTime.of(TODAY, LocalTime.of(9, 15)); // 09:15 遲到15分鐘

            // When
            record.checkIn(checkInTime, shift);

            // Then
            assertTrue(record.isLate());
            assertEquals(15, record.getLateMinutes());
        }

        @Test
        @DisplayName("在容許時間內打卡應標記為非遲到")
        void shouldNotBeLateWithinTolerance() {
            // Given
            AttendanceRecord record = createTestRecord();
            Shift shift = createRegularShift(); // 09:00 開始, 5分鐘容許
            LocalDateTime checkInTime = LocalDateTime.of(TODAY, LocalTime.of(9, 5)); // 09:05

            // When
            record.checkIn(checkInTime, shift);

            // Then
            assertFalse(record.isLate());
            assertEquals(0, record.getLateMinutes());
        }
    }

    // ==================== Check-Out Tests ====================

    @Nested
    @DisplayName("下班打卡")
    class CheckOutTests {

        @Test
        @DisplayName("準時下班應標記為非早退")
        void shouldMarkAsNotEarlyLeaveWhenOnTime() {
            // Given
            AttendanceRecord record = createTestRecord();
            Shift shift = createRegularShift(); // 18:00 結束
            record.checkIn(LocalDateTime.of(TODAY, LocalTime.of(9, 0)), shift);
            LocalDateTime checkOutTime = LocalDateTime.of(TODAY, LocalTime.of(18, 5));

            // When
            record.checkOut(checkOutTime, shift);

            // Then
            assertEquals(checkOutTime, record.getCheckOutTime());
            assertFalse(record.isEarlyLeave());
            assertEquals(0, record.getEarlyLeaveMinutes());
        }

        @Test
        @DisplayName("早退應計算早退分鐘數")
        void shouldCalculateEarlyLeaveMinutes() {
            // Given
            AttendanceRecord record = createTestRecord();
            Shift shift = createRegularShift(); // 18:00 結束
            record.checkIn(LocalDateTime.of(TODAY, LocalTime.of(9, 0)), shift);
            LocalDateTime checkOutTime = LocalDateTime.of(TODAY, LocalTime.of(17, 30)); // 17:30 早退30分鐘

            // When
            record.checkOut(checkOutTime, shift);

            // Then
            assertTrue(record.isEarlyLeave());
            assertEquals(30, record.getEarlyLeaveMinutes());
        }
    }

    // ==================== Helper Methods ====================

    private AttendanceRecord createTestRecord() {
        return new AttendanceRecord(
                new RecordId(java.util.UUID.randomUUID().toString()),
                EMPLOYEE_ID,
                TODAY);
    }

    private Shift createRegularShift() {
        Shift shift = new Shift(
                new ShiftId("shift-001"),
                "ORG-001",
                "S001",
                "常規班",
                ShiftType.REGULAR,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0));
        shift.setTolerances(5, 5); // 5分鐘遲到/早退容許
        return shift;
    }
}
