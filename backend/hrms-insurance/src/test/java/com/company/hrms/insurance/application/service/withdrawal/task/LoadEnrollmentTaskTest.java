package com.company.hrms.insurance.application.service.withdrawal.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
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

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.insurance.api.request.WithdrawEnrollmentRequest;
import com.company.hrms.insurance.application.service.withdrawal.context.WithdrawalContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

/**
 * LoadEnrollmentTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoadEnrollmentTask 測試")
class LoadEnrollmentTaskTest {

    @Mock
    private IInsuranceEnrollmentRepository enrollmentRepository;

    @InjectMocks
    private LoadEnrollmentTask task;

    private WithdrawalContext context;
    private static final String ENROLLMENT_ID = "enrollment-001";

    @BeforeEach
    void setUp() {
        WithdrawEnrollmentRequest request = WithdrawEnrollmentRequest.builder()
                .withdrawDate("2025-12-31")
                .reason("離職")
                .build();
        context = new WithdrawalContext(ENROLLMENT_ID, request, "default");
    }

    @Nested
    @DisplayName("execute 方法測試")
    class ExecuteTests {

        @Test
        @DisplayName("成功載入加保記錄時，應設置到 Context")
        void execute_Success_ShouldSetEnrollmentToContext() throws Exception {
            // Given
            InsuranceEnrollment enrollment = createActiveEnrollment();
            when(enrollmentRepository.findById(any(EnrollmentId.class)))
                    .thenReturn(Optional.of(enrollment));

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getEnrollment());
            assertEquals(enrollment, context.getEnrollment());
            verify(enrollmentRepository).findById(any(EnrollmentId.class));
        }

        @Test
        @DisplayName("加保記錄不存在時，應拋出 EntityNotFoundException")
        void execute_EnrollmentNotFound_ShouldThrowException() {
            // Given
            when(enrollmentRepository.findById(any(EnrollmentId.class)))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThrows(EntityNotFoundException.class, () -> task.execute(context));
        }
    }

    // ==================== Helper Methods ====================

    private InsuranceEnrollment createActiveEnrollment() {
        InsuranceLevel level = new InsuranceLevel(
                LevelId.generate(),
                InsuranceType.LABOR,
                15,
                new BigDecimal("48200"),
                LocalDate.of(2025, 1, 1));

        return InsuranceEnrollment.enroll(
                "EMP001",
                new UnitId("unit-001"),
                InsuranceType.LABOR,
                level,
                LocalDate.of(2025, 1, 1));
    }
}
