package com.company.hrms.attendance.application.service.overtime.task;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.overtime.ApplyOvertimeRequest;
import com.company.hrms.attendance.application.service.overtime.context.OvertimeContext;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOvertimeApplicationTask 測試")
class CreateOvertimeApplicationTaskTest {

    @InjectMocks
    private CreateOvertimeApplicationTask task;

    private OvertimeContext context;
    private ApplyOvertimeRequest request;

    @BeforeEach
    void setUp() {
        request = new ApplyOvertimeRequest();
        request.setEmployeeId("EMP-001");
        request.setDate(LocalDate.now());
        request.setHours(2.5);
        request.setOvertimeType("WORKDAY");
        request.setReason("Urgent Work");

        context = new OvertimeContext(request, "tenant-001");
    }

    @Test
    @DisplayName("應成功建立加班申請")
    void shouldCreateApplicationSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        assertNotNull(context.getApplication());
        assertEquals("EMP-001", context.getApplication().getEmployeeId());
        assertEquals(2.5, context.getApplication().getHours());
        assertEquals(OvertimeType.WORKDAY, context.getApplication().getOvertimeType());
        assertEquals("Urgent Work", context.getApplication().getReason());
    }

    @Test
    @DisplayName("getName 應返回 '建立加班申請'")
    void shouldReturnCorrectName() {
        assertEquals("建立加班申請", task.getName());
    }

    @Test
    @DisplayName("shouldExecute 在有 Request 時應返回 true")
    void shouldExecuteWhenRequestExists() {
        assertTrue(task.shouldExecute(context));
    }
}
