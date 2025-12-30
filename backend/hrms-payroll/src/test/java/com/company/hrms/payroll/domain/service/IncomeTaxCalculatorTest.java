package com.company.hrms.payroll.domain.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.company.hrms.payroll.domain.model.valueobject.TaxBracket;

class IncomeTaxCalculatorTest {

    private final IncomeTaxCalculator calculator = new IncomeTaxCalculator();

    @Test
    void shouldCalculateTax() {
        // Setup brackets:
        // 0 - 20000: 0%
        // 20000 - 50000: 5% (Progressive Deduction: 0 for simple case or calc)
        // Let's use simple logic: (Income * Rate) - Deduction
        // Bracket 1: 0 - 20000. Rate 0.
        // Bracket 2: 20001 - 50000. Rate 5%. Deduction = 20000 * 0.05 = 1000 (if calc
        // properly)
        // Wait, PD formula: (Min of bracket * (Rate - PreviousRate)) + PreviousPD
        // Here: 20000 * (0.05 - 0) + 0 = 1000.

        List<TaxBracket> brackets = List.of(
                TaxBracket.builder().minIncome(BigDecimal.ZERO).maxIncome(new BigDecimal("20000"))
                        .taxRate(BigDecimal.ZERO).progressiveDeduction(BigDecimal.ZERO).build(),
                TaxBracket.builder().minIncome(new BigDecimal("20001")).maxIncome(new BigDecimal("50000"))
                        .taxRate(new BigDecimal("0.05")).progressiveDeduction(new BigDecimal("1000")).build());

        // Case 1: 10000 -> 0 tax
        assertThat(calculator.calculate(new BigDecimal("10000"), brackets)).isEqualByComparingTo("0");

        // Case 2: 30000 -> (30000 * 0.05) - 1000 = 1500 - 1000 = 500.
        // Formula check: (30000 - 20000) * 0.05 = 500. Correct.
        assertThat(calculator.calculate(new BigDecimal("30000"), brackets)).isEqualByComparingTo("500");
    }

    @Test
    void shouldHandleZeroIncome() {
        assertThat(calculator.calculate(BigDecimal.ZERO, List.of())).isEqualByComparingTo("0");
    }
}
