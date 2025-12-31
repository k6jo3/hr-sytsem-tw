package com.company.hrms.attendance.application.service.checkout.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.attendance.CheckOutRequest;
import com.company.hrms.attendance.application.service.checkout.context.CheckOutContext;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidateCheckOutTask 測試")
class ValidateCheckOutTaskTest {

    @Mock
    private IAttendanceRecordRepository attendanceRecordRepository;

    @InjectMocks
    private ValidateCheckOutTask task;

    private CheckOutContext context;
    private CheckOutRequest request;
    private String employeeId = "EMP-001";

    @BeforeEach
    void setUp() {
        request = new CheckOutRequest();
        request.setEmployeeId(employeeId);
        request.setCheckOutTime(LocalDateTime.now());

        context = new CheckOutContext(request, "tenant-001");
    }

    @Nested
    @DisplayName("驗證成功")
    class SuccessTests {
        @Test
        @DisplayName("今日有上班打卡但未下班，應驗證通過")
        void shouldPassValidation() throws Exception {
            // Given
            AttendanceRecord record = new AttendanceRecord(
                    new RecordId("rec-001"), employeeId, LocalDate.now());
            // Make sure it looks like checked in (checkInTime set via CheckIn)
            // But we can just set it via constructor if we had one, or rely on internal
            // state if accessible.
            // Actually AttendanceRecord constructor doesn't set checkInTime, checkIn()
            // method does.
            // But for this test, as long as object exists and getCheckOutTime() is null, it
            // should pass.

            when(attendanceRecordRepository.findByEmployeeIdAndDate(
                    eq(employeeId), any(LocalDate.class)))
                    .thenReturn(List.of(record));

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getRecord());
            assertEquals(record, context.getRecord());
        }
    }

    @Nested
    @DisplayName("驗證失敗")
    class FailureTests {
        @Test
        @DisplayName("今日無任何打卡記錄應拋出例外 (尚未上班)")
        void shouldThrowExceptionWhenNoRecord() {
            // Given
            when(attendanceRecordRepository.findByEmployeeIdAndDate(
                    eq(employeeId), any(LocalDate.class)))
                    .thenReturn(List.of());

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("今日尚未上班打卡"));
        }

        @Test
        @DisplayName("今日已完成下班打卡應拋出例外")
        void shouldThrowExceptionWhenAlreadyCheckedOut() throws Exception {
            // Given
            AttendanceRecord record = new AttendanceRecord(
                    new RecordId("rec-001"), employeeId, LocalDate.now());
            // Simulate checked out
            // We need to call checkOut() or mock behavior to ensure getCheckOutTime() is
            // not null.
            // Since we use real object, let's call checkOut.
            // But checkOut needs a Shift.

            // Allow checkOut to set time
            record.checkIn(LocalDateTime.now().minusHours(9),
                    new com.company.hrms.attendance.domain.model.aggregate.Shift(
                            new com.company.hrms.attendance.domain.model.valueobject.ShiftId("S1"),
                            "Regular",
                            com.company.hrms.attendance.domain.model.valueobject.ShiftType.REGULAR,
                            java.time.LocalTime.of(9, 0), java.time.LocalTime.of(18, 0)));

            record.checkOut(LocalDateTime.now(),
                    new com.company.hrms.attendance.domain.model.aggregate.Shift(
                            new com.company.hrms.attendance.domain.model.valueobject.ShiftId("S1"),
                            "Regular",
                            com.company.hrms.attendance.domain.model.valueobject.ShiftType.REGULAR,
                            java.time.LocalTime.of(9, 0), java.time.LocalTime.of(18, 0)));

            when(attendanceRecordRepository.findByEmployeeIdAndDate(
                    eq(employeeId), any(LocalDate.class)))
                    .thenReturn(List.of(record));

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("已完成下班打卡"));
        }
    }

    @Test
    @DisplayName("getName 應返回 '驗證下班打卡'")
    void shouldReturnCorrectName() {
        assertEquals("驗證下班打卡", task.getName());
    }

    @Test
    @DisplayName("shouldExecute 在有 Request 時應返回 true")
    void shouldExecuteWhenRequestExists() {
        assertTrue(task.shouldExecute(context));
    }
}
