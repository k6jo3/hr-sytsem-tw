package com.company.hrms.attendance.application.service.checkout.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("SaveCheckOutRecordTask 測試")
class SaveCheckOutRecordTaskTest {

    @Mock
    private IAttendanceRecordRepository attendanceRecordRepository;

    @InjectMocks
    private SaveCheckOutRecordTask task;

    private CheckOutContext context;

    @BeforeEach
    void setUp() {
        CheckOutRequest request = new CheckOutRequest();
        context = new CheckOutContext(request, "tenant-001");

        AttendanceRecord record = new AttendanceRecord(
                new RecordId("rec-001"), "EMP-001", LocalDate.now());
        context.setRecord(record);
    }

    @Test
    @DisplayName("應成功儲存下班打卡記錄")
    void shouldSaveRecordSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        verify(attendanceRecordRepository).save(any(AttendanceRecord.class));
    }

    @Test
    @DisplayName("getName 應返回 '儲存下班打卡記錄'")
    void shouldReturnCorrectName() {
        assertEquals("儲存下班打卡記錄", task.getName());
    }

    @Test
    @DisplayName("shouldExecute 在有 Record 時應返回 true")
    void shouldExecuteWhenRecordExists() {
        assertTrue(task.shouldExecute(context));
    }
}
