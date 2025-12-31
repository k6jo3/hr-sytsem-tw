package com.company.hrms.payroll.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;
import com.company.hrms.payroll.domain.model.valueobject.InsuranceDeductions;
import com.company.hrms.payroll.domain.model.valueobject.OvertimePayDetail;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayrollCycle;
import com.company.hrms.payroll.domain.model.valueobject.RunId;

@ExtendWith(MockitoExtension.class)
class PayrollCalculationDomainServiceTest {

    @Mock
    private OvertimePayCalculator overtimePayCalculator;
    @Mock
    private IncomeTaxCalculator incomeTaxCalculator;
    @Mock
    private LeaveDeductionCalculator leaveDeductionCalculator;

    @InjectMocks
    private PayrollCalculationDomainService service;

    @Test
    void shouldCalculatePayslip() {
        // Prepare Inputs
        Payslip payslip = Payslip.create(RunId.generate(), "E", "001", "N", PayPeriod.ofMonth(2025, 12),
                LocalDate.now());

        SalaryStructure structure = SalaryStructure.createMonthly(
                "E", new BigDecimal("40000"), PayrollCycle.MONTHLY, LocalDate.now());
        structure.addSalaryItem(SalaryItem.createEarning("F", "Food", new BigDecimal("2400")));

        PayrollCalculationDomainService.PayrollCalculationInput input = PayrollCalculationDomainService.PayrollCalculationInput
                .builder()
                .insuranceDeductions(InsuranceDeductions.of(new BigDecimal("1000"), BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO))
                .build();

        // Mocks
        when(overtimePayCalculator.calculate(any(), any(), any(), any()))
                .thenReturn(OvertimePayDetail.empty());
        when(leaveDeductionCalculator.calculate(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(incomeTaxCalculator.calculate(any(), any()))
                .thenReturn(new BigDecimal("500"));

        // Execute
        service.calculate(payslip, structure, input, Collections.emptyList());

        // Verify matches (40000 + 2400) - 1000 (Ins) - 500 (Tax) = 40900
        assertThat(payslip.getBaseSalary()).isEqualByComparingTo("40000");
        assertThat(payslip.getGrossWage()).isEqualByComparingTo("42400"); // 40000 + 2400
        assertThat(payslip.getNetWage()).isEqualByComparingTo("40900");
        assertThat(payslip.getIncomeTax()).isEqualByComparingTo("500");
    }
}
