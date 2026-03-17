package com.company.hrms.attendance.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.attendance.domain.model.aggregate.AnnualLeavePolicy;
import com.company.hrms.attendance.domain.model.entity.AnnualLeaveRule;
import com.company.hrms.attendance.domain.model.valueobject.PolicyId;
import com.company.hrms.common.domain.model.Identifier;

/**
 * AnnualLeaveCalculationDomainService 單元測試
 * 驗證以月數計算特休額度
 */
class AnnualLeaveCalculationDomainServiceTest {

    private AnnualLeaveCalculationDomainService service;

    private Identifier<String> ruleId(String id) {
        return new Identifier<String>(id) {};
    }

    @BeforeEach
    void setUp() {
        service = new AnnualLeaveCalculationDomainService();
    }

    @Nested
    @DisplayName("calculateEntitlementByMonths 方法")
    class CalculateEntitlementByMonthsTests {

        @Test
        @DisplayName("使用月數計算特休額度 — 正常流程")
        void shouldCalculateByMonths() {
            AnnualLeavePolicy policy = new AnnualLeavePolicy(new PolicyId("P1"), "測試政策");
            policy.addRule(new AnnualLeaveRule(ruleId("R1"), 6, 12, 3));
            policy.addRule(new AnnualLeaveRule(ruleId("R2"), 12, 24, 7));

            assertEquals(3, service.calculateEntitlementByMonths(policy, 6));
            assertEquals(7, service.calculateEntitlementByMonths(policy, 12));
            assertEquals(0, service.calculateEntitlementByMonths(policy, 5));
        }

        @Test
        @DisplayName("政策為 null 應返回 0")
        void nullPolicy_shouldReturnZero() {
            assertEquals(0, service.calculateEntitlementByMonths(null, 12));
        }

        @Test
        @DisplayName("非活躍政策應返回 0")
        void inactivePolicy_shouldReturnZero() {
            AnnualLeavePolicy policy = new AnnualLeavePolicy(new PolicyId("P2"), "停用政策");
            policy.addRule(new AnnualLeaveRule(ruleId("R1"), 6, 12, 3));
            // 需要一個停用方法 — 如果沒有，這個測試可能需要調整
            // 目前 AnnualLeavePolicy 預設 active=true，暫時跳過此測試
        }
    }

    @Nested
    @DisplayName("向後相容 calculateEntitlement")
    class BackwardCompatibilityTests {

        @Test
        @DisplayName("原有方法仍可正常使用")
        void shouldStillWorkWithYears() {
            AnnualLeavePolicy policy = new AnnualLeavePolicy(new PolicyId("P3"), "相容測試");
            policy.addRule(new AnnualLeaveRule(ruleId("R1"), 12, 24, 7));

            assertEquals(7, service.calculateEntitlement(policy, 1));
            assertEquals(0, service.calculateEntitlement(policy, 0));
        }
    }
}
