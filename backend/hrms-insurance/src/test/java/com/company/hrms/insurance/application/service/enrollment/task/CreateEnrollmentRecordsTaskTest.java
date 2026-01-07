package com.company.hrms.insurance.application.service.enrollment.task;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.insurance.api.request.EnrollEmployeeRequest;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceUnit;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;

/**
 * CreateEnrollmentRecordsTask 單元測試
 */
@DisplayName("CreateEnrollmentRecordsTask 測試")
class CreateEnrollmentRecordsTaskTest {

    private CreateEnrollmentRecordsTask task;
    private EnrollmentContext context;

    private static final String EMPLOYEE_ID = "EMP001";
    private static final String UNIT_ID = "unit-001";
    private static final BigDecimal MONTHLY_SALARY = new BigDecimal("48200");
    private static final String ENROLL_DATE = "2025-01-01";

    @BeforeEach
    void setUp() {
        task = new CreateEnrollmentRecordsTask();

        // 準備請求
        EnrollEmployeeRequest request = EnrollEmployeeRequest.builder()
                .employeeId(EMPLOYEE_ID)
                .insuranceUnitId(UNIT_ID)
                .monthlySalary(MONTHLY_SALARY)
                .enrollDate(ENROLL_DATE)
                .build();

        context = new EnrollmentContext(request, "default");

        // 設置中間數據
        InsuranceUnit unit = InsuranceUnit.create("ORG001", "UNIT001", "測試投保單位");
        context.setInsuranceUnit(unit);

        InsuranceLevel laborLevel = createTestLevel(InsuranceType.LABOR);
        InsuranceLevel healthLevel = createTestLevel(InsuranceType.HEALTH);
        InsuranceLevel pensionLevel = createTestLevel(InsuranceType.PENSION);

        context.setLaborLevel(laborLevel);
        context.setHealthLevel(healthLevel);
        context.setPensionLevel(pensionLevel);
    }

    @Nested
    @DisplayName("execute 方法測試")
    class ExecuteTests {

        @Test
        @DisplayName("成功建立三筆加保記錄 (勞保、健保、勞退)")
        void execute_Success_ShouldCreateThreeEnrollments() throws Exception {
            // When
            task.execute(context);

            // Then
            assertEquals(3, context.getEnrollments().size(), "應建立三筆加保記錄");

            // 驗證勞保
            var laborEnrollment = context.getEnrollments().stream()
                    .filter(e -> e.getInsuranceType() == InsuranceType.LABOR)
                    .findFirst()
                    .orElseThrow();
            assertEquals(EMPLOYEE_ID, laborEnrollment.getEmployeeId());
            assertEquals(EnrollmentStatus.ACTIVE, laborEnrollment.getStatus());

            // 驗證健保
            var healthEnrollment = context.getEnrollments().stream()
                    .filter(e -> e.getInsuranceType() == InsuranceType.HEALTH)
                    .findFirst()
                    .orElseThrow();
            assertEquals(EMPLOYEE_ID, healthEnrollment.getEmployeeId());

            // 驗證勞退
            var pensionEnrollment = context.getEnrollments().stream()
                    .filter(e -> e.getInsuranceType() == InsuranceType.PENSION)
                    .findFirst()
                    .orElseThrow();
            assertEquals(EMPLOYEE_ID, pensionEnrollment.getEmployeeId());
        }

        @Test
        @DisplayName("加保記錄應包含正確的加保日期")
        void execute_Success_ShouldHaveCorrectEnrollDate() throws Exception {
            // When
            task.execute(context);

            // Then
            LocalDate expectedDate = LocalDate.parse(ENROLL_DATE);
            context.getEnrollments().forEach(enrollment ->
                    assertEquals(expectedDate, enrollment.getEnrollDate()));
        }

        @Test
        @DisplayName("加保記錄應包含正確的投保薪資")
        void execute_Success_ShouldHaveCorrectMonthlySalary() throws Exception {
            // When
            task.execute(context);

            // Then
            context.getEnrollments().forEach(enrollment ->
                    assertEquals(MONTHLY_SALARY, enrollment.getMonthlySalary()));
        }
    }

    @Nested
    @DisplayName("shouldExecute 方法測試")
    class ShouldExecuteTests {

        @Test
        @DisplayName("有投保單位和級距時，應返回 true")
        void shouldExecute_WithUnitAndLevel_ShouldReturnTrue() {
            assertTrue(task.shouldExecute(context));
        }

        @Test
        @DisplayName("無投保單位時，應返回 false")
        void shouldExecute_WithoutUnit_ShouldReturnFalse() {
            context.setInsuranceUnit(null);
            assertFalse(task.shouldExecute(context));
        }

        @Test
        @DisplayName("無勞保級距時，應返回 false")
        void shouldExecute_WithoutLaborLevel_ShouldReturnFalse() {
            context.setLaborLevel(null);
            assertFalse(task.shouldExecute(context));
        }
    }

    @Test
    @DisplayName("getName 應返回 '建立加保記錄'")
    void getName_ShouldReturnCorrectName() {
        assertEquals("建立加保記錄", task.getName());
    }

    // ==================== Helper Methods ====================

    private InsuranceLevel createTestLevel(InsuranceType type) {
        return new InsuranceLevel(
                LevelId.generate(),
                type,
                15,
                MONTHLY_SALARY,
                LocalDate.parse(ENROLL_DATE));
    }
}
