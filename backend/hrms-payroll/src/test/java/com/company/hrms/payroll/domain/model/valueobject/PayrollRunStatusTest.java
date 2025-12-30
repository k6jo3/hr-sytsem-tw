package com.company.hrms.payroll.domain.model.valueobject;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;

class PayrollRunStatusTest {

    @Test
    void shouldAllowValidTransition() {
        PayrollRunStatus status = PayrollRunStatus.DRAFT;

        assertThat(status.canTransitionTo(PayrollRunStatus.CALCULATING)).isTrue();
        assertThat(status.canTransitionTo(PayrollRunStatus.CANCELLED)).isTrue();
    }

    @Test
    void shouldForbidInvalidTransition() {
        PayrollRunStatus status = PayrollRunStatus.DRAFT;

        assertThat(status.canTransitionTo(PayrollRunStatus.COMPLETED)).isFalse();
        assertThat(status.canTransitionTo(PayrollRunStatus.PAID)).isFalse();
    }

    @Test
    void shouldValidateTransition() {
        PayrollRunStatus status = PayrollRunStatus.DRAFT;

        // Valid
        status.validateTransition(PayrollRunStatus.CALCULATING);

        // Invalid
        assertThatThrownBy(() -> status.validateTransition(PayrollRunStatus.PAID))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("無法從 DRAFT 轉換到 PAID");
    }

    @Test
    void shouldIdentifyFinalState() {
        assertThat(PayrollRunStatus.PAID.isFinal()).isTrue();
        assertThat(PayrollRunStatus.CANCELLED.isFinal()).isTrue();
        assertThat(PayrollRunStatus.DRAFT.isFinal()).isFalse();
    }
}
