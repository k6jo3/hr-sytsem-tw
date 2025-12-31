package com.company.hrms.payroll.domain.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class LeaveDeductionCalculatorTest {

    private final LeaveDeductionCalculator calculator = new LeaveDeductionCalculator();

    @Test
    void shouldCalculateDeduction() {
        BigDecimal rate = new BigDecimal("200");
        BigDecimal unpaid = new BigDecimal("8"); // 8 hours * 200 = 1600
        BigDecimal sick = new BigDecimal("4"); // 4 hours * 200 * 0.5 = 400

        // Total = 2000
        BigDecimal result = calculator.calculate(rate, unpaid, sick);

        assertThat(result).isEqualByComparingTo("2000");
    }

    @Test
    void shouldHandleNulls() {
        BigDecimal result = calculator.calculate(new BigDecimal("200"), null, null);
        assertThat(result).isEqualByComparingTo("0");
    }
}
