package com.company.hrms.attendance.application.service.leave.task;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.leave.RejectLeaveRequest;
import com.company.hrms.attendance.application.service.leave.context.RejectLeaveContext;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerformRejectLeaveTask 測試")
class PerformRejectLeaveTaskTest {

    @InjectMocks
    private PerformRejectLeaveTask task;

    private RejectLeaveContext context;
    private LeaveApplication application;

    @BeforeEach
    void setUp() {
        RejectLeaveRequest request = new RejectLeaveRequest();
        request.setReason("假期餘額不足");
        context = new RejectLeaveContext(request, "tenant-001");

        application = new LeaveApplication(
                new ApplicationId("app-001"),
                "EMP-001",
                new LeaveTypeId("annual"),
                LocalDate.now(),
                LocalDate.now(),
                LeavePeriodType.FULL_DAY,
                LeavePeriodType.FULL_DAY,
                "請假原因");
        context.setApplication(application);
    }

    @Nested
    @DisplayName("成功場景")
    class SuccessTests {

        @Test
        @DisplayName("應成功執行駁回")
        void shouldRejectSuccessfully() throws Exception {
            // When
            task.execute(context);

            // Then
            assertEquals(ApplicationStatus.REJECTED, application.getStatus());
            assertEquals("假期餘額不足", application.getRejectionReason());
        }
    }

    @Nested
    @DisplayName("失敗場景")
    class FailureTests {

        @Test
        @DisplayName("已核准的申請不可駁回")
        void shouldFailWhenAlreadyApproved() {
            // Given
            application.approve();

            // When & Then
            assertThrows(IllegalStateException.class, () -> task.execute(context));
        }

        @Test
        @DisplayName("已駁回的申請不可再次駁回")
        void shouldFailWhenAlreadyRejected() {
            // Given
            application.reject("第一次駁回");

            // When & Then
            assertThrows(IllegalStateException.class, () -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("方法測試")
    class MethodTests {

        @Test
        @DisplayName("getName 應返回 '執行請假駁回'")
        void shouldReturnCorrectName() {
            assertEquals("執行請假駁回", task.getName());
        }

        @Test
        @DisplayName("shouldExecute 在有 Application 和 Request 時應返回 true")
        void shouldExecuteWhenApplicationAndRequestExist() {
            assertTrue(task.shouldExecute(context));
        }

        @Test
        @DisplayName("shouldExecute 在無 Application 時應返回 false")
        void shouldNotExecuteWhenApplicationIsNull() {
            context.setApplication(null);
            assertFalse(task.shouldExecute(context));
        }

        @Test
        @DisplayName("shouldExecute 在無 Request 時應返回 false")
        void shouldNotExecuteWhenRequestIsNull() {
            RejectLeaveContext emptyContext = new RejectLeaveContext(null, "tenant-001");
            emptyContext.setApplication(application);
            assertFalse(task.shouldExecute(emptyContext));
        }
    }
}
