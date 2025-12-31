package com.company.hrms.attendance.application.service.leave.task;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.leave.ApplyLeaveRequest;
import com.company.hrms.attendance.application.service.leave.context.LeaveContext;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateLeaveApplicationTask 測試")
class CreateLeaveApplicationTaskTest {

    @InjectMocks
    private CreateLeaveApplicationTask task;

    private LeaveContext context;
    private ApplyLeaveRequest request;

    @BeforeEach
    void setUp() {
        request = new ApplyLeaveRequest();
        request.setEmployeeId("EMP-001");
        request.setLeaveTypeId("annual");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now());
        request.setReason("Test Reason");
        request.setStartPeriod("FULL_DAY");
        request.setEndPeriod("FULL_DAY");

        context = new LeaveContext(request, "tenant-001");
    }

    @Test
    @DisplayName("應成功建立請假申請")
    void shouldCreateApplicationSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        assertNotNull(context.getApplication());
        assertEquals("EMP-001", context.getApplication().getEmployeeId());
        assertEquals("annual", context.getApplication().getLeaveTypeId().getValue());
        assertEquals("Test Reason", context.getApplication().getReason());
        assertEquals(LeavePeriodType.FULL_DAY, context.getApplication().getStartPeriod());
    }

    @Test
    @DisplayName("若有附件URL應一併設定")
    void shouldSetAttachmentUrlIfPresent() throws Exception {
        // Given
        request.setProofAttachmentUrl("http://example.com/proof.jpg");

        // When
        task.execute(context);

        // Then
        assertEquals("http://example.com/proof.jpg", context.getApplication().getProofAttachmentUrl());
    }

    @Test
    @DisplayName("getName 應返回 '建立請假申請'")
    void shouldReturnCorrectName() {
        assertEquals("建立請假申請", task.getName());
    }

    @Test
    @DisplayName("shouldExecute 在有 Request 時應返回 true")
    void shouldExecuteWhenRequestExists() {
        assertTrue(task.shouldExecute(context));
    }
}
