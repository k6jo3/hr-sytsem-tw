package com.company.hrms.attendance.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 勞基法第 38 條法定特休天數計算測試
 * 完整覆蓋各年資段落 + 邊界值
 */
class StatutoryAnnualLeaveCalculatorTest {

    private StatutoryAnnualLeaveCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StatutoryAnnualLeaveCalculator();
    }

    @Nested
    @DisplayName("各年資段落法定天數")
    class ServiceMonthsTierTests {

        @Test
        @DisplayName("未滿 6 個月 — 0 天")
        void lessThanSixMonths_shouldBeZero() {
            assertEquals(0, calculator.calculateStatutoryDays(0));
            assertEquals(0, calculator.calculateStatutoryDays(3));
            assertEquals(0, calculator.calculateStatutoryDays(5));
        }

        @Test
        @DisplayName("6 個月(含) ~ 未滿 1 年 — 3 天")
        void sixMonthsToOneYear_shouldBeThreeDays() {
            assertEquals(3, calculator.calculateStatutoryDays(6));
            assertEquals(3, calculator.calculateStatutoryDays(9));
            assertEquals(3, calculator.calculateStatutoryDays(11));
        }

        @Test
        @DisplayName("1 年(含) ~ 未滿 2 年 — 7 天")
        void oneYearToTwoYears_shouldBeSevenDays() {
            assertEquals(7, calculator.calculateStatutoryDays(12));
            assertEquals(7, calculator.calculateStatutoryDays(18));
            assertEquals(7, calculator.calculateStatutoryDays(23));
        }

        @Test
        @DisplayName("2 年(含) ~ 未滿 3 年 — 10 天")
        void twoYearsToThreeYears_shouldBeTenDays() {
            assertEquals(10, calculator.calculateStatutoryDays(24));
            assertEquals(10, calculator.calculateStatutoryDays(30));
            assertEquals(10, calculator.calculateStatutoryDays(35));
        }

        @Test
        @DisplayName("3 年(含) ~ 未滿 5 年 — 14 天")
        void threeYearsToFiveYears_shouldBeFourteenDays() {
            assertEquals(14, calculator.calculateStatutoryDays(36));
            assertEquals(14, calculator.calculateStatutoryDays(48));
            assertEquals(14, calculator.calculateStatutoryDays(59));
        }

        @Test
        @DisplayName("5 年(含) ~ 未滿 10 年 — 15 天")
        void fiveYearsToTenYears_shouldBeFifteenDays() {
            assertEquals(15, calculator.calculateStatutoryDays(60));
            assertEquals(15, calculator.calculateStatutoryDays(90));
            assertEquals(15, calculator.calculateStatutoryDays(119));
        }

        @Test
        @DisplayName("10 年(含)以上 — 每年多 1 天，最多 30 天")
        void tenYearsAndAbove_shouldIncrementByOne() {
            assertEquals(16, calculator.calculateStatutoryDays(120));  // 10 年
            assertEquals(17, calculator.calculateStatutoryDays(132));  // 11 年
            assertEquals(18, calculator.calculateStatutoryDays(144));  // 12 年
            assertEquals(25, calculator.calculateStatutoryDays(228));  // 19 年
            assertEquals(26, calculator.calculateStatutoryDays(240));  // 20 年
        }

        @Test
        @DisplayName("25 年以上 — 上限 30 天")
        void twentyFiveYearsAndAbove_shouldCapAtThirty() {
            assertEquals(30, calculator.calculateStatutoryDays(300));  // 25 年
            assertEquals(30, calculator.calculateStatutoryDays(360));  // 30 年
            assertEquals(30, calculator.calculateStatutoryDays(480));  // 40 年
        }
    }

    @Nested
    @DisplayName("邊界值測試")
    class BoundaryTests {

        @ParameterizedTest(name = "年資 {0} 個月 → {1} 天")
        @CsvSource({
            "0, 0",     // 剛入職
            "5, 0",     // 未滿 6 個月
            "6, 3",     // 剛好 6 個月
            "11, 3",    // 差 1 個月滿 1 年
            "12, 7",    // 剛好 1 年
            "23, 7",    // 差 1 個月滿 2 年
            "24, 10",   // 剛好 2 年
            "35, 10",   // 差 1 個月滿 3 年
            "36, 14",   // 剛好 3 年
            "59, 14",   // 差 1 個月滿 5 年
            "60, 15",   // 剛好 5 年
            "119, 15",  // 差 1 個月滿 10 年
            "120, 16",  // 剛好 10 年
            "240, 26",  // 剛好 20 年
            "300, 30",  // 剛好 25 年（達上限）
            "360, 30",  // 30 年（仍為 30）
        })
        void boundaryValues(int serviceMonths, int expectedDays) {
            assertEquals(expectedDays, calculator.calculateStatutoryDays(serviceMonths));
        }
    }

    @Nested
    @DisplayName("異常輸入")
    class InvalidInputTests {

        @Test
        @DisplayName("負數月數應拋出 IllegalArgumentException")
        void negativeMonths_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                calculator.calculateStatutoryDays(-1));
        }
    }

    @Nested
    @DisplayName("輔助方法 calculateStatutoryDaysByYears")
    class ByYearsHelperTests {

        @Test
        @DisplayName("以年數計算法定天數")
        void byYears_shouldConvertToMonths() {
            assertEquals(7, calculator.calculateStatutoryDaysByYears(1));
            assertEquals(10, calculator.calculateStatutoryDaysByYears(2));
            assertEquals(14, calculator.calculateStatutoryDaysByYears(3));
            assertEquals(15, calculator.calculateStatutoryDaysByYears(5));
            assertEquals(16, calculator.calculateStatutoryDaysByYears(10));
            assertEquals(30, calculator.calculateStatutoryDaysByYears(25));
        }
    }
}
