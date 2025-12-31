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

import com.company.hrms.attendance.api.request.attendance.CreateCorrectionRequest;
import com.company.hrms.attendance.application.service.correction.context.CorrectionContext;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.CorrectionType;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCorrectionTask 測試")
class CreateCorrectionTaskTest {

    @InjectMocks
    private CreateCorrectionTask task;

    private CorrectionContext context;
    private CreateCorrectionRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateCorrectionRequest();
        request.setEmployeeId("EMP-001");
        request.setAttendanceRecordId("REC-001");
        request.setCorrectionDate(LocalDate.now());
        request.setCorrectionType("FORGET_CHECK_IN");
        request.setCorrectedCheckInTime(LocalTime.of(9, 0));
        request.setReason("忘記打卡");

        context = new CorrectionContext(request, "tenant-001");
    }

    @Nested
    @DisplayName("成功場景")
    class SuccessTests {

        @Test
        @DisplayName("應成功建立補卡申請")
        void shouldCreateCorrectionSuccessfully() throws Exception {
            // When
            task.execute(context);

            // Then
            assertNotNull(context.getApplication());
            assertEquals("EMP-001", context.getApplication().getEmployeeId());
            assertEquals(CorrectionType.FORGET_CHECK_IN, context.getApplication().getCorrectionType());
            assertEquals("忘記打卡", context.getApplication().getReason());
            assertEquals(ApplicationStatus.PENDING, context.getApplication().getStatus());
        }

        @Test
        @DisplayName("應正確設定補卡時間")
        void shouldSetCorrectionTimeCorrectly() throws Exception {
            // When
            task.execute(context);

            // Then
            assertEquals(LocalTime.of(9, 0), context.getApplication().getCorrectedCheckInTime());
        }
    }

    @Nested
    @DisplayName("方法測試")
    class MethodTests {

        @Test
        @DisplayName("getName 應返回 '建立補卡申請'")
        void shouldReturnCorrectName() {
            assertEquals("建立補卡申請", task.getName());
        }

        @Test
        @DisplayName("shouldExecute 在有 Request 時應返回 true")
        void shouldExecuteWhenRequestExists() {
            assertTrue(task.shouldExecute(context));
        }

        @Test
        @DisplayName("shouldExecute 在無 Request 時應返回 false")
        void shouldNotExecuteWhenRequestIsNull() {
            CorrectionContext emptyContext = new CorrectionContext(null, "tenant-001");
            assertFalse(task.shouldExecute(emptyContext));
        }
    }
}
