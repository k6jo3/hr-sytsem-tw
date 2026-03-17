package com.company.hrms.attendance.domain.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.domain.model.Identifier;

/**
 * AnnualLeaveRule 單元測試
 * 驗證年資匹配邏輯（支援月數精度）
 */
class AnnualLeaveRuleTest {

    private Identifier<String> ruleId(String id) {
        return new Identifier<String>(id) {};
    }

    @Nested
    @DisplayName("以月數建構規則")
    class MonthBasedRuleTests {

        @Test
        @DisplayName("6~12 個月年資應匹配 matchesMonths(6) = true")
        void sixMonthsShouldMatch() {
            AnnualLeaveRule rule = new AnnualLeaveRule(ruleId("R1"), 6, 12, 3);
            assertTrue(rule.matchesMonths(6));
            assertTrue(rule.matchesMonths(11));
            assertFalse(rule.matchesMonths(5));
            assertFalse(rule.matchesMonths(12));
        }

        @Test
        @DisplayName("12~24 個月年資應匹配 matchesMonths(12) = true")
        void twelveMonthsShouldMatch() {
            AnnualLeaveRule rule = new AnnualLeaveRule(ruleId("R2"), 12, 24, 7);
            assertTrue(rule.matchesMonths(12));
            assertTrue(rule.matchesMonths(23));
            assertFalse(rule.matchesMonths(11));
            assertFalse(rule.matchesMonths(24));
        }

        @Test
        @DisplayName("取得天數應返回正確值")
        void getDaysShouldReturnCorrectValue() {
            AnnualLeaveRule rule = new AnnualLeaveRule(ruleId("R3"), 24, 36, 10);
            assertEquals(10, rule.getDays());
        }

        @Test
        @DisplayName("取得最小與最大月數應返回正確值")
        void getMinMaxMonthsShouldReturnCorrectValues() {
            AnnualLeaveRule rule = new AnnualLeaveRule(ruleId("R4"), 6, 12, 3);
            assertEquals(6, rule.getMinServiceMonths());
            assertEquals(12, rule.getMaxServiceMonths());
        }
    }

    @Nested
    @DisplayName("向後相容 — matches(int yearsOfService)")
    class BackwardCompatibilityTests {

        @Test
        @DisplayName("以年數匹配時自動轉換為月數")
        void matchesByYearsShouldConvertToMonths() {
            // 1~2 年 = 12~24 月
            AnnualLeaveRule rule = new AnnualLeaveRule(ruleId("R5"), 12, 24, 7);
            assertTrue(rule.matches(1));  // 1 年 = 12 月
            assertFalse(rule.matches(2)); // 2 年 = 24 月 (exclusive)
            assertFalse(rule.matches(0));
        }
    }
}
