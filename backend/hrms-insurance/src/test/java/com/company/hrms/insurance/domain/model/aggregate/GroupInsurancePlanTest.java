package com.company.hrms.insurance.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.insurance.domain.model.entity.PlanTier;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;

/**
 * GroupInsurancePlan 聚合根單元測試
 * 涵蓋建立方案、新增職等、停用方案、合約有效期檢查、查詢職等、PlanTier 計算
 */
@DisplayName("GroupInsurancePlan 聚合根測試")
class GroupInsurancePlanTest {

    private static final String ORG_ID = "ORG001";
    private static final String PLAN_NAME = "2026年團體壽險方案";
    private static final String PLAN_CODE = "GL-2026-001";
    private static final String INSURER_NAME = "國泰人壽";
    private static final String POLICY_NUMBER = "POL-20260101";
    private static final LocalDate CONTRACT_START = LocalDate.of(2026, 1, 1);
    private static final LocalDate CONTRACT_END = LocalDate.of(2026, 12, 31);

    // ==================== 建立方案 ====================

    @Nested
    @DisplayName("建立方案 (Creation)")
    class CreationTests {

        @Test
        @DisplayName("成功建立團保方案時，應包含正確的基本資訊且為啟用狀態")
        void should_createPlanWithCorrectInfo_when_validParameters() {
            // When
            GroupInsurancePlan plan = createDefaultPlan();

            // Then
            assertNotNull(plan.getPlanId(), "planId 不應為 null");
            assertEquals(ORG_ID, plan.getOrganizationId());
            assertEquals(PLAN_NAME, plan.getPlanName());
            assertEquals(PLAN_CODE, plan.getPlanCode());
            assertEquals(InsuranceType.GROUP_LIFE, plan.getInsuranceType());
            assertEquals(INSURER_NAME, plan.getInsurerName());
            assertEquals(POLICY_NUMBER, plan.getPolicyNumber());
            assertEquals(CONTRACT_START, plan.getContractStartDate());
            assertEquals(CONTRACT_END, plan.getContractEndDate());
            assertTrue(plan.isActive(), "新建方案應為啟用狀態");
            assertNotNull(plan.getTiers(), "tiers 清單不應為 null");
            assertTrue(plan.getTiers().isEmpty(), "新建方案不應有職等");
            assertNotNull(plan.getCreatedAt());
            assertNotNull(plan.getUpdatedAt());
        }

        @Test
        @DisplayName("使用非團體保險類型建立時，應拋出 IllegalArgumentException")
        void should_throwException_when_insuranceTypeIsNotGroupInsurance() {
            // When & Then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    GroupInsurancePlan.create(ORG_ID, PLAN_NAME, PLAN_CODE,
                            InsuranceType.LABOR, INSURER_NAME, POLICY_NUMBER,
                            CONTRACT_START, CONTRACT_END));

            assertTrue(ex.getMessage().contains("團體保險"), "錯誤訊息應包含「團體保險」");
        }

        @Test
        @DisplayName("使用健保類型建立時，應拋出 IllegalArgumentException")
        void should_throwException_when_insuranceTypeIsHealth() {
            assertThrows(IllegalArgumentException.class, () ->
                    GroupInsurancePlan.create(ORG_ID, PLAN_NAME, PLAN_CODE,
                            InsuranceType.HEALTH, INSURER_NAME, POLICY_NUMBER,
                            CONTRACT_START, CONTRACT_END));
        }

        @Test
        @DisplayName("使用 GROUP_ACCIDENT 類型建立時，應成功")
        void should_createPlan_when_insuranceTypeIsGroupAccident() {
            GroupInsurancePlan plan = GroupInsurancePlan.create(ORG_ID, PLAN_NAME, PLAN_CODE,
                    InsuranceType.GROUP_ACCIDENT, INSURER_NAME, POLICY_NUMBER,
                    CONTRACT_START, CONTRACT_END);

            assertEquals(InsuranceType.GROUP_ACCIDENT, plan.getInsuranceType());
        }

        @Test
        @DisplayName("使用 GROUP_MEDICAL 類型建立時，應成功")
        void should_createPlan_when_insuranceTypeIsGroupMedical() {
            GroupInsurancePlan plan = GroupInsurancePlan.create(ORG_ID, PLAN_NAME, PLAN_CODE,
                    InsuranceType.GROUP_MEDICAL, INSURER_NAME, POLICY_NUMBER,
                    CONTRACT_START, CONTRACT_END);

            assertEquals(InsuranceType.GROUP_MEDICAL, plan.getInsuranceType());
        }
    }

    // ==================== 新增職等 ====================

    @Nested
    @DisplayName("新增職等 (AddTier)")
    class AddTierTests {

        @Test
        @DisplayName("成功新增職等時，tiers 清單應包含該職等")
        void should_addTierSuccessfully_when_jobGradeIsNew() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();

            // When
            PlanTier tier = plan.addTier("M1",
                    new BigDecimal("1000000"),
                    new BigDecimal("2000"),
                    new BigDecimal("0.7"));

            // Then
            assertEquals(1, plan.getTiers().size());
            assertNotNull(tier.getTierId());
            assertEquals("M1", tier.getJobGrade());
            assertEquals(new BigDecimal("1000000"), tier.getCoverageAmount());
            assertEquals(new BigDecimal("2000"), tier.getMonthlyPremium());
            assertEquals(new BigDecimal("0.7"), tier.getEmployerShareRate());
        }

        @Test
        @DisplayName("新增多個不同職等時，tiers 數量應正確")
        void should_addMultipleTiers_when_differentJobGrades() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();

            // When
            plan.addTier("M1", new BigDecimal("1000000"), new BigDecimal("2000"), new BigDecimal("0.7"));
            plan.addTier("M2", new BigDecimal("2000000"), new BigDecimal("3500"), new BigDecimal("0.8"));
            plan.addTier("E1", new BigDecimal("500000"), new BigDecimal("1000"), new BigDecimal("0.5"));

            // Then
            assertEquals(3, plan.getTiers().size());
        }

        @Test
        @DisplayName("新增重複職等時，應拋出 IllegalStateException")
        void should_throwException_when_duplicateJobGrade() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();
            plan.addTier("M1", new BigDecimal("1000000"), new BigDecimal("2000"), new BigDecimal("0.7"));

            // When & Then
            IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                    plan.addTier("M1", new BigDecimal("2000000"), new BigDecimal("3000"), new BigDecimal("0.8")));

            assertTrue(ex.getMessage().contains("M1"), "錯誤訊息應包含職等代碼");
        }

        @Test
        @DisplayName("employerShareRate 大於 1 時，應拋出 IllegalArgumentException")
        void should_throwException_when_employerShareRateExceedsOne() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();

            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                    plan.addTier("M1", new BigDecimal("1000000"), new BigDecimal("2000"), new BigDecimal("1.1")));
        }

        @Test
        @DisplayName("employerShareRate 小於 0 時，應拋出 IllegalArgumentException")
        void should_throwException_when_employerShareRateIsNegative() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();

            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                    plan.addTier("M1", new BigDecimal("1000000"), new BigDecimal("2000"), new BigDecimal("-0.1")));
        }

        @Test
        @DisplayName("employerShareRate 等於 0 時，應成功建立")
        void should_addTier_when_employerShareRateIsZero() {
            GroupInsurancePlan plan = createDefaultPlan();

            PlanTier tier = plan.addTier("M1", new BigDecimal("1000000"),
                    new BigDecimal("2000"), BigDecimal.ZERO);

            assertEquals(BigDecimal.ZERO, tier.getEmployerShareRate());
        }

        @Test
        @DisplayName("employerShareRate 等於 1 時，應成功建立")
        void should_addTier_when_employerShareRateIsOne() {
            GroupInsurancePlan plan = createDefaultPlan();

            PlanTier tier = plan.addTier("M1", new BigDecimal("1000000"),
                    new BigDecimal("2000"), BigDecimal.ONE);

            assertEquals(BigDecimal.ONE, tier.getEmployerShareRate());
        }
    }

    // ==================== 停用方案 ====================

    @Nested
    @DisplayName("停用方案 (Deactivate)")
    class DeactivateTests {

        @Test
        @DisplayName("成功停用方案時，active 應為 false")
        void should_deactivate_when_planIsActive() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();
            assertTrue(plan.isActive());

            // When
            plan.deactivate();

            // Then
            assertFalse(plan.isActive(), "停用後 active 應為 false");
        }

        @Test
        @DisplayName("已停用方案再次停用時，active 仍為 false（冪等操作）")
        void should_remainInactive_when_deactivateAlreadyInactivePlan() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();
            plan.deactivate();

            // When - 再次停用（Domain 未拋異常，屬冪等操作）
            plan.deactivate();

            // Then
            assertFalse(plan.isActive());
        }
    }

    // ==================== 合約有效期檢查 ====================

    @Nested
    @DisplayName("合約有效期檢查 (ContractValidity)")
    class ContractValidityTests {

        @Test
        @DisplayName("日期在合約期間內時，應返回 true")
        void should_returnTrue_when_dateIsWithinContractPeriod() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();
            LocalDate middleDate = LocalDate.of(2026, 6, 15);

            // When & Then
            assertTrue(plan.isContractValidOn(middleDate));
        }

        @Test
        @DisplayName("日期在合約起始日當天時，應返回 true（邊界）")
        void should_returnTrue_when_dateIsContractStartDate() {
            GroupInsurancePlan plan = createDefaultPlan();

            assertTrue(plan.isContractValidOn(CONTRACT_START));
        }

        @Test
        @DisplayName("日期在合約結束日當天時，應返回 true（邊界）")
        void should_returnTrue_when_dateIsContractEndDate() {
            GroupInsurancePlan plan = createDefaultPlan();

            assertTrue(plan.isContractValidOn(CONTRACT_END));
        }

        @Test
        @DisplayName("日期在合約起始日之前時，應返回 false")
        void should_returnFalse_when_dateIsBeforeContractStart() {
            GroupInsurancePlan plan = createDefaultPlan();
            LocalDate beforeStart = CONTRACT_START.minusDays(1);

            assertFalse(plan.isContractValidOn(beforeStart));
        }

        @Test
        @DisplayName("日期在合約結束日之後時，應返回 false")
        void should_returnFalse_when_dateIsAfterContractEnd() {
            GroupInsurancePlan plan = createDefaultPlan();
            LocalDate afterEnd = CONTRACT_END.plusDays(1);

            assertFalse(plan.isContractValidOn(afterEnd));
        }

        @Test
        @DisplayName("合約結束日為 null 時（開放式合約），任何未來日期都應返回 true")
        void should_returnTrue_when_contractEndDateIsNull() {
            // Given - 無結束日的開放式合約
            GroupInsurancePlan plan = GroupInsurancePlan.create(ORG_ID, PLAN_NAME, PLAN_CODE,
                    InsuranceType.GROUP_LIFE, INSURER_NAME, POLICY_NUMBER,
                    CONTRACT_START, null);

            // When & Then
            assertTrue(plan.isContractValidOn(LocalDate.of(2030, 12, 31)));
            assertTrue(plan.isContractValidOn(CONTRACT_START));
        }
    }

    // ==================== 查詢職等 ====================

    @Nested
    @DisplayName("查詢職等 (FindTier)")
    class FindTierTests {

        @Test
        @DisplayName("職等存在時，應返回對應的 PlanTier")
        void should_returnTier_when_jobGradeExists() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();
            plan.addTier("M1", new BigDecimal("1000000"), new BigDecimal("2000"), new BigDecimal("0.7"));

            // When
            Optional<PlanTier> result = plan.findTierByJobGrade("M1");

            // Then
            assertTrue(result.isPresent());
            assertEquals("M1", result.get().getJobGrade());
        }

        @Test
        @DisplayName("職等不存在時，應返回 Optional.empty")
        void should_returnEmpty_when_jobGradeNotFound() {
            // Given
            GroupInsurancePlan plan = createDefaultPlan();
            plan.addTier("M1", new BigDecimal("1000000"), new BigDecimal("2000"), new BigDecimal("0.7"));

            // When
            Optional<PlanTier> result = plan.findTierByJobGrade("E1");

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("tiers 為空時，應返回 Optional.empty")
        void should_returnEmpty_when_noTiersExist() {
            GroupInsurancePlan plan = createDefaultPlan();

            Optional<PlanTier> result = plan.findTierByJobGrade("M1");

            assertTrue(result.isEmpty());
        }
    }

    // ==================== PlanTier 計算 ====================

    @Nested
    @DisplayName("PlanTier 費用計算")
    class PlanTierCalculationTests {

        @Test
        @DisplayName("getEmployerAmount 計算正確 — 公司負擔 70%")
        void should_calculateEmployerAmount_when_shareRateIs70Percent() {
            // Given: 月繳 2000, 公司負擔 70%
            PlanTier tier = PlanTier.create("M1",
                    new BigDecimal("1000000"),
                    new BigDecimal("2000"),
                    new BigDecimal("0.7"));

            // When
            BigDecimal employerAmount = tier.getEmployerAmount();

            // Then: 2000 * 0.7 = 1400
            assertEquals(new BigDecimal("1400"), employerAmount);
        }

        @Test
        @DisplayName("getEmployeeAmount 計算正確 — 員工負擔 30%")
        void should_calculateEmployeeAmount_when_shareRateIs70Percent() {
            // Given: 月繳 2000, 公司負擔 70%, 員工負擔 30%
            PlanTier tier = PlanTier.create("M1",
                    new BigDecimal("1000000"),
                    new BigDecimal("2000"),
                    new BigDecimal("0.7"));

            // When
            BigDecimal employeeAmount = tier.getEmployeeAmount();

            // Then: 2000 - 1400 = 600
            assertEquals(new BigDecimal("600"), employeeAmount);
        }

        @Test
        @DisplayName("getEmployerAmount 四捨五入正確 — 處理小數點")
        void should_roundHalfUp_when_employerAmountHasDecimals() {
            // Given: 月繳 1000, 公司負擔 33.3%
            PlanTier tier = PlanTier.create("E1",
                    new BigDecimal("500000"),
                    new BigDecimal("1000"),
                    new BigDecimal("0.333"));

            // When
            BigDecimal employerAmount = tier.getEmployerAmount();

            // Then: 1000 * 0.333 = 333.0 → 四捨五入 = 333
            assertEquals(new BigDecimal("333"), employerAmount);
        }

        @Test
        @DisplayName("公司負擔 100% 時，員工自付為 0")
        void should_employeePayZero_when_employerShareIsFullCoverage() {
            PlanTier tier = PlanTier.create("M1",
                    new BigDecimal("1000000"),
                    new BigDecimal("3000"),
                    BigDecimal.ONE);

            assertEquals(new BigDecimal("3000"), tier.getEmployerAmount());
            assertEquals(new BigDecimal("0"), tier.getEmployeeAmount());
        }

        @Test
        @DisplayName("公司負擔 0% 時，員工自付全額")
        void should_employeePayFull_when_employerShareIsZero() {
            PlanTier tier = PlanTier.create("M1",
                    new BigDecimal("1000000"),
                    new BigDecimal("3000"),
                    BigDecimal.ZERO);

            assertEquals(new BigDecimal("0"), tier.getEmployerAmount());
            assertEquals(new BigDecimal("3000"), tier.getEmployeeAmount());
        }
    }

    // ==================== Helper Methods ====================

    private GroupInsurancePlan createDefaultPlan() {
        return GroupInsurancePlan.create(ORG_ID, PLAN_NAME, PLAN_CODE,
                InsuranceType.GROUP_LIFE, INSURER_NAME, POLICY_NUMBER,
                CONTRACT_START, CONTRACT_END);
    }
}
