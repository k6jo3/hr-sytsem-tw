package com.company.hrms.insurance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;

/**
 * InsuranceEnrollment 聚合根單元測試
 * TDD: 先寫測試，再驗證實作
 */
@DisplayName("InsuranceEnrollment 聚合根測試")
class InsuranceEnrollmentTest {

    private static final String EMPLOYEE_ID = "EMP001";
    private static final UnitId UNIT_ID = new UnitId("unit-001");
    private static final LocalDate ENROLL_DATE = LocalDate.of(2025, 1, 1);
    private static final BigDecimal MONTHLY_SALARY = new BigDecimal("48200");

    @Nested
    @DisplayName("加保測試")
    class EnrollTests {

        @Test
        @DisplayName("成功加保時，狀態應為 ACTIVE")
        void testEnroll_ShouldCreateWithActiveStatus() {
            // Given
            InsuranceLevel level = createTestLevel();

            // When
            InsuranceEnrollment enrollment = InsuranceEnrollment.enroll(
                    EMPLOYEE_ID, UNIT_ID, InsuranceType.LABOR, level, ENROLL_DATE);

            // Then
            assertNotNull(enrollment.getId());
            assertEquals(EMPLOYEE_ID, enrollment.getEmployeeId());
            assertEquals(InsuranceType.LABOR, enrollment.getInsuranceType());
            assertEquals(EnrollmentStatus.ACTIVE, enrollment.getStatus());
            assertEquals(ENROLL_DATE, enrollment.getEnrollDate());
            assertEquals(MONTHLY_SALARY, enrollment.getMonthlySalary());
        }

        @Test
        @DisplayName("員工ID為空時，應拋出例外")
        void testEnroll_WithNullEmployeeId_ShouldThrow() {
            InsuranceLevel level = createTestLevel();

            assertThrows(IllegalArgumentException.class,
                    () -> InsuranceEnrollment.enroll(null, UNIT_ID, InsuranceType.LABOR, level, ENROLL_DATE));
        }

        @Test
        @DisplayName("投保級距月薪為零時，創建 Level 應拋出例外")
        void testEnroll_WithZeroSalary_ShouldThrow() {
            // 驗證在創建 InsuranceLevel 時就拋出異常
            assertThrows(IllegalArgumentException.class, () -> new InsuranceLevel(
                    LevelId.generate(), InsuranceType.LABOR, 1, BigDecimal.ZERO, ENROLL_DATE));
        }
    }

    @Nested
    @DisplayName("退保測試")
    class WithdrawTests {

        @Test
        @DisplayName("已加保狀態退保，狀態應變更為 WITHDRAWN")
        void testWithdraw_ActiveStatus_ShouldChangeToWithdrawn() {
            // Given
            InsuranceEnrollment enrollment = createActiveEnrollment();
            LocalDate withdrawDate = LocalDate.of(2025, 12, 31);

            // When
            enrollment.withdraw(withdrawDate);

            // Then
            assertEquals(EnrollmentStatus.WITHDRAWN, enrollment.getStatus());
            assertEquals(withdrawDate, enrollment.getWithdrawDate());
        }

        @Test
        @DisplayName("退保日期早於加保日期，應拋出例外")
        void testWithdraw_BeforeEnrollDate_ShouldThrow() {
            InsuranceEnrollment enrollment = createActiveEnrollment();
            LocalDate invalidWithdrawDate = LocalDate.of(2024, 12, 31);

            assertThrows(IllegalArgumentException.class, () -> enrollment.withdraw(invalidWithdrawDate));
        }
    }

    @Nested
    @DisplayName("調整級距測試")
    class AdjustLevelTests {

        @Test
        @DisplayName("調整級距應更新投保薪資")
        void testAdjustLevel_ShouldUpdateMonthlySalary() {
            // Given
            InsuranceEnrollment enrollment = createActiveEnrollment();
            InsuranceLevel newLevel = new InsuranceLevel(
                    LevelId.generate(), InsuranceType.LABOR, 16,
                    new BigDecimal("50600"), ENROLL_DATE);

            // When
            enrollment.adjustLevel(newLevel);

            // Then
            assertEquals(new BigDecimal("50600"), enrollment.getMonthlySalary());
        }
    }

    // ==================== Helper Methods ====================

    private InsuranceLevel createTestLevel() {
        return new InsuranceLevel(
                LevelId.generate(), InsuranceType.LABOR, 15, MONTHLY_SALARY, ENROLL_DATE);
    }

    private InsuranceEnrollment createActiveEnrollment() {
        return InsuranceEnrollment.enroll(
                EMPLOYEE_ID, UNIT_ID, InsuranceType.LABOR, createTestLevel(), ENROLL_DATE);
    }
}
