package com.company.hrms.payroll.domain.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 資遣費計算 Domain Service 單元測試
 * 新制（勞退新制，2005/7/1 後）：
 * - 每滿 1 年發給 0.5 個月平均工資
 * - 未滿 1 年以比例計算
 * - 最高發給 6 個月平均工資
 */
class SeverancePayCalculationDomainServiceTest {

    private SeverancePayCalculationDomainService service;

    @BeforeEach
    void setUp() {
        service = new SeverancePayCalculationDomainService();
    }

    @Nested
    @DisplayName("新制資遣費計算")
    class NewSystemSeverancePay {

        @Test
        @DisplayName("年資 1 年 — 應發 0.5 個月平均工資")
        void shouldCalculateHalfMonthForOneYear() {
            // 年資 12 個月, 平均月薪 40,000
            // 資遣費 = (12/12) * 0.5 * 40000 = 20,000
            BigDecimal result = service.calculate(12, new BigDecimal("40000"));
            assertThat(result).isEqualByComparingTo("20000");
        }

        @Test
        @DisplayName("年資 5 年 — 應發 2.5 個月平均工資")
        void shouldCalculateTwoAndHalfMonthsForFiveYears() {
            // 年資 60 個月, 平均月薪 50,000
            // 資遣費 = (60/12) * 0.5 * 50000 = 125,000
            BigDecimal result = service.calculate(60, new BigDecimal("50000"));
            assertThat(result).isEqualByComparingTo("125000");
        }

        @Test
        @DisplayName("年資 12 年以上 — 應 cap 在 6 個月平均工資")
        void shouldCapAtSixMonthsForTwelveYearsOrMore() {
            // 年資 180 個月 (15 年), 平均月薪 60,000
            // 資遣費 = min((180/12)*0.5*60000, 6*60000) = min(450000, 360000) = 360,000
            BigDecimal result = service.calculate(180, new BigDecimal("60000"));
            assertThat(result).isEqualByComparingTo("360000");
        }

        @Test
        @DisplayName("年資未滿 1 年 — 應按比例計算")
        void shouldCalculateProRataForLessThanOneYear() {
            // 年資 6 個月, 平均月薪 36,000
            // 資遣費 = (6/12) * 0.5 * 36000 = 9,000
            BigDecimal result = service.calculate(6, new BigDecimal("36000"));
            assertThat(result).isEqualByComparingTo("9000");
        }

        @Test
        @DisplayName("年資 0 個月 — 資遣費為 0")
        void shouldReturnZeroForZeroMonths() {
            BigDecimal result = service.calculate(0, new BigDecimal("40000"));
            assertThat(result).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("年資剛好 12 年 — 恰好等於 cap 上限")
        void shouldExactlyReachCapAtTwelveYears() {
            // 年資 144 個月 (12 年), 平均月薪 60,000
            // 資遣費 = (144/12) * 0.5 * 60000 = 360,000 = 6 * 60000 = 360,000
            BigDecimal result = service.calculate(144, new BigDecimal("60000"));
            assertThat(result).isEqualByComparingTo("360000");
        }

        @Test
        @DisplayName("年資 3 個月 — 未滿 1 年按比例")
        void shouldCalculateProRataForThreeMonths() {
            // 年資 3 個月, 平均月薪 48,000
            // 資遣費 = (3/12) * 0.5 * 48000 = 0.25 * 0.5 * 48000 = 6,000
            BigDecimal result = service.calculate(3, new BigDecimal("48000"));
            assertThat(result).isEqualByComparingTo("6000");
        }

        @Test
        @DisplayName("平均工資為 0 — 資遣費為 0")
        void shouldReturnZeroForZeroSalary() {
            BigDecimal result = service.calculate(24, BigDecimal.ZERO);
            assertThat(result).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("負年資應拋出例外")
        void shouldThrowForNegativeServiceMonths() {
            assertThatThrownBy(() -> service.calculate(-1, new BigDecimal("40000")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("null 平均工資應拋出例外")
        void shouldThrowForNullSalary() {
            assertThatThrownBy(() -> service.calculate(12, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
