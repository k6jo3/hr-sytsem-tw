package com.company.hrms.attendance.application.service.overtime.task;

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

import com.company.hrms.attendance.api.request.overtime.ApplyOvertimeRequest;
import com.company.hrms.attendance.application.service.overtime.context.OvertimeContext;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaveOvertimeApplicationTask 測試")
class SaveOvertimeApplicationTaskTest {

    @Mock
    private IOvertimeApplicationRepository overtimeApplicationRepository;

    @InjectMocks
    private SaveOvertimeApplicationTask task;

    private OvertimeContext context;

    @BeforeEach
    void setUp() {
        ApplyOvertimeRequest request = new ApplyOvertimeRequest();
        context = new OvertimeContext(request, "tenant-001");

        OvertimeApplication app = new OvertimeApplication(
                new OvertimeId("app-001"),
                "EMP-001",
                LocalDate.now(),
                2.0,
                OvertimeType.WORKDAY,
                "Reason");
        context.setApplication(app);
    }

    @Test
    @DisplayName("應成功儲存加班申請")
    void shouldSaveApplicationSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        verify(overtimeApplicationRepository).save(any(OvertimeApplication.class));
    }

    @Test
    @DisplayName("getName 應返回 '儲存加班申請'")
    void shouldReturnCorrectName() {
        assertEquals("儲存加班申請", task.getName());
    }

    @Test
    @DisplayName("shouldExecute 在有 Application 時應返回 true")
    void shouldExecuteWhenApplicationExists() {
        assertTrue(task.shouldExecute(context));
    }
}
