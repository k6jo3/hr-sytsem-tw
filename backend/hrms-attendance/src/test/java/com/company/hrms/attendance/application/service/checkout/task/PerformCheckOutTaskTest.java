package com.company.hrms.attendance.application.service.checkout.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.attendance.domain.repository.IShiftRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerformCheckOutTask 測試")
class PerformCheckOutTaskTest {

    @Mock
    private IShiftRepository shiftRepository;

    @InjectMocks
    private PerformCheckOutTask task;

    private CheckOutContext context;
    private AttendanceRecord record;

    @BeforeEach
    void setUp() {
        CheckOutRequest request = new CheckOutRequest();
        request.setEmployeeId("EMP-001");
        request.setCheckOutTime(LocalDateTime.now());

        context = new CheckOutContext(request, "tenant-001");

        // Prepare record (Already checked in)
        record = new AttendanceRecord(
                new RecordId("rec-001"), "EMP-001", LocalDate.now());

        // Mock check-in (needed for working hours calculation)
        Shift shift = new Shift(new ShiftId("S1"), "ORG001", "S1", "Regular", ShiftType.REGULAR, LocalTime.of(9, 0),
                LocalTime.of(18, 0));
        record.checkIn(LocalDateTime.now().withHour(9).withMinute(0), shift);

        context.setRecord(record);
    }

    @Nested
    @DisplayName("執行成功")
    class SuccessTests {
        @Test
        @DisplayName("應成功執行下班打卡並計算工時")
        void shouldPerformCheckOutSuccessfully() throws Exception {
            // Given
            Shift shift = new Shift(
                    new ShiftId("shift-001"),
                    "ORG001",
                    "S001",
                    "常規班",
                    ShiftType.REGULAR,
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0));
            when(shiftRepository.findAll()).thenReturn(List.of(shift));

            // Set check-out time to 18:00 (9 hours after 9:00 check-in)
            context.getCheckOutRequest().setCheckOutTime(LocalDateTime.now().withHour(18).withMinute(0));

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getShift());
            assertNotNull(record.getCheckOutTime());
            assertEquals(9.0, context.getWorkingHours(), 0.1);
        }
    }

    @Nested
    @DisplayName("執行失敗")
    class FailureTests {
        @Test
        @DisplayName("找不到班別設定應拋出例外")
        void shouldThrowExceptionWhenNoShiftFound() {
            // Given
            when(shiftRepository.findAll()).thenReturn(List.of());

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("找不到班別設定"));
        }
    }
}
