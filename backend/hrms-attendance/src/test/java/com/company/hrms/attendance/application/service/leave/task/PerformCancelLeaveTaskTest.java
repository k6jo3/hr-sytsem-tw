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

import com.company.hrms.attendance.application.service.leave.context.CancelLeaveContext;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.LeavePeriodType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerformCancelLeaveTask 測試")
class PerformCancelLeaveTaskTest {

    @InjectMocks
    private PerformCancelLeaveTask task;

    private CancelLeaveContext context;
    private LeaveApplication application;

    @BeforeEach
    void setUp() {
        context = new CancelLeaveContext("tenant-001");

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
        @DisplayName("應成功執行取消")
        void shouldCancelSuccessfully() throws Exception {
            // When
            task.execute(context);

            // Then
            assertEquals(ApplicationStatus.CANCELLED, application.getStatus());
        }
    }

    @Nested
    @DisplayName("失敗場景")
    class FailureTests {

        @Test
        @DisplayName("已核准的申請不可取消")
        void shouldFailWhenAlreadyApproved() {
            // Given
            application.approve();

            // When & Then
            assertThrows(IllegalStateException.class, () -> task.execute(context));
        }

        @Test
        @DisplayName("已駁回的申請不可取消")
        void shouldFailWhenAlreadyRejected() {
            // Given
            application.reject("駁回原因");

            // When & Then
            assertThrows(IllegalStateException.class, () -> task.execute(context));
        }

        @Test
        @DisplayName("已取消的申請不可再次取消")
        void shouldFailWhenAlreadyCancelled() {
            // Given
            application.cancel();

            // When & Then
            assertThrows(IllegalStateException.class, () -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("方法測試")
    class MethodTests {

        @Test
        @DisplayName("getName 應返回 '執行請假取消'")
        void shouldReturnCorrectName() {
            assertEquals("執行請假取消", task.getName());
        }

        @Test
        @DisplayName("shouldExecute 在有 Application 時應返回 true")
        void shouldExecuteWhenApplicationExists() {
            assertTrue(task.shouldExecute(context));
        }

        @Test
        @DisplayName("shouldExecute 在無 Application 時應返回 false")
        void shouldNotExecuteWhenApplicationIsNull() {
            context.setApplication(null);
            assertFalse(task.shouldExecute(context));
        }
    }
}
