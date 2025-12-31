package com.company.hrms.insurance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.insurance.domain.model.valueobject.IncomeType;

/**
 * SupplementaryPremium 補充保費單元測試
 * TDD: 先寫測試，再驗證實作
 */
@DisplayName("SupplementaryPremium 補充保費測試")
class SupplementaryPremiumTest {

    private static final String EMPLOYEE_ID = "EMP001";
    private static final LocalDate INCOME_DATE = LocalDate.of(2025, 6, 30);
    private static final BigDecimal INSURED_SALARY = new BigDecimal("48200"); // 投保薪資
    private static final BigDecimal THRESHOLD = new BigDecimal("192800"); // 門檻 = 48200 × 4

    @Nested
    @DisplayName("補充保費計算測試")
    class CalculateTests {

        @Test
        @DisplayName("收入超過門檻時，應計算補充保費")
        void testCalculate_IncomeExceedsThreshold_ShouldCalculatePremium() {
            // Given: 獎金 250,000 > 門檻 192,800
            BigDecimal incomeAmount = new BigDecimal("250000");

            // When
            SupplementaryPremium premium = SupplementaryPremium.calculate(
                    EMPLOYEE_ID, IncomeType.BONUS, INCOME_DATE, incomeAmount, INSURED_SALARY);

            // Then
            assertNotNull(premium);
            assertEquals(EMPLOYEE_ID, premium.getEmployeeId());
            assertEquals(IncomeType.BONUS, premium.getIncomeType());

            // 計費基準 = 250,000 - 192,800 = 57,200
            assertEquals(new BigDecimal("57200"), premium.getPremiumBase());

            // 補充保費 = 57,200 × 2.11% = 1,206.92 → 無條件進位 = 1,207
            assertEquals(new BigDecimal("1207"), premium.getPremiumAmount());
        }

        @Test
        @DisplayName("收入未超過門檻時，應返回 null")
        void testCalculate_IncomeUnderThreshold_ShouldReturnNull() {
            // Given: 獎金 100,000 <= 門檻 192,800
            BigDecimal incomeAmount = new BigDecimal("100000");

            // When
            SupplementaryPremium premium = SupplementaryPremium.calculate(
                    EMPLOYEE_ID, IncomeType.BONUS, INCOME_DATE, incomeAmount, INSURED_SALARY);

            // Then
            assertNull(premium);
        }

        @Test
        @DisplayName("收入剛好等於門檻時，應返回 null")
        void testCalculate_IncomeEqualsThreshold_ShouldReturnNull() {
            // Given: 獎金 = 門檻 192,800
            BigDecimal incomeAmount = THRESHOLD;

            // When
            SupplementaryPremium premium = SupplementaryPremium.calculate(
                    EMPLOYEE_ID, IncomeType.BONUS, INCOME_DATE, incomeAmount, INSURED_SALARY);

            // Then
            assertNull(premium);
        }

        @Test
        @DisplayName("計費基準超過上限時，應被 cap 在 1000 萬")
        void testCalculate_ExceedsMaxBase_ShouldCapAt10Million() {
            // Given: 獎金 15,000,000 (超過上限)
            BigDecimal incomeAmount = new BigDecimal("15000000");

            // When
            SupplementaryPremium premium = SupplementaryPremium.calculate(
                    EMPLOYEE_ID, IncomeType.BONUS, INCOME_DATE, incomeAmount, INSURED_SALARY);

            // Then
            assertNotNull(premium);
            // 計費基準應被 cap 在 10,000,000
            assertEquals(new BigDecimal("10000000"), premium.getPremiumBase());
            // 補充保費 = 10,000,000 × 2.11% = 211,000
            assertEquals(new BigDecimal("211000"), premium.getPremiumAmount());
        }
    }

    @Nested
    @DisplayName("靜態判斷方法測試")
    class NeedsPremiumTests {

        @Test
        @DisplayName("收入超過門檻時，需要繳納補充保費")
        void testNeedsSupplementaryPremium_ExceedsThreshold_ShouldReturnTrue() {
            BigDecimal income = new BigDecimal("250000");
            assertTrue(SupplementaryPremium.needsSupplementaryPremium(income, INSURED_SALARY));
        }

        @Test
        @DisplayName("收入未超過門檻時，不需繳納補充保費")
        void testNeedsSupplementaryPremium_UnderThreshold_ShouldReturnFalse() {
            BigDecimal income = new BigDecimal("100000");
            assertFalse(SupplementaryPremium.needsSupplementaryPremium(income, INSURED_SALARY));
        }
    }
}
