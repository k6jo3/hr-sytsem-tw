package com.company.hrms.payroll.domain.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.company.hrms.payroll.domain.model.valueobject.OvertimePayDetail;

class OvertimePayCalculatorTest {

    private final OvertimePayCalculator calculator = new OvertimePayCalculator();

    @Test
    void shouldCalculateCorrectly() {
        // Simple delegator test
        OvertimePayDetail detail = calculator.calculate(
                new BigDecimal("200"), // rate
                new BigDecimal("4"), // weekday
                new BigDecimal("0"), // rest
                new BigDecimal("0") // holiday
        );

        // 200 * 2 * 1.34 + 200 * 2 * 1.67 = 536 + 668 = 1204
        assertThat(detail.getWeekdayPay()).isEqualByComparingTo("1204");
    }

    @Test
    void shouldHandleNulls() {
        OvertimePayDetail detail = calculator.calculate(
                new BigDecimal("200"),
                null, null, null);

        assertThat(detail.getTotal()).isEqualByComparingTo("0");
    }
}
