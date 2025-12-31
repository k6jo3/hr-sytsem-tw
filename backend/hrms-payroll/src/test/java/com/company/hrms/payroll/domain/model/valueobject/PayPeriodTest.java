package com.company.hrms.payroll.domain.model.valueobject;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PayPeriodTest {

    @Test
    void shouldCreateMonthlyPeriod() {
        PayPeriod period = PayPeriod.ofMonth(2025, 12);

        assertThat(period.getStartDate()).isEqualTo("2025-12-01");
        assertThat(period.getEndDate()).isEqualTo("2025-12-31");
        assertThat(period.getYear()).isEqualTo(2025);
        assertThat(period.getMonth()).isEqualTo(12);
    }

    @Test
    void shouldCalculateDays() {
        PayPeriod period = new PayPeriod(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 10));

        assertThat(period.getDays()).isEqualTo(10);
    }

    @Test
    void shouldCalculateWorkingDays() {
        // 2025-12-01 is Monday
        // 1 (Mon) ~ 7 (Sun) => 5 working days
        // 8 (Mon) ~ 10 (Wed) => 3 working days
        // Total 8 days
        PayPeriod period = new PayPeriod(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 10));

        assertThat(period.getWorkingDays()).isEqualTo(8);
    }

    @Test
    void shouldCheckContains() {
        PayPeriod period = PayPeriod.ofMonth(2025, 12);

        assertThat(period.contains(LocalDate.of(2025, 12, 15))).isTrue();
        assertThat(period.contains(LocalDate.of(2025, 11, 30))).isFalse();
        assertThat(period.contains(LocalDate.of(2026, 1, 1))).isFalse();
    }

    @Test
    void shouldValidateDates() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.minusDays(1);

        assertThatThrownBy(() -> new PayPeriod(start, end))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
