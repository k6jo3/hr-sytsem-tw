package com.company.hrms.attendance.application.service.checkin.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.attendance.CheckInRequest;
import com.company.hrms.attendance.application.service.checkin.context.AttendanceContext;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;

/**
 * ValidateCheckInTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ValidateCheckInTask 測試")
class ValidateCheckInTaskTest {

    @Mock
    private IAttendanceRecordRepository attendanceRecordRepository;

    @InjectMocks
    private ValidateCheckInTask task;

    private AttendanceContext context;
    private CheckInRequest request;

    @BeforeEach
    void setUp() {
        request = new CheckInRequest();
        request.setEmployeeId("EMP-001");
        request.setCheckInTime(LocalDateTime.now());

        context = new AttendanceContext(request, "tenant-001");
    }

    @Nested
    @DisplayName("驗證成功")
    class SuccessTests {

        @Test
        @DisplayName("今日無打卡記錄應驗證通過")
        void shouldPassWhenNoRecordToday() throws Exception {
            // Given
            when(attendanceRecordRepository.findByEmployeeIdAndDate(
                    eq("EMP-001"), any(LocalDate.class)))
                    .thenReturn(Collections.emptyList());

            // When & Then
            assertDoesNotThrow(() -> task.execute(context));
            verify(attendanceRecordRepository).findByEmployeeIdAndDate(eq("EMP-001"), any(LocalDate.class));
        }
    }

    @Nested
    @DisplayName("驗證失敗")
    class FailureTests {

        @Test
        @DisplayName("今日已有打卡記錄應拋出例外")
        void shouldThrowExceptionWhenRecordExists() {
            // Given - create record with checkInTime set
            AttendanceRecord existingRecord = new AttendanceRecord(
                    new RecordId("rec-001"), "EMP-001", LocalDate.now());
            // Need to set checkInTime - use a Shift to call checkIn
            existingRecord.checkIn(
                    java.time.LocalDateTime.now(),
                    new Shift(
                            new ShiftId("s1"),
                            "ORG001",
                            "S001",
                            "Standard",
                            ShiftType.REGULAR,
                            java.time.LocalTime.of(9, 0),
                            java.time.LocalTime.of(18, 0)));
            when(attendanceRecordRepository.findByEmployeeIdAndDate(
                    eq("EMP-001"), any(LocalDate.class)))
                    .thenReturn(List.of(existingRecord));

            // When & Then
            com.company.hrms.common.exception.ResourceAlreadyExistsException exception = assertThrows(
                    com.company.hrms.common.exception.ResourceAlreadyExistsException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("已完成上班打卡"));
        }

        @Test
        @DisplayName("員工 ID 為空應拋出 ValidationException")
        void shouldThrowExceptionWhenEmployeeIdIsEmpty() {
            // Given
            request.setEmployeeId("");

            // When & Then
            com.company.hrms.common.exception.ValidationException exception = assertThrows(
                    com.company.hrms.common.exception.ValidationException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("員工 ID 為必填"));
        }
    }

    @Test
    @DisplayName("shouldExecute 在有 CheckInRequest 時應返回 true")
    void shouldExecuteWhenCheckInRequestExists() {
        assertTrue(task.shouldExecute(context));
    }

    @Test
    @DisplayName("shouldExecute 在無 CheckInRequest 時應返回 false")
    void shouldNotExecuteWhenCheckInRequestNotExists() {
        AttendanceContext emptyContext = new AttendanceContext(null, "tenant-001");
        assertFalse(task.shouldExecute(emptyContext));
    }

    @Test
    @DisplayName("getName 應返回 '驗證上班打卡'")
    void shouldReturnCorrectName() {
        assertEquals("驗證打卡", task.getName());
    }
}
