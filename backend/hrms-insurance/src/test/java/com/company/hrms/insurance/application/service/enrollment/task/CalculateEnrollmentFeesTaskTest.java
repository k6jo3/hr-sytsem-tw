package com.company.hrms.insurance.application.service.enrollment.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.service.InsuranceFeeCalculationService;

/**
 * CalculateEnrollmentFeesTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CalculateEnrollmentFeesTask 測試")
class CalculateEnrollmentFeesTaskTest {

    @Mock
    private InsuranceFeeCalculationService feeCalculationService;

    @InjectMocks
    private CalculateEnrollmentFeesTask task;

    private EnrollmentContext context;
    private InsuranceLevel testLevel;
    private InsuranceFees testFees;

    private static final BigDecimal MONTHLY_SALARY = new BigDecimal("48200");
    private static final String ENROLL_DATE = "2025-01-01";

    @BeforeEach
    void setUp() {
        EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
                .employeeId("EMP001")
                .insuranceUnitId("unit-001")
                .monthlySalary(MONTHLY_SALARY)
                .enrollDate(ENROLL_DATE)
                .selfContributionRate(new BigDecimal("0.06"))
                .build();

        context = new EnrollmentContext(request, "default");

        // 設置級距
        testLevel = new InsuranceLevel(
                LevelId.generate(),
                InsuranceType.LABOR,
                15,
                MONTHLY_SALARY,
                LocalDate.parse(ENROLL_DATE));
        context.setLaborLevel(testLevel);

        // 準備預期的保費結果
        testFees = new InsuranceFees(
                new BigDecimal("1109"),    // laborEmployeeFee
                new BigDecimal("3881"),    // laborEmployerFee
                new BigDecimal("748"),     // healthEmployeeFee
                new BigDecimal("1496"),    // healthEmployerFee
                new BigDecimal("2892"),    // pensionEmployerFee
                new BigDecimal("2892")     // pensionSelfContribution
        );
    }

    @Nested
    @DisplayName("execute 方法測試")
    class ExecuteTests {

        @Test
        @DisplayName("成功計算保費時，應設置到 Context")
        void execute_Success_ShouldSetFeesToContext() throws Exception {
            // Given
            when(feeCalculationService.calculate(any(), any())).thenReturn(testFees);

            // When
            task.execute(context);

            // Then
            assertNotNull(context.getFees());
            assertEquals(testFees, context.getFees());
            verify(feeCalculationService).calculate(eq(testLevel), any());
        }

        @Test
        @DisplayName("無自提比例時，也應正確計算保費")
        void execute_WithoutSelfContribution_ShouldCalculateFees() throws Exception {
            // Given
            EnrollEmployeeRequest requestWithoutSelf = EnrollEmployeeRequest.builder()
                    .employeeId("EMP001")
                    .insuranceUnitId("unit-001")
                    .monthlySalary(MONTHLY_SALARY)
                    .enrollDate(ENROLL_DATE)
                    .build();
            EnrollmentContext ctxWithoutSelf = new EnrollmentContext(requestWithoutSelf, "default");
            ctxWithoutSelf.setLaborLevel(testLevel);

            InsuranceFees feesWithoutSelf = new InsuranceFees(
                    new BigDecimal("1109"),    // laborEmployeeFee
                    new BigDecimal("3881"),    // laborEmployerFee
                    new BigDecimal("748"),     // healthEmployeeFee
                    new BigDecimal("1496"),    // healthEmployerFee
                    new BigDecimal("2892"),    // pensionEmployerFee
                    BigDecimal.ZERO            // pensionSelfContribution
            );

            when(feeCalculationService.calculate(any(), isNull())).thenReturn(feesWithoutSelf);

            // When
            task.execute(ctxWithoutSelf);

            // Then
            assertNotNull(ctxWithoutSelf.getFees());
            verify(feeCalculationService).calculate(eq(testLevel), isNull());
        }

        @Test
        @DisplayName("計算服務拋出異常時，應傳遞異常")
        void execute_ServiceThrows_ShouldPropagateException() {
            // Given
            when(feeCalculationService.calculate(any(), any()))
                    .thenThrow(new RuntimeException("計算失敗"));

            // When & Then
            assertThrows(RuntimeException.class, () -> task.execute(context));
        }
    }

    @Nested
    @DisplayName("shouldExecute 方法測試")
    class ShouldExecuteTests {

        @Test
        @DisplayName("有勞保級距時，應返回 true")
        void shouldExecute_WithLaborLevel_ShouldReturnTrue() {
            assertTrue(task.shouldExecute(context));
        }

        @Test
        @DisplayName("無勞保級距時，應返回 false")
        void shouldExecute_WithoutLaborLevel_ShouldReturnFalse() {
            context.setLaborLevel(null);
            assertFalse(task.shouldExecute(context));
        }
    }

    @Test
    @DisplayName("getName 應返回 '計算保費'")
    void getName_ShouldReturnCorrectName() {
        assertEquals("計算保費", task.getName());
    }
}
