package com.company.hrms.attendance.application.service.checkin.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.attendance.CheckInRequest;
import com.company.hrms.attendance.application.service.checkin.context.AttendanceContext;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;

/**
 * SaveRecordTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SaveRecordTask 測試")
class SaveRecordTaskTest {

    @Mock
    private IAttendanceRecordRepository attendanceRecordRepository;

    @InjectMocks
    private SaveRecordTask task;

    private AttendanceContext context;

    @BeforeEach
    void setUp() {
        CheckInRequest request = new CheckInRequest();
        request.setEmployeeId("EMP-001");
        request.setCheckInTime(LocalDateTime.now());

        context = new AttendanceContext(request, "tenant-001");

        // Create a record to save
        AttendanceRecord record = new AttendanceRecord(
                new RecordId("rec-001"), "EMP-001", LocalDate.now());
        context.setRecord(record);
    }

    @Test
    @DisplayName("應成功儲存打卡記錄")
    void shouldSaveRecordSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        verify(attendanceRecordRepository).save(any(AttendanceRecord.class));
    }

    @Test
    @DisplayName("shouldExecute 在有 Record 時應返回 true")
    void shouldExecuteWhenRecordExists() {
        assertTrue(task.shouldExecute(context));
    }

    @Test
    @DisplayName("shouldExecute 在無 Record 時應返回 false")
    void shouldNotExecuteWhenRecordNotExists() {
        AttendanceContext emptyContext = new AttendanceContext(null, "tenant-001");
        assertFalse(task.shouldExecute(emptyContext));
    }

    @Test
    @DisplayName("getName 應返回 '儲存打卡記錄'")
    void shouldReturnCorrectName() {
        assertEquals("儲存打卡記錄", task.getName());
    }
}
