package com.company.hrms.payroll.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayrollRunStatus;
import com.company.hrms.payroll.domain.model.valueobject.PayrollStatistics;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

class PayrollRunTest {

    private PayrollRun createRun() {
        return PayrollRun.create(
                new RunId("PR-202512"),
                "Dec 2025 Payroll",
                "ORG-001",
                PayPeriod.ofMonth(2025, 12),
                PayrollSystem.MONTHLY,
                LocalDate.of(2026, 1, 5),
                "USER-001");
    }

    @Test
    void shouldCreateDraft() {
        PayrollRun run = createRun();

        assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.DRAFT);
        assertThat(run.getId().toString()).isEqualTo("PR-202512");
        assertThat(run.getPayrollSystem()).isEqualTo(PayrollSystem.MONTHLY);
    }

    @Test
    void shouldStartExecution() {
        PayrollRun run = createRun();

        run.startExecution("USER-001", 100);

        assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.CALCULATING);
        assertThat(run.getStatistics().getTotalEmployees()).isEqualTo(100);
    }

    @Test
    void shouldCompleteExecution() {
        PayrollRun run = createRun();
        run.startExecution("USER-001", 10);

        run.complete(PayrollStatistics.builder().processedEmployees(10).build());

        assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.COMPLETED);
    }

    @Test
    void shouldResetFailedRun() {
        PayrollRun run = createRun();
        run.startExecution("USER-001", 10);
        run.fail("Error");

        assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.FAILED);

        run.reset();
        assertThat(run.getStatus()).isEqualTo(PayrollRunStatus.DRAFT);
        assertThat(run.getCancelReason()).isNull();
    }

    @Test
    void shouldNotResetCompletedRun() {
        PayrollRun run = createRun();
        run.startExecution("USER-001", 10);
        run.complete(PayrollStatistics.empty());

        assertThatThrownBy(run::reset)
                .isInstanceOf(DomainException.class);
    }
}
