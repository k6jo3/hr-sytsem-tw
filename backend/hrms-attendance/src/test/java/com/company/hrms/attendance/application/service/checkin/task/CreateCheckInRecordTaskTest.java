package com.company.hrms.attendance.application.service.checkin.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.model.valueobject.ShiftType;
import com.company.hrms.attendance.domain.repository.IShiftRepository;

/**
 * CreateCheckInRecordTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCheckInRecordTask 測試")
class CreateCheckInRecordTaskTest {

    @Mock
    private IShiftRepository shiftRepository;

    @InjectMocks
    private CreateCheckInRecordTask task;

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
    @DisplayName("建立成功")
    class SuccessTests {

        @Test
        @DisplayName("應成功建立打卡記錄")
        void shouldCreateRecordSuccessfully() throws Exception {
            // Given
            Shift shift = new Shift(
                    new ShiftId("shift-001"),
                    "ORG001",
                    "常規班",
                    ShiftType.REGULAR,
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0));
            when(shiftRepository.findAll()).thenReturn(java.util.List.of(shift));

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getRecord());
            assertEquals("EMP-001", context.getRecord().getEmployeeId());
            assertEquals(LocalDate.now(), context.getRecord().getDate());
            assertNotNull(context.getShift());
        }
    }

    @Nested
    @DisplayName("建立失敗")
    class FailureTests {

        @Test
        @DisplayName("無班別設定應拋出例外")
        void shouldThrowExceptionWhenNoShift() {
            // Given
            when(shiftRepository.findAll()).thenReturn(java.util.Collections.emptyList());

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("班別設定"));
        }
    }

    @Test
    @DisplayName("getName 應返回 '建立打卡記錄'")
    void shouldReturnCorrectName() {
        assertEquals("建立打卡記錄", task.getName());
    }
}
