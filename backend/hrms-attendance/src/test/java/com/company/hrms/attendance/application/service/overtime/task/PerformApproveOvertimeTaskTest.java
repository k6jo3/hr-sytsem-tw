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

import com.company.hrms.attendance.api.request.overtime.ApproveOvertimeRequest;
import com.company.hrms.attendance.application.service.overtime.context.ApproveOvertimeContext;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeType;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerformApproveOvertimeTask 測試")
class PerformApproveOvertimeTaskTest {

    @InjectMocks
    private PerformApproveOvertimeTask task;

    private ApproveOvertimeContext context;
    private OvertimeApplication application;

    @BeforeEach
    void setUp() {
        ApproveOvertimeRequest request = new ApproveOvertimeRequest();
        request.setComment("核准");
        context = new ApproveOvertimeContext(request, "tenant-001");

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
        @DisplayName("應成功執行核准")
        void shouldApproveSuccessfully() throws Exception {
            // When
            task.execute(context);

            // Then
            assertEquals(ApplicationStatus.APPROVED, application.getStatus());
        }
    }

    @Nested
    @DisplayName("失敗場景")
    class FailureTests {

        @Test
        @DisplayName("已核准的申請不可再次核准")
        void shouldFailWhenAlreadyApproved() {
            // Given
            application.approve();

            // When & Then
            assertThrows(IllegalStateException.class, () -> task.execute(context));
        }

        @Test
        @DisplayName("已駁回的申請不可核准")
        void shouldFailWhenAlreadyRejected() {
            // Given
            application.reject("不需要加班");

            // When & Then
            assertThrows(IllegalStateException.class, () -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("方法測試")
    class MethodTests {

        @Test
        @DisplayName("getName 應返回 '執行加班核准'")
        void shouldReturnCorrectName() {
            assertEquals("執行加班核准", task.getName());
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
