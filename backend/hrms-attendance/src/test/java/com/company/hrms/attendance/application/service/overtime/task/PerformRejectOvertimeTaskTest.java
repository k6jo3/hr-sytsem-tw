package com.company.hrms.attendance.application.service.overtime.task;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.overtime.RejectOvertimeRequest;
import com.company.hrms.attendance.application.service.overtime.context.RejectOvertimeContext;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerformRejectOvertimeTask 測試")
class PerformRejectOvertimeTaskTest {

    @InjectMocks
    private PerformRejectOvertimeTask task;

    private RejectOvertimeContext context;
    private OvertimeApplication application;

    @BeforeEach
    void setUp() {
        RejectOvertimeRequest request = new RejectOvertimeRequest();
        request.setReason("加班時數超過上限");
        context = new RejectOvertimeContext(request, "tenant-001");

        application = new OvertimeApplication(
                new OvertimeId("ot-001"),
                "EMP-001",
                LocalDate.now(),
                2.0,
                OvertimeType.WORKDAY,
                "專案趕工");
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
            assertEquals("加班時數超過上限", application.getRejectionReason());
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
        @DisplayName("getName 應返回 '執行加班駁回'")
        void shouldReturnCorrectName() {
            assertEquals("執行加班駁回", task.getName());
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
            RejectOvertimeContext emptyContext = new RejectOvertimeContext(null, "tenant-001");
            emptyContext.setApplication(application);
            assertFalse(task.shouldExecute(emptyContext));
        }
    }
}
