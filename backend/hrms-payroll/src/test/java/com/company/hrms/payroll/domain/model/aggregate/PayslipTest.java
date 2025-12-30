package com.company.hrms.payroll.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.company.hrms.payroll.domain.model.entity.PayslipItem;
import com.company.hrms.payroll.domain.model.valueobject.InsuranceDeductions;
import com.company.hrms.payroll.domain.model.valueobject.OvertimePayDetail;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayslipStatus;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

class PayslipTest {

    @Test
    void shouldCalculateNetWage() {
        Payslip payslip = Payslip.create(
                RunId.generate(), "EMP", "001", "Name",
                PayPeriod.ofMonth(2025, 12), LocalDate.now());

        // Base: 40000
        payslip.setBaseSalary(new BigDecimal("40000"));

        // Earning: +5000
        payslip.addEarningItem(PayslipItem.createEarning("A", "Allow", new BigDecimal("5000"), ""));

        // Deduction: -1000
        payslip.addDeductionItem(PayslipItem.createDeduction("D", "Deduct", new BigDecimal("1000"), ""));

        // Overtime: +2000
        payslip.setOvertimePay(OvertimePayDetail.builder().weekdayPay(new BigDecimal("2000")).build());

        // Leave: -500
        payslip.setLeaveDeduction(new BigDecimal("500"));

        // Insurance: -1500
        payslip.setInsuranceDeductions(InsuranceDeductions.of(
                new BigDecimal("1000"), // labor
                new BigDecimal("500"), // health
                BigDecimal.ZERO,
                BigDecimal.ZERO));

        // Tax: -0 (for simplicity)
        payslip.setIncomeTax(BigDecimal.ZERO);

        // Calc:
        // Gross = 40000 + 5000 + 2000 - 500 = 46500
        // Net = 46500 - 1000 - 1500 = 44000

        payslip.calculate();

        assertThat(payslip.getGrossWage()).isEqualByComparingTo("46500");
        assertThat(payslip.getNetWage()).isEqualByComparingTo("44000");
    }

    @Test
    void shouldFinalize() {
        Payslip payslip = Payslip.create(RunId.generate(), "EMP", "001", "Name", PayPeriod.ofMonth(2025, 12),
                LocalDate.now());

        payslip.setBaseSalary(new BigDecimal("1000"));
        payslip.calculate();

        payslip.finalize();

        assertThat(payslip.getStatus()).isEqualTo(PayslipStatus.FINALIZED);
    }
}
