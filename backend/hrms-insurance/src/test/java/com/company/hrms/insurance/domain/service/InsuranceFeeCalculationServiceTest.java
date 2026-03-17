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
 *
 * [2026-03-17 更新]
 * - 費率拆分：勞保普通事故 10.5%、就業保險 1%、職災保險 0.21%
 * - 進位方式：CEILING → HALF_UP
 * - 健保雇主費增加平均眷口數乘數 × 1.57
 * - 新增 65 歲以上場景測試
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
    @DisplayName("一般勞工保費計算（未滿 65 歲）")
    class NonSeniorCalculateFeesTests {

        @Test
        @DisplayName("勞保普通事故費計算: 48200 x 10.5% x 20% = 1012 (員工), x 70% = 3542 (雇主)")
        void testCalculate_LaborFees_ShouldApplyCorrectRate() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            // 勞保普通事故 = 48200 x 10.5% = 5061
            // 員工負擔 = 5061 x 20% = 1012.2 → 四捨五入 = 1012
            assertEquals(new BigDecimal("1012"), fees.getLaborEmployeeFee());
            // 雇主負擔 = 5061 x 70% = 3542.7 → 四捨五入 = 3543
            assertEquals(new BigDecimal("3543"), fees.getLaborEmployerFee());
        }

        @Test
        @DisplayName("就業保險費計算: 48200 x 1% x 20% = 96 (員工), x 70% = 337 (雇主)")
        void testCalculate_EmploymentInsuranceFees_ShouldApplyCorrectRate() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            // 就業保險 = 48200 x 1% = 482
            // 員工負擔 = 482 x 20% = 96.4 → 四捨五入 = 96
            assertEquals(new BigDecimal("96"), fees.getEmploymentInsuranceEmployeeFee());
            // 雇主負擔 = 482 x 70% = 337.4 → 四捨五入 = 337
            assertEquals(new BigDecimal("337"), fees.getEmploymentInsuranceEmployerFee());
        }

        @Test
        @DisplayName("職災保險費計算: 48200 x 0.21% = 101 (雇主全額負擔)")
        void testCalculate_OccupationalAccidentFee_ShouldBeEmployerOnly() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            // 職災保險 = 48200 x 0.21% = 101.22 → 四捨五入 = 101
            assertEquals(new BigDecimal("101"), fees.getOccupationalAccidentFee());
        }

        @Test
        @DisplayName("健保費計算: 48200 x 5.17% x 30% = 748 (員工), 雇主含眷口數乘數")
        void testCalculate_HealthFees_ShouldApplyCorrectRate() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            // 健保 = 48200 x 5.17% = 2491.94
            // 員工負擔 = 2491.94 x 30% = 747.582 → 四捨五入 = 748
            assertEquals(new BigDecimal("748"), fees.getHealthEmployeeFee());
            // 雇主負擔 = 2491.94 x 60% x 1.57 = 2347.4268 → 四捨五入 = 2347
            assertEquals(new BigDecimal("2347"), fees.getHealthEmployerFee());
        }

        @Test
        @DisplayName("勞退提繳: 48200 x 6% = 2892 (雇主)")
        void testCalculate_PensionFees_ShouldApplyCorrectRate() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            // 勞退 = 48200 x 6% = 2892
            assertEquals(new BigDecimal("2892"), fees.getPensionEmployerFee());
        }

        @Test
        @DisplayName("含個人自提: 48200 x 6% = 2892")
        void testCalculate_WithSelfContribution_ShouldAddToEmployeeFee() {
            // Given: 個人自提 6%
            BigDecimal selfRate = new BigDecimal("0.06");

            // When
            InsuranceFees fees = service.calculate(testLevel, selfRate);

            // Then
            // 個人自提 = 48200 x 6% = 2892
            assertEquals(new BigDecimal("2892"), fees.getPensionSelfContribution());
        }

        @Test
        @DisplayName("員工負擔總計 = 勞保員工 + 健保員工 + 就業保險員工 + 自提")
        void testCalculate_TotalEmployeeFee_ShouldSumCorrectly() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            BigDecimal expected = fees.getLaborEmployeeFee()
                    .add(fees.getHealthEmployeeFee())
                    .add(fees.getEmploymentInsuranceEmployeeFee())
                    .add(fees.getPensionSelfContribution());
            assertEquals(expected, fees.getTotalEmployeeFee());
        }

        @Test
        @DisplayName("雇主負擔總計 = 勞保雇主 + 健保雇主 + 勞退 + 職災 + 就業保險雇主")
        void testCalculate_TotalEmployerFee_ShouldSumCorrectly() {
            // When
            InsuranceFees fees = service.calculate(testLevel);

            // Then
            BigDecimal expected = fees.getLaborEmployerFee()
                    .add(fees.getHealthEmployerFee())
                    .add(fees.getPensionEmployerFee())
                    .add(fees.getOccupationalAccidentFee())
                    .add(fees.getEmploymentInsuranceEmployerFee());
            assertEquals(expected, fees.getTotalEmployerFee());
        }
    }

    @Nested
    @DisplayName("65 歲以上勞工保費計算")
    class SeniorCalculateFeesTests {

        @Test
        @DisplayName("65 歲以上：勞保普通事故與就業保險為 0")
        void testCalculate_Senior_LaborAndEmploymentShouldBeZero() {
            // When
            InsuranceFees fees = service.calculate(testLevel, null, true);

            // Then — 勞保普通事故免投保
            assertEquals(BigDecimal.ZERO, fees.getLaborEmployeeFee());
            assertEquals(BigDecimal.ZERO, fees.getLaborEmployerFee());

            // Then — 就業保險免投保
            assertEquals(BigDecimal.ZERO, fees.getEmploymentInsuranceEmployeeFee());
            assertEquals(BigDecimal.ZERO, fees.getEmploymentInsuranceEmployerFee());
        }

        @Test
        @DisplayName("65 歲以上：職災保險仍須投保（雇主全額負擔）")
        void testCalculate_Senior_OccupationalAccidentStillRequired() {
            // When
            InsuranceFees fees = service.calculate(testLevel, null, true);

            // Then — 職災保險 = 48200 x 0.21% = 101.22 → 四捨五入 = 101
            assertEquals(new BigDecimal("101"), fees.getOccupationalAccidentFee());
        }

        @Test
        @DisplayName("65 歲以上：健保仍須投保")
        void testCalculate_Senior_HealthStillRequired() {
            // When
            InsuranceFees fees = service.calculate(testLevel, null, true);

            // Then — 健保與一般勞工相同
            assertEquals(new BigDecimal("748"), fees.getHealthEmployeeFee());
            assertEquals(new BigDecimal("2347"), fees.getHealthEmployerFee());
        }

        @Test
        @DisplayName("65 歲以上：勞退仍須提繳")
        void testCalculate_Senior_PensionStillRequired() {
            // When
            InsuranceFees fees = service.calculate(testLevel, null, true);

            // Then — 勞退 = 48200 x 6% = 2892
            assertEquals(new BigDecimal("2892"), fees.getPensionEmployerFee());
        }

        @Test
        @DisplayName("65 歲以上：員工負擔總計僅含健保（無勞保、無就業保險）")
        void testCalculate_Senior_TotalEmployeeFee_OnlyHealth() {
            // When
            InsuranceFees fees = service.calculate(testLevel, null, true);

            // Then — 員工負擔 = 健保員工 748
            assertEquals(new BigDecimal("748"), fees.getTotalEmployeeFee());
        }

        @Test
        @DisplayName("65 歲以上：雇主負擔總計 = 健保雇主 + 勞退 + 職災")
        void testCalculate_Senior_TotalEmployerFee() {
            // When
            InsuranceFees fees = service.calculate(testLevel, null, true);

            // Then — 雇主負擔 = 2347 + 2892 + 101 = 5340
            BigDecimal expected = fees.getHealthEmployerFee()
                    .add(fees.getPensionEmployerFee())
                    .add(fees.getOccupationalAccidentFee());
            assertEquals(expected, fees.getTotalEmployerFee());
            assertEquals(new BigDecimal("5340"), fees.getTotalEmployerFee());
        }

        @Test
        @DisplayName("65 歲以上含個人自提: 48200 x 3% = 1446")
        void testCalculate_Senior_WithSelfContribution() {
            // Given: 個人自提 3%
            BigDecimal selfRate = new BigDecimal("0.03");

            // When
            InsuranceFees fees = service.calculate(testLevel, selfRate, true);

            // Then
            assertEquals(new BigDecimal("1446"), fees.getPensionSelfContribution());
            // 員工負擔 = 健保 748 + 自提 1446 = 2194
            assertEquals(new BigDecimal("2194"), fees.getTotalEmployeeFee());
        }
    }

    @Nested
    @DisplayName("InsuranceType 年齡相關方法測試")
    class InsuranceTypeTests {

        @Test
        @DisplayName("isRequiredForSenior: 職災、健保、勞退回傳 true")
        void testIsRequiredForSenior() {
            assertTrue(InsuranceType.OCCUPATIONAL_ACCIDENT.isRequiredForSenior());
            assertTrue(InsuranceType.HEALTH.isRequiredForSenior());
            assertTrue(InsuranceType.PENSION.isRequiredForSenior());

            assertFalse(InsuranceType.LABOR.isRequiredForSenior());
            assertFalse(InsuranceType.EMPLOYMENT_INSURANCE.isRequiredForSenior());
            assertFalse(InsuranceType.GROUP_LIFE.isRequiredForSenior());
            assertFalse(InsuranceType.GROUP_ACCIDENT.isRequiredForSenior());
            assertFalse(InsuranceType.GROUP_MEDICAL.isRequiredForSenior());
        }

        @Test
        @DisplayName("isRequiredForNonSenior: 勞保、就業保險、職災、健保、勞退回傳 true")
        void testIsRequiredForNonSenior() {
            assertTrue(InsuranceType.LABOR.isRequiredForNonSenior());
            assertTrue(InsuranceType.EMPLOYMENT_INSURANCE.isRequiredForNonSenior());
            assertTrue(InsuranceType.OCCUPATIONAL_ACCIDENT.isRequiredForNonSenior());
            assertTrue(InsuranceType.HEALTH.isRequiredForNonSenior());
            assertTrue(InsuranceType.PENSION.isRequiredForNonSenior());

            assertFalse(InsuranceType.GROUP_LIFE.isRequiredForNonSenior());
            assertFalse(InsuranceType.GROUP_ACCIDENT.isRequiredForNonSenior());
            assertFalse(InsuranceType.GROUP_MEDICAL.isRequiredForNonSenior());
        }

        @Test
        @DisplayName("isStatutory: 勞保、健保、勞退、職災、就業保險均為法定保險")
        void testIsStatutory() {
            assertTrue(InsuranceType.LABOR.isStatutory());
            assertTrue(InsuranceType.HEALTH.isStatutory());
            assertTrue(InsuranceType.PENSION.isStatutory());
            assertTrue(InsuranceType.OCCUPATIONAL_ACCIDENT.isStatutory());
            assertTrue(InsuranceType.EMPLOYMENT_INSURANCE.isStatutory());

            assertFalse(InsuranceType.GROUP_LIFE.isStatutory());
        }

        @Test
        @DisplayName("新增枚舉值的 displayName 正確")
        void testDisplayName() {
            assertEquals("職災保險", InsuranceType.OCCUPATIONAL_ACCIDENT.getDisplayName());
            assertEquals("就業保險", InsuranceType.EMPLOYMENT_INSURANCE.getDisplayName());
        }
    }

    @Nested
    @DisplayName("進位方式驗證")
    class RoundingTests {

        @Test
        @DisplayName("HALF_UP 進位：0.5 進位為 1，0.4 捨去為 0")
        void testRounding_HalfUp() {
            // 使用投保薪資 27,600（第 7 級），驗證四捨五入行為
            InsuranceLevel level7 = new InsuranceLevel(
                    LevelId.generate(),
                    InsuranceType.LABOR,
                    7,
                    new BigDecimal("27600"),
                    LocalDate.of(2025, 1, 1));

            InsuranceFees fees = service.calculate(level7);

            // 勞保普通事故 = 27600 x 10.5% = 2898
            // 員工 = 2898 x 20% = 579.6 → 四捨五入 = 580
            assertEquals(new BigDecimal("580"), fees.getLaborEmployeeFee());
            // 雇主 = 2898 x 70% = 2028.6 → 四捨五入 = 2029
            assertEquals(new BigDecimal("2029"), fees.getLaborEmployerFee());
        }
    }
}
