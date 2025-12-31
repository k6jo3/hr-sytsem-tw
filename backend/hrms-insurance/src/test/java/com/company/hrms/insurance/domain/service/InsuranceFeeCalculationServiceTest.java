package com.company.hrms.insurance.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;

/**
 * InsuranceFeeCalculationService 保費計算 Domain Service 單元測試
 * TDD: 先寫測試，再驗證實作
 */
@DisplayName("InsuranceFeeCalculationService 保費計算測試")
class InsuranceFeeCalculationServiceTest {

    private InsuranceFeeCalculationService service;
    private InsuranceLevel testLevel;

    @BeforeEach
    void setUp() {
        service = new InsuranceFeeCalculationService();
        // 使用第 15 級，投保薪資 48,200
        testLevel = new InsuranceLevel(
                LevelId.generate(),
                InsuranceType.LABOR,
                15,
                new BigDecimal("48200"),
                LocalDate.of(2025, 1, 1));
    }

    @Nested
    @DisplayName("保費計算測試 (2025 費率)")
    class CalculateFeesTests {

        @Test
        @DisplayName("勞保費計算: 48200 × 11.5% × 20% = 1109 (員工), × 70% = 3881 (雇主)")
        void testCalculate_LaborFees_ShouldApplyCorrectRate() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            // 勞保 = 48200 × 11.5% = 5543
            // 員工負擔 = 5543 × 20% = 1108.6 → 無條件進位 = 1109
            assertEquals(new BigDecimal("1109"), fees.getLaborEmployeeFee());
            // 雇主負擔 = 5543 × 70% = 3880.1 → 無條件進位 = 3881
            assertEquals(new BigDecimal("3881"), fees.getLaborEmployerFee());
        }

        @Test
        @DisplayName("健保費計算: 48200 × 5.17% × 30% = 747 (員工), × 60% = 1495 (雇主)")
        void testCalculate_HealthFees_ShouldApplyCorrectRate() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            // 健保 = 48200 × 5.17% = 2491.94
            // 員工負擔 = 2491.94 × 30% = 747.58 → 無條件進位 = 748
            assertTrue(fees.getHealthEmployeeFee().compareTo(new BigDecimal("745")) > 0);
            assertTrue(fees.getHealthEmployeeFee().compareTo(new BigDecimal("750")) < 0);
            // 雇主負擔 = 2491.94 × 60% = 1495.16 → 無條件進位
            assertTrue(fees.getHealthEmployerFee().compareTo(new BigDecimal("1490")) > 0);
        }

        @Test
        @DisplayName("勞退提繳: 48200 × 6% = 2892 (雇主)")
        void testCalculate_PensionFees_ShouldApplyCorrectRate() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            // 勞退 = 48200 × 6% = 2892
            assertEquals(new BigDecimal("2892"), fees.getPensionEmployerFee());
        }

        @Test
        @DisplayName("含個人自提: 48200 × 6% = 2892")
        void testCalculate_WithSelfContribution_ShouldAddToEmployeeFee() {
            // Given: 個人自提 6%
            BigDecimal selfRate = new BigDecimal("0.06");

            // When
            InsuranceFees fees = service.calculate(testLevel, selfRate);

            // Then
            // 個人自提 = 48200 × 6% = 2892
            assertEquals(new BigDecimal("2892"), fees.getPensionSelfContribution());
        }

        @Test
        @DisplayName("員工負擔總計 = 勞保員工 + 健保員工 + 自提")
        void testCalculate_TotalEmployeeFee_ShouldSumCorrectly() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            BigDecimal expected = fees.getLaborEmployeeFee()
                    .add(fees.getHealthEmployeeFee())
                    .add(fees.getPensionSelfContribution());
            assertEquals(expected, fees.getTotalEmployeeFee());
        }

        @Test
        @DisplayName("雇主負擔總計 = 勞保雇主 + 健保雇主 + 勞退")
        void testCalculate_TotalEmployerFee_ShouldSumCorrectly() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            BigDecimal expected = fees.getLaborEmployerFee()
                    .add(fees.getHealthEmployerFee())
                    .add(fees.getPensionEmployerFee());
            assertEquals(expected, fees.getTotalEmployerFee());
        }
    }
}
