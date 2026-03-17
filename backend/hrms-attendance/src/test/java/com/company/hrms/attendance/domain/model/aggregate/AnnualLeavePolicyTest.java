package com.company.hrms.attendance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.attendance.domain.model.entity.AnnualLeaveRule;
import com.company.hrms.attendance.domain.model.valueobject.PolicyId;
import com.company.hrms.common.domain.model.Identifier;

/**
 * AnnualLeavePolicy 聚合根測試
 * 驗證月數匹配與法定預設規則建立
 */
class AnnualLeavePolicyTest {

    private Identifier<String> ruleId(String id) {
        return new Identifier<String>(id) {};
    }

    @Nested
    @DisplayName("calculateDaysByMonths 方法")
    class CalculateDaysByMonthsTests {

        private AnnualLeavePolicy policy;

        @BeforeEach
        void setUp() {
            policy = new AnnualLeavePolicy(new PolicyId("P1"), "測試政策");
            // 6~12 月 = 3 天
            policy.addRule(new AnnualLeaveRule(ruleId("R1"), 6, 12, 3));
            // 12~24 月 = 7 天
            policy.addRule(new AnnualLeaveRule(ruleId("R2"), 12, 24, 7));
            // 24~36 月 = 10 天
            policy.addRule(new AnnualLeaveRule(ruleId("R3"), 24, 36, 10));
        }

        @Test
        @DisplayName("6 個月年資應返回 3 天")
        void sixMonths_shouldReturnThreeDays() {
            assertEquals(3, policy.calculateDaysByMonths(6));
        }

        @Test
        @DisplayName("12 個月年資應返回 7 天")
        void twelveMonths_shouldReturnSevenDays() {
            assertEquals(7, policy.calculateDaysByMonths(12));
        }

        @Test
        @DisplayName("未匹配任何規則應返回 0 天")
        void noMatch_shouldReturnZero() {
            assertEquals(0, policy.calculateDaysByMonths(5));
            assertEquals(0, policy.calculateDaysByMonths(36));
        }
    }

    @Nested
    @DisplayName("createStatutoryDefault 靜態工廠方法")
    class StatutoryDefaultTests {

        @Test
        @DisplayName("應建立包含勞基法第 38 條所有段落的預設政策")
        void shouldCreatePolicyWithAllTiers() {
            AnnualLeavePolicy policy = AnnualLeavePolicy.createStatutoryDefault();

            assertNotNull(policy);
            assertEquals("勞基法第38條法定特休", policy.getName());
            assertTrue(policy.isActive());

            // 驗證各年資段落
            assertEquals(0, policy.calculateDaysByMonths(5));    // 未滿 6 個月
            assertEquals(3, policy.calculateDaysByMonths(6));    // 6 個月
            assertEquals(7, policy.calculateDaysByMonths(12));   // 1 年
            assertEquals(10, policy.calculateDaysByMonths(24));  // 2 年
            assertEquals(14, policy.calculateDaysByMonths(36));  // 3 年
            assertEquals(15, policy.calculateDaysByMonths(60));  // 5 年
            assertEquals(15, policy.calculateDaysByMonths(119)); // 9 年 11 個月
        }

        @Test
        @DisplayName("預設政策應使用週年制")
        void shouldUseAnniversarySystem() {
            AnnualLeavePolicy policy = AnnualLeavePolicy.createStatutoryDefault();
            assertEquals(
                com.company.hrms.attendance.domain.model.valueobject.AnnualLeaveSystem.ANNIVERSARY,
                policy.getAnnualLeaveSystem()
            );
        }
    }

    @Nested
    @DisplayName("向後相容 calculateDays(int yearsOfService)")
    class BackwardCompatibilityTests {

        @Test
        @DisplayName("既有的 calculateDays 方法仍以年為單位匹配")
        void calculateDays_shouldStillWork() {
            AnnualLeavePolicy policy = new AnnualLeavePolicy(new PolicyId("P2"), "向後相容測試");
            // 規則：12~24 月（即 1~2 年）
            policy.addRule(new AnnualLeaveRule(ruleId("R1"), 12, 24, 7));

            // 原本用年數匹配，1 年 = 12 月 → 匹配
            assertEquals(7, policy.calculateDays(1));
            assertEquals(0, policy.calculateDays(2));
        }
    }
}
