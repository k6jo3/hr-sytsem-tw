package com.company.hrms.attendance.application.service.leave.task;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.attendance.application.service.leave.context.ApproveLeaveContext;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerformApproveLeaveTask 測試")
class PerformApproveLeaveTaskTest {

    @InjectMocks
    private PerformApproveLeaveTask task;

    private ApproveLeaveContext context;
    private LeaveApplication app;

    @BeforeEach
    void setUp() {
        ApproveLeaveRequest request = new ApproveLeaveRequest();
        context = new ApproveLeaveContext(request, "tenant-001");

        app = new LeaveApplication(
                new ApplicationId("app-001"),
                "EMP-001",
                new LeaveTypeId("annual"),
                LocalDate.now(),
                LocalDate.now(),
                LeavePeriodType.FULL_DAY,
                LeavePeriodType.FULL_DAY,
                "Reason");
        context.setApplication(app);
    }

    @Test
    @DisplayName("應成功執行核准")
    void shouldApproveApplicationSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        assertEquals(ApplicationStatus.APPROVED, app.getStatus());
    }

    @Test
    @DisplayName("getName 應返回 '執行請假核准'")
    void shouldReturnCorrectName() {
        assertEquals("執行請假核准", task.getName());
    }

    @Test
    @DisplayName("shouldExecute 在有 Application 時應返回 true")
    void shouldExecuteWhenApplicationExists() {
        assertTrue(task.shouldExecute(context));
    }
}
