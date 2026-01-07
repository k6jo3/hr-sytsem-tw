package com.company.hrms.attendance.application.service.correction.task;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.api.request.attendance.ApproveCorrectionRequest;
import com.company.hrms.attendance.application.service.correction.context.ApproveCorrectionContext;
import com.company.hrms.attendance.domain.model.aggregate.CorrectionApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionId;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionType;

@ExtendWith(MockitoExtension.class)
@DisplayName("PerformApproveCorrectionTask 測試")
class PerformApproveCorrectionTaskTest {

    @InjectMocks
    private PerformApproveCorrectionTask task;

    private ApproveCorrectionContext context;
    private CorrectionApplication application;

    @BeforeEach
    void setUp() {
        ApproveCorrectionRequest request = new ApproveCorrectionRequest();
        context = new ApproveCorrectionContext(request, "tenant-001");

        application = new CorrectionApplication(
                new CorrectionId("corr-001"),
                "EMP-001",
                "REC-001",
                LocalDate.now(),
                CorrectionType.FORGET_CHECK_IN,
                LocalTime.of(9, 0),
                null,
                "忘記打卡");
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
        void shouldFailWhenAlreadyApproved() throws Exception {
            // Given
            application.approve();

            // When & Then
            assertThrows(IllegalStateException.class, () -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("方法測試")
    class MethodTests {

        @Test
        @DisplayName("getName 應返回 '執行補卡核准'")
        void shouldReturnCorrectName() {
            assertEquals("執行補卡核准", task.getName());
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
