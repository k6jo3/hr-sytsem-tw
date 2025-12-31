package com.company.hrms.payroll.domain.model.valueobject;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class PayrollStatisticsTest {

    @Test
    void shouldCalculateSuccessRate() {
        PayrollStatistics stats = PayrollStatistics.builder()
                .totalEmployees(100)
                .processedEmployees(95)
                .failedEmployees(2)
                .build();

        // (95 / 100) * 100 = 95.00
        assertThat(stats.getSuccessRate()).isEqualByComparingTo("95.00");
    }

    @Test
    void shouldCalculateAverageWage() {
        PayrollStatistics stats = PayrollStatistics.builder()
                .processedEmployees(10)
                .totalGrossAmount(new BigDecimal("500000"))
                .build();

        // 500000 / 10 = 50000
        assertThat(stats.getAverageGrossWage()).isEqualByComparingTo("50000");
    }

    @Test
    void shouldMergeStatistics() {
        PayrollStatistics s1 = PayrollStatistics.builder()
                .totalEmployees(100)
                .processedEmployees(10)
                .totalGrossAmount(new BigDecimal("1000"))
                .build();

        PayrollStatistics s2 = PayrollStatistics.builder()
                .totalEmployees(100) // This is config, keeps original
                .processedEmployees(20)
                .totalGrossAmount(new BigDecimal("2000"))
                .build();

        PayrollStatistics merged = s1.merge(s2);

        assertThat(merged.getProcessedEmployees()).isEqualTo(30);
        assertThat(merged.getTotalGrossAmount()).isEqualByComparingTo("3000");

        // Note: Merge logic keeps s1.totalEmployees
        assertThat(merged.getTotalEmployees()).isEqualTo(100);
    }
}
