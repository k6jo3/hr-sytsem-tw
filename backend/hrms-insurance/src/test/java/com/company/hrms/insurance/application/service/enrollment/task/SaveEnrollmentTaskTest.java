package com.company.hrms.insurance.application.service.enrollment.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.insurance.api.request.EnrollEmployeeRequest;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

/**
 * SaveEnrollmentTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SaveEnrollmentTask 測試")
class SaveEnrollmentTaskTest {

    @Mock
    private IInsuranceEnrollmentRepository enrollmentRepository;

    @InjectMocks
    private SaveEnrollmentTask task;

    private EnrollmentContext context;

    private static final String EMPLOYEE_ID = "EMP001";
    private static final UnitId UNIT_ID = new UnitId("unit-001");
    private static final BigDecimal MONTHLY_SALARY = new BigDecimal("48200");
    private static final String ENROLL_DATE = "2025-01-01";

    @BeforeEach
    void setUp() {
        EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
                .employeeId(EMPLOYEE_ID)
                .insuranceUnitId(UNIT_ID.getValue())
                .monthlySalary(MONTHLY_SALARY)
                .enrollDate(ENROLL_DATE)
                .build();

        context = new EnrollmentContext(request, "default");
    }

    @Nested
    @DisplayName("execute 方法測試")
    class ExecuteTests {

        @Test
        @DisplayName("成功儲存所有加保記錄")
        void execute_Success_ShouldSaveAllEnrollments() throws Exception {
            // Given
            InsuranceEnrollment laborEnrollment = createEnrollment(InsuranceType.LABOR);
            InsuranceEnrollment healthEnrollment = createEnrollment(InsuranceType.HEALTH);
            InsuranceEnrollment pensionEnrollment = createEnrollment(InsuranceType.PENSION);

            context.addEnrollment(laborEnrollment);
            context.addEnrollment(healthEnrollment);
            context.addEnrollment(pensionEnrollment);

            when(enrollmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            // When
            task.execute(context);

            // Then
            verify(enrollmentRepository, times(3)).save(any());
            verify(enrollmentRepository).save(laborEnrollment);
            verify(enrollmentRepository).save(healthEnrollment);
            verify(enrollmentRepository).save(pensionEnrollment);
        }

        @Test
        @DisplayName("單筆記錄也能正確儲存")
        void execute_SingleEnrollment_ShouldSave() throws Exception {
            // Given
            InsuranceEnrollment enrollment = createEnrollment(InsuranceType.LABOR);
            context.addEnrollment(enrollment);

            when(enrollmentRepository.save(any())).thenReturn(enrollment);

            // When
            task.execute(context);

            // Then
            verify(enrollmentRepository, times(1)).save(enrollment);
        }

        @Test
        @DisplayName("Repository 拋出異常時，應傳遞異常")
        void execute_RepositoryThrows_ShouldPropagateException() {
            // Given
            InsuranceEnrollment enrollment = createEnrollment(InsuranceType.LABOR);
            context.addEnrollment(enrollment);

            when(enrollmentRepository.save(any()))
                    .thenThrow(new RuntimeException("儲存失敗"));

            // When & Then
            assertThrows(RuntimeException.class, () -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("shouldExecute 方法測試")
    class ShouldExecuteTests {

        @Test
        @DisplayName("有加保記錄時，應返回 true")
        void shouldExecute_WithEnrollments_ShouldReturnTrue() {
            context.addEnrollment(createEnrollment(InsuranceType.LABOR));
            assertTrue(task.shouldExecute(context));
        }

        @Test
        @DisplayName("無加保記錄時，應返回 false")
        void shouldExecute_WithoutEnrollments_ShouldReturnFalse() {
            assertFalse(task.shouldExecute(context));
        }

        @Test
        @DisplayName("空的加保記錄列表時，應返回 false")
        void shouldExecute_EmptyEnrollments_ShouldReturnFalse() {
            // enrollments 預設為空 ArrayList
            assertFalse(task.shouldExecute(context));
        }
    }

    @Test
    @DisplayName("getName 應返回 '儲存加保記錄'")
    void getName_ShouldReturnCorrectName() {
        assertEquals("儲存加保記錄", task.getName());
    }

    // ==================== Helper Methods ====================

    private InsuranceEnrollment createEnrollment(InsuranceType type) {
        InsuranceLevel level = new InsuranceLevel(
                LevelId.generate(),
                type,
                15,
                MONTHLY_SALARY,
                LocalDate.parse(ENROLL_DATE));

        return InsuranceEnrollment.enroll(
                EMPLOYEE_ID,
                UNIT_ID,
                type,
                level,
                LocalDate.parse(ENROLL_DATE));
    }
}
