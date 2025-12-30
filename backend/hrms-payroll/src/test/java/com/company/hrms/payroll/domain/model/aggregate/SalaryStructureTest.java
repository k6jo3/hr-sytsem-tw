package com.company.hrms.payroll.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;
import com.company.hrms.payroll.domain.model.valueobject.PayrollCycle;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;

class SalaryStructureTest {

    @Test
    void shouldCreateMonthlyStructure() {
        SalaryStructure structure = SalaryStructure.createMonthly(
                "EMP-001",
                new BigDecimal("48000"),
                PayrollCycle.MONTHLY,
                LocalDate.now());

        assertThat(structure.getPayrollSystem()).isEqualTo(PayrollSystem.MONTHLY);
        assertThat(structure.getMonthlySalary()).isEqualByComparingTo("48000");
        // 48000 / 240 = 200
        assertThat(structure.getCalculatedHourlyRate()).isEqualByComparingTo("200");
    }

    @Test
    void shouldCreateHourlyStructure() {
        SalaryStructure structure = SalaryStructure.createHourly(
                "EMP-001",
                new BigDecimal("200"),
                PayrollCycle.BI_WEEKLY,
                LocalDate.now());

        assertThat(structure.getPayrollSystem()).isEqualTo(PayrollSystem.HOURLY);
        assertThat(structure.getHourlyRate()).isEqualByComparingTo("200");
        assertThat(structure.getCalculatedHourlyRate()).isEqualByComparingTo("200");
    }

    @Test
    void shouldCalculateMonthlyGross() {
        SalaryStructure structure = SalaryStructure.createMonthly(
                "EMP-001",
                new BigDecimal("40000"),
                PayrollCycle.MONTHLY,
                LocalDate.now());

        // Add 5000 allowance
        structure.addSalaryItem(SalaryItem.createEarning("ALLOWANCE", "津貼", new BigDecimal("5000")));

        // Add 1000 deduction (should not affect gross in this method?)
        // Wait, calculateMonthlyGross usually includes earnings, but excludes
        // deductions.
        structure.addSalaryItem(SalaryItem.createDeduction("WELFARE", "福利金", new BigDecimal("500")));

        // 40000 + 5000 = 45000
        assertThat(structure.calculateMonthlyGross()).isEqualByComparingTo("45000");
    }

    @Test
    void shouldCalculateInsurableSalary() {
        SalaryStructure structure = SalaryStructure.createMonthly(
                "EMP-001",
                new BigDecimal("40000"),
                PayrollCycle.MONTHLY,
                LocalDate.now());

        // Insurable Allowance
        structure.addSalaryItem(SalaryItem.createEarning("FOOD", "伙食", new BigDecimal("2400")));

        // Non-insurable Allowance
        // SalaryItem bonus = SalaryItem.createEarning("BONUS", "獎金", new
        // BigDecimal("10000"));
        // Assuming createEarning sets insurable=true by default. I need a way to set
        // false.
        // There is no explicit method for non-insurable earning in factory, but can use
        // builder or createNonTaxable (which sets insurable=false too?).
        // Let's use non-taxable as proxy or create a custom one via builder if
        // accessible?
        // SalaryItem properties are private final, but builder is on class.
        // Wait, builder is package-private or public? @Builder on class usually public.

        // Actually SalaryItem.createNonTaxableEarning sets insurable=false
        structure.addSalaryItem(SalaryItem.createNonTaxableEarning("TRAVEL", "差旅", new BigDecimal("5000")));

        // 40000 + 2400 = 42400
        assertThat(structure.calculateInsurableSalary()).isEqualByComparingTo("42400");
    }

    @Test
    void shouldValidateDuplicateItem() {
        SalaryStructure structure = SalaryStructure.createMonthly("EMP-001", BigDecimal.TEN, null, LocalDate.now());

        structure.addSalaryItem(SalaryItem.createEarning("A", "A", BigDecimal.TEN));

        assertThatThrownBy(() -> structure.addSalaryItem(SalaryItem.createEarning("A", "A", BigDecimal.TEN)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("代碼已存在");
    }
}
