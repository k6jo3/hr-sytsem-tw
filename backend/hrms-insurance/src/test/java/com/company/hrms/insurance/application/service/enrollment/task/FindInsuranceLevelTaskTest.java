package com.company.hrms.insurance.application.service.enrollment.task;

import static org.junit.jupiter.api.Assertions.*;
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

import com.company.hrms.insurance.api.request.EnrollEmployeeRequest;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.service.InsuranceLevelMatchingService;

/**
 * FindInsuranceLevelTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FindInsuranceLevelTask 測試")
class FindInsuranceLevelTaskTest {

    @Mock
    private InsuranceLevelMatchingService levelMatchingService;

    @InjectMocks
    private FindInsuranceLevelTask task;

    private EnrollmentContext context;
    private static final BigDecimal MONTHLY_SALARY = new BigDecimal("48200");
    private static final LocalDate ENROLL_DATE = LocalDate.of(2025, 1, 1);

    @BeforeEach
    void setUp() {
        EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
                .employeeId("EMP001")
                .insuranceUnitId("unit-001")
                .monthlySalary(MONTHLY_SALARY)
                .enrollDate("2025-01-01")
                .build();
        context = new EnrollmentContext(request, "default");
    }

    @Nested
    @DisplayName("execute 方法測試")
    class ExecuteTests {

        @Test
        @DisplayName("成功查詢級距時，應設置勞保/健保/勞退級距")
        void execute_Success_ShouldSetAllLevels() throws Exception {
            // Given
            InsuranceLevel laborLevel = createLevel(InsuranceType.LABOR, 15);
            InsuranceLevel healthLevel = createLevel(InsuranceType.HEALTH, 15);
            InsuranceLevel pensionLevel = createLevel(InsuranceType.PENSION, 15);

            when(levelMatchingService.findAppropriateLevel(MONTHLY_SALARY, InsuranceType.LABOR, ENROLL_DATE))
                    .thenReturn(Optional.of(laborLevel));
            when(levelMatchingService.findAppropriateLevel(MONTHLY_SALARY, InsuranceType.HEALTH, ENROLL_DATE))
                    .thenReturn(Optional.of(healthLevel));
            when(levelMatchingService.findAppropriateLevel(MONTHLY_SALARY, InsuranceType.PENSION, ENROLL_DATE))
                    .thenReturn(Optional.of(pensionLevel));

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getLaborLevel());
            assertNotNull(context.getHealthLevel());
            assertNotNull(context.getPensionLevel());
            assertEquals(laborLevel, context.getLaborLevel());
        }

        @Test
        @DisplayName("找不到勞保級距時，應拋出 IllegalArgumentException")
        void execute_NoLaborLevel_ShouldThrowException() {
            // Given
            when(levelMatchingService.findAppropriateLevel(MONTHLY_SALARY, InsuranceType.LABOR, ENROLL_DATE))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> task.execute(context));
        }

        @Test
        @DisplayName("健保級距不存在時，應使用勞保級距")
        void execute_NoHealthLevel_ShouldUseLaborLevel() throws Exception {
            // Given
            InsuranceLevel laborLevel = createLevel(InsuranceType.LABOR, 15);

            when(levelMatchingService.findAppropriateLevel(MONTHLY_SALARY, InsuranceType.LABOR, ENROLL_DATE))
                    .thenReturn(Optional.of(laborLevel));
            when(levelMatchingService.findAppropriateLevel(MONTHLY_SALARY, InsuranceType.HEALTH, ENROLL_DATE))
                    .thenReturn(Optional.empty());
            when(levelMatchingService.findAppropriateLevel(MONTHLY_SALARY, InsuranceType.PENSION, ENROLL_DATE))
                    .thenReturn(Optional.empty());

            // When
            task.execute(context);

            // Then
            assertEquals(laborLevel, context.getHealthLevel());
            assertEquals(laborLevel, context.getPensionLevel());
        }
    }

    @Nested
    @DisplayName("shouldExecute 方法測試")
    class ShouldExecuteTests {

        @Test
        @DisplayName("有月薪時，應返回 true")
        void shouldExecute_WithSalary_ShouldReturnTrue() {
            assertTrue(task.shouldExecute(context));
        }

        @Test
        @DisplayName("無月薪時，應返回 false")
        void shouldExecute_WithoutSalary_ShouldReturnFalse() {
            EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
                    .employeeId("EMP001")
                    .insuranceUnitId("unit-001")
                    .enrollDate("2025-01-01")
                    .build();
            EnrollmentContext ctx = new EnrollmentContext(request, "default");

            assertFalse(task.shouldExecute(ctx));
        }
    }

    // ==================== Helper Methods ====================

    private InsuranceLevel createLevel(InsuranceType type, int levelNumber) {
        return new InsuranceLevel(
                LevelId.generate(),
                type,
                levelNumber,
                MONTHLY_SALARY,
                ENROLL_DATE);
    }
}
