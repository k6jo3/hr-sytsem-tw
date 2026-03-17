package com.company.hrms.insurance.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * DependentHealthFeeCalculationService 眷屬健保費計算測試
 * [2026-03-17] 新增雇主健保費乘以平均眷口數的測試案例
 */
@DisplayName("DependentHealthFeeCalculationService 眷屬健保費計算測試")
class DependentHealthFeeCalculationServiceTest {

    private DependentHealthFeeCalculationService service;

    // 測試用投保薪資：48,200
    private static final BigDecimal SALARY = new BigDecimal("48200");

    @BeforeEach
    void setUp() {
        // 使用預設平均眷口數 0.57
        service = new DependentHealthFeeCalculationService();
    }

    @Nested
    @DisplayName("眷屬健保費計算（員工負擔）")
    class DependentFeeTests {

        @Test
        @DisplayName("無眷屬時費用為 0")
        void calculateDependentFee_zeroDependents_shouldReturnZero() {
            BigDecimal fee = service.calculateDependentFee(SALARY, 0);
            assertEquals(BigDecimal.ZERO, fee);
        }

        @Test
        @DisplayName("負數眷屬人數時費用為 0")
        void calculateDependentFee_negativeDependents_shouldReturnZero() {
            BigDecimal fee = service.calculateDependentFee(SALARY, -1);
            assertEquals(BigDecimal.ZERO, fee);
        }

        @Test
        @DisplayName("1 位眷屬的健保費計算")
        void calculateDependentFee_oneDependent_shouldCalculateCorrectly() {
            // 48200 × 0.0517 × 0.30 = 747.582 → 四捨五入 = 748
            BigDecimal fee = service.calculateDependentFee(SALARY, 1);
            assertEquals(new BigDecimal("748"), fee);
        }

        @Test
        @DisplayName("2 位眷屬的健保費計算")
        void calculateDependentFee_twoDependents_shouldCalculateCorrectly() {
            // 單人 = 748, 兩人 = 748 × 2 = 1496
            BigDecimal fee = service.calculateDependentFee(SALARY, 2);
            assertEquals(new BigDecimal("1496"), fee);
        }

        @Test
        @DisplayName("眷屬人數超過 3 人上限，以 3 人計")
        void calculateDependentFee_overLimit_shouldCapAtThree() {
            BigDecimal feeThree = service.calculateDependentFee(SALARY, 3);
            BigDecimal feeFive = service.calculateDependentFee(SALARY, 5);
            assertEquals(feeThree, feeFive, "超過 3 人上限應與 3 人費用相同");
        }
    }

    @Nested
    @DisplayName("員工健保費總額計算（員工本人 + 眷屬）")
    class TotalHealthFeeTests {

        @Test
        @DisplayName("無眷屬時只有員工本人費用")
        void calculateTotalHealthFee_noDependents_shouldReturnEmployeeOnly() {
            // 員工本人 = 48200 × 0.0517 × 0.30 = 747.582 → 748
            BigDecimal total = service.calculateTotalHealthFee(SALARY, 0);
            assertEquals(new BigDecimal("748"), total);
        }

        @Test
        @DisplayName("含 2 位眷屬的總費用")
        void calculateTotalHealthFee_twoDependents_shouldSumCorrectly() {
            // 員工 = 748, 眷屬 = 748 × 2 = 1496, 合計 = 2244
            BigDecimal total = service.calculateTotalHealthFee(SALARY, 2);
            assertEquals(new BigDecimal("2244"), total);
        }
    }

    @Nested
    @DisplayName("雇主健保費計算（含平均眷口數）")
    class EmployerHealthFeeTests {

        @Test
        @DisplayName("雇主健保費應乘以 (1 + 平均眷口數)")
        void calculateEmployerHealthFee_shouldMultiplyByAverageDependentRatio() {
            // 48200 × 0.0517 × 0.60 × (1 + 0.57)
            // = 48200 × 0.0517 × 0.60 × 1.57
            // = 2491.94 × 0.60 × 1.57
            // = 1495.164 × 1.57
            // = 2347.40748 → 四捨五入 = 2347
            BigDecimal fee = service.calculateEmployerHealthFee(SALARY);
            assertEquals(new BigDecimal("2347"), fee);
        }

        @Test
        @DisplayName("使用自訂平均眷口數計算雇主健保費")
        void calculateEmployerHealthFee_customRatio_shouldCalculateCorrectly() {
            // 平均眷口數設定為 0.61
            DependentHealthFeeCalculationService customService =
                    new DependentHealthFeeCalculationService(new BigDecimal("0.61"));

            // 48200 × 0.0517 × 0.60 × (1 + 0.61) = 48200 × 0.0517 × 0.60 × 1.61
            // = 2491.94 × 0.60 × 1.61
            // = 1495.164 × 1.61
            // = 2407.21404 → 四捨五入 = 2407
            BigDecimal fee = customService.calculateEmployerHealthFee(SALARY);
            assertEquals(new BigDecimal("2407"), fee);
        }

        @Test
        @DisplayName("平均眷口數為 0 時，雇主健保費不含眷屬加成")
        void calculateEmployerHealthFee_zeroRatio_shouldNotMultiply() {
            DependentHealthFeeCalculationService zeroService =
                    new DependentHealthFeeCalculationService(BigDecimal.ZERO);

            // 48200 × 0.0517 × 0.60 × (1 + 0) = 48200 × 0.0517 × 0.60
            // = 2491.94 × 0.60 = 1495.164 → 四捨五入 = 1495
            BigDecimal fee = zeroService.calculateEmployerHealthFee(SALARY);
            assertEquals(new BigDecimal("1495"), fee);
        }

        @Test
        @DisplayName("進位方式為 HALF_UP（四捨五入）")
        void calculateEmployerHealthFee_shouldUseHalfUpRounding() {
            // 使用特定薪資驗證四捨五入
            // 30300 × 0.0517 × 0.60 × 1.57
            // = 1566.51 × 0.60 × 1.57
            // = 939.906 × 1.57
            // = 1475.65242 → 四捨五入 = 1476
            BigDecimal salary = new BigDecimal("30300");
            BigDecimal fee = service.calculateEmployerHealthFee(salary);
            assertEquals(new BigDecimal("1476"), fee);
        }
    }
}
