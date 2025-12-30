package com.company.hrms.attendance.application.service.leave.task;

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

import com.company.hrms.attendance.api.request.leave.ApplyLeaveRequest;
import com.company.hrms.attendance.application.service.leave.context.LeaveContext;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaveLeaveApplicationTask 測試")
class SaveLeaveApplicationTaskTest {

    @Mock
    private ILeaveApplicationRepository leaveApplicationRepository;

    @InjectMocks
    private SaveLeaveApplicationTask task;

    private LeaveContext context;

    @BeforeEach
    void setUp() {
        ApplyLeaveRequest request = new ApplyLeaveRequest();
        context = new LeaveContext(request, "tenant-001");

        LeaveApplication app = new LeaveApplication(
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
    @DisplayName("應成功儲存請假申請")
    void shouldSaveApplicationSuccessfully() throws Exception {
        // When
        task.execute(context);

        // Then
        verify(leaveApplicationRepository).save(any(LeaveApplication.class));
    }

    @Test
    @DisplayName("getName 應返回 '儲存請假申請'")
    void shouldReturnCorrectName() {
        assertEquals("儲存請假申請", task.getName());
    }

    @Test
    @DisplayName("shouldExecute 在有 Application 時應返回 true")
    void shouldExecuteWhenApplicationExists() {
        assertTrue(task.shouldExecute(context));
    }
}
