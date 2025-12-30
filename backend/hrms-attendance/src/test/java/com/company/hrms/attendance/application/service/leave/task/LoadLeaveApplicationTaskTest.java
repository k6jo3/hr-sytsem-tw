package com.company.hrms.attendance.application.service.leave.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.leave.ApproveLeaveRequest;
import com.company.hrms.attendance.application.service.leave.context.ApproveLeaveContext;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoadLeaveApplicationTask 測試")
class LoadLeaveApplicationTaskTest {

    @Mock
    private ILeaveApplicationRepository leaveApplicationRepository;

    @InjectMocks
    private LoadLeaveApplicationTask task;

    private ApproveLeaveContext context;
    private ApproveLeaveRequest request;

    @BeforeEach
    void setUp() {
        request = new ApproveLeaveRequest();
        request.setApplicationId("app-001");

        context = new ApproveLeaveContext(request, "tenant-001");
    }

    @Nested
    @DisplayName("載入成功")
    class SuccessTests {
        @Test
        @DisplayName("應成功載入請假申請")
        void shouldLoadApplicationSuccessfully() throws Exception {
            // Given
            LeaveApplication app = new LeaveApplication(
                    new ApplicationId("app-001"),
                    "EMP-001",
                    new LeaveTypeId("annual"),
                    LocalDate.now(),
                    LocalDate.now(),
                    LeavePeriodType.FULL_DAY,
                    LeavePeriodType.FULL_DAY,
                    "Reason");

            when(leaveApplicationRepository.findById(any(ApplicationId.class)))
                    .thenReturn(Optional.of(app));

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getApplication());
            assertEquals("app-001", context.getApplication().getId().getValue());
        }
    }

    @Nested
    @DisplayName("載入失敗")
    class FailureTests {
        @Test
        @DisplayName("找不到申請應拋出例外")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            when(leaveApplicationRepository.findById(any(ApplicationId.class)))
                    .thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> task.execute(context));
            assertTrue(exception.getMessage().contains("找不到請假申請"));
        }
    }
}
