package com.company.hrms.attendance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;

/**
 * Shift 聚合根單元測試
 * 遵循 TDD 原則，測試班別業務邏輯
 */
@DisplayName("Shift 聚合根測試")
class ShiftTest {

    // ==================== Constructor Tests ====================

    @Nested
    @DisplayName("建立班別")
    class ConstructorTests {

        @Test
        @DisplayName("應成功建立常規班別")
        void shouldCreateRegularShift() {
            // When
            Shift shift = createRegularShift();

            // Then
            assertNotNull(shift.getId());
            assertEquals("S001", shift.getCode());
            assertEquals("常規班", shift.getName());
            assertEquals(ShiftType.REGULAR, shift.getType());
            assertEquals(LocalTime.of(9, 0), shift.getWorkStartTime());
            assertEquals(LocalTime.of(18, 0), shift.getWorkEndTime());
        }

        @Test
        @DisplayName("名稱為空時應拋出例外")
        void shouldThrowExceptionWhenNameIsBlank() {
            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> new Shift(new ShiftId("shift-001"), "ORG001", "S001", "", ShiftType.REGULAR,
                            LocalTime.of(9, 0), LocalTime.of(18, 0)));
        }

        @Test
        @DisplayName("編碼為空時應拋出例外")
        void shouldThrowExceptionWhenCodeIsBlank() {
            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> new Shift(new ShiftId("shift-001"), "ORG001", "", "常規班", ShiftType.REGULAR,
                            LocalTime.of(9, 0), LocalTime.of(18, 0)));
        }
    }

    // ==================== Break Time Tests ====================

    @Nested
    @DisplayName("休息時間設定")
    class BreakTimeTests {

        @Test
        @DisplayName("應成功設定休息時間")
        void shouldSetBreakTime() {
            // Given
            Shift shift = createRegularShift();
            LocalTime breakStart = LocalTime.of(12, 0);
            LocalTime breakEnd = LocalTime.of(13, 0);

            // When
            shift.setBreakTime(breakStart, breakEnd);

            // Then
            assertEquals(breakStart, shift.getBreakStartTime());
            assertEquals(breakEnd, shift.getBreakEndTime());
        }

        @Test
        @DisplayName("休息結束時間早於開始時間時應拋出例外")
        void shouldThrowExceptionWhenBreakEndBeforeStart() {
            // Given
            Shift shift = createRegularShift();

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> shift.setBreakTime(LocalTime.of(13, 0), LocalTime.of(12, 0)));
        }
    }

    // ==================== Tolerance Tests ====================

    @Nested
    @DisplayName("遲到/早退容許設定")
    class ToleranceTests {

        @Test
        @DisplayName("應成功設定容許分鐘數")
        void shouldSetTolerances() {
            // Given
            Shift shift = createRegularShift();

            // When
            shift.setTolerances(10, 5);

            // Then
            assertEquals(10, shift.getLateToleranceMinutes());
            assertEquals(5, shift.getEarlyLeaveToleranceMinutes());
        }

        @Test
        @DisplayName("容許分鐘數為負數時應拋出例外")
        void shouldThrowExceptionWhenToleranceIsNegative() {
            // Given
            Shift shift = createRegularShift();

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> shift.setTolerances(-1, 5));
        }
    }

    // ==================== Helper Methods ====================

    private Shift createRegularShift() {
        return new Shift(
                new ShiftId("shift-001"),
                "ORG001",
                "S001",
                "常規班",
                ShiftType.REGULAR,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0));
    }
}
