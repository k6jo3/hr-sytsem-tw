package com.company.hrms.organization.domain.model.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TerminationType 列舉單元測試
 */
@DisplayName("TerminationType 列舉測試")
class TerminationTypeTest {

    @Nested
    @DisplayName("isInvoluntary - 是否為非自願離職")
    class IsInvoluntaryTests {

        @Test
        @DisplayName("資遣應為非自願離職")
        void layoffShouldBeInvoluntary() {
            assertTrue(TerminationType.LAYOFF.isInvoluntary());
        }

        @Test
        @DisplayName("懲戒解雇應為非自願離職")
        void dismissalShouldBeInvoluntary() {
            assertTrue(TerminationType.DISMISSAL.isInvoluntary());
        }

        @Test
        @DisplayName("自願離職不應為非自願離職")
        void voluntaryResignationShouldNotBeInvoluntary() {
            assertFalse(TerminationType.VOLUNTARY_RESIGNATION.isInvoluntary());
        }

        @Test
        @DisplayName("退休不應為非自願離職")
        void retirementShouldNotBeInvoluntary() {
            assertFalse(TerminationType.RETIREMENT.isInvoluntary());
        }

        @Test
        @DisplayName("合意終止不應為非自願離職")
        void mutualAgreementShouldNotBeInvoluntary() {
            assertFalse(TerminationType.MUTUAL_AGREEMENT.isInvoluntary());
        }

        @Test
        @DisplayName("定期契約到期不應為非自願離職")
        void contractExpiryShouldNotBeInvoluntary() {
            assertFalse(TerminationType.CONTRACT_EXPIRY.isInvoluntary());
        }
    }

    @Nested
    @DisplayName("requiresNoticePeriod - 是否需要預告期")
    class RequiresNoticePeriodTests {

        @Test
        @DisplayName("自願離職需要預告期")
        void voluntaryResignationRequiresNotice() {
            assertTrue(TerminationType.VOLUNTARY_RESIGNATION.requiresNoticePeriod());
        }

        @Test
        @DisplayName("資遣需要預告期")
        void layoffRequiresNotice() {
            assertTrue(TerminationType.LAYOFF.requiresNoticePeriod());
        }

        @Test
        @DisplayName("退休需要預告期")
        void retirementRequiresNotice() {
            assertTrue(TerminationType.RETIREMENT.requiresNoticePeriod());
        }

        @Test
        @DisplayName("懲戒解雇不需要預告期")
        void dismissalDoesNotRequireNotice() {
            assertFalse(TerminationType.DISMISSAL.requiresNoticePeriod());
        }

        @Test
        @DisplayName("合意終止不需要預告期")
        void mutualAgreementDoesNotRequireNotice() {
            assertFalse(TerminationType.MUTUAL_AGREEMENT.requiresNoticePeriod());
        }

        @Test
        @DisplayName("定期契約到期不需要預告期")
        void contractExpiryDoesNotRequireNotice() {
            assertFalse(TerminationType.CONTRACT_EXPIRY.requiresNoticePeriod());
        }
    }

    @Nested
    @DisplayName("requiresSeverancePay - 是否需要資遣費")
    class RequiresSeverancePayTests {

        @Test
        @DisplayName("資遣需要資遣費")
        void layoffRequiresSeverancePay() {
            assertTrue(TerminationType.LAYOFF.requiresSeverancePay());
        }

        @Test
        @DisplayName("自願離職不需要資遣費")
        void voluntaryResignationDoesNotRequireSeverancePay() {
            assertFalse(TerminationType.VOLUNTARY_RESIGNATION.requiresSeverancePay());
        }

        @Test
        @DisplayName("懲戒解雇不需要資遣費")
        void dismissalDoesNotRequireSeverancePay() {
            assertFalse(TerminationType.DISMISSAL.requiresSeverancePay());
        }

        @Test
        @DisplayName("退休不需要資遣費")
        void retirementDoesNotRequireSeverancePay() {
            assertFalse(TerminationType.RETIREMENT.requiresSeverancePay());
        }
    }

    @Nested
    @DisplayName("displayName - 顯示名稱")
    class DisplayNameTests {

        @Test
        @DisplayName("各離職類型應有正確的中文顯示名稱")
        void shouldHaveCorrectDisplayNames() {
            assertEquals("自願離職", TerminationType.VOLUNTARY_RESIGNATION.getDisplayName());
            assertEquals("資遣", TerminationType.LAYOFF.getDisplayName());
            assertEquals("懲戒解雇", TerminationType.DISMISSAL.getDisplayName());
            assertEquals("合意終止", TerminationType.MUTUAL_AGREEMENT.getDisplayName());
            assertEquals("定期契約到期", TerminationType.CONTRACT_EXPIRY.getDisplayName());
            assertEquals("退休", TerminationType.RETIREMENT.getDisplayName());
        }
    }
}
