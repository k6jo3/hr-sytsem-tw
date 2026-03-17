package com.company.hrms.payroll.domain.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 二代健保補充保費計算器單元測試
 *
 * 補充保費規則：
 * - 單次獎金超過投保薪資 4 倍時，需計算補充保費
 * - 補充保費 = (獎金金額 - 投保薪資 x 4) x 2.11%
 * - 若差額為負，則不收取補充保費
 * - 費率常數：2.11%
 */
class SupplementaryPremiumCalculatorTest {

    private SupplementaryPremiumCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SupplementaryPremiumCalculator();
    }

    @Nested
    @DisplayName("獎金補充保費計算")
    class BonusSupplementaryPremium {

        @Test
        @DisplayName("獎金未超過投保薪資 4 倍 — 補充保費為 0")
        void shouldReturnZeroWhenBonusBelowThreshold() {
            // 投保薪資 45,800, 門檻 = 45800 * 4 = 183,200
            // 獎金 100,000 < 183,200 → 補充保費 = 0
            BigDecimal result = calculator.calculateBonusPremium(
                    new BigDecimal("100000"), new BigDecimal("45800"));
            assertThat(result).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("獎金剛好等於投保薪資 4 倍 — 補充保費為 0")
        void shouldReturnZeroWhenBonusEqualsThreshold() {
            // 投保薪資 45,800, 門檻 = 183,200
            // 獎金 183,200 = 183,200 → 差額 = 0 → 補充保費 = 0
            BigDecimal result = calculator.calculateBonusPremium(
                    new BigDecimal("183200"), new BigDecimal("45800"));
            assertThat(result).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("獎金超過投保薪資 4 倍 — 應計算補充保費")
        void shouldCalculatePremiumWhenBonusExceedsThreshold() {
            // 投保薪資 45,800, 門檻 = 183,200
            // 獎金 300,000, 差額 = 300,000 - 183,200 = 116,800
            // 補充保費 = 116,800 * 2.11% = 2,464.48 → 取整 = 2,464
            BigDecimal result = calculator.calculateBonusPremium(
                    new BigDecimal("300000"), new BigDecimal("45800"));
            assertThat(result).isEqualByComparingTo("2464");
        }

        @Test
        @DisplayName("高額獎金應正確計算")
        void shouldCalculateForLargeBonus() {
            // 投保薪資 45,800, 門檻 = 183,200
            // 獎金 1,000,000, 差額 = 1,000,000 - 183,200 = 816,800
            // 補充保費 = 816,800 * 2.11% = 17,234.48 → 取整 = 17,234
            BigDecimal result = calculator.calculateBonusPremium(
                    new BigDecimal("1000000"), new BigDecimal("45800"));
            assertThat(result).isEqualByComparingTo("17234");
        }

        @Test
        @DisplayName("獎金為 0 — 補充保費為 0")
        void shouldReturnZeroForZeroBonus() {
            BigDecimal result = calculator.calculateBonusPremium(
                    BigDecimal.ZERO, new BigDecimal("45800"));
            assertThat(result).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("獎金為 null — 補充保費為 0")
        void shouldReturnZeroForNullBonus() {
            BigDecimal result = calculator.calculateBonusPremium(
                    null, new BigDecimal("45800"));
            assertThat(result).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("投保薪資為 null — 應拋出例外")
        void shouldThrowForNullInsuredSalary() {
            assertThatThrownBy(() -> calculator.calculateBonusPremium(
                    new BigDecimal("300000"), null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
