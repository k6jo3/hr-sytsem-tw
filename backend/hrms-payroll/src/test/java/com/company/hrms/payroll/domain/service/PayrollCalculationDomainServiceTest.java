package com.company.hrms.payroll.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceStatus;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;
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

    // ==================== 輔助方法 ====================

    private Payslip createDraftPayslip() {
        return Payslip.create(RunId.generate(), "E", "001", "N",
                PayPeriod.ofMonth(2025, 12), LocalDate.now());
    }

    private SalaryStructure createMonthlyStructure(BigDecimal monthlySalary) {
        return SalaryStructure.createMonthly(
                "E", monthlySalary, PayrollCycle.MONTHLY, LocalDate.now());
    }

    private void mockCalculatorsDefault() {
        when(overtimePayCalculator.calculate(any(), any(), any(), any()))
                .thenReturn(OvertimePayDetail.empty());
        when(leaveDeductionCalculator.calculate(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
    }

    // ==================== 既有測試 ====================

    @Test
    @DisplayName("基本薪資計算 - 底薪 + 津貼 - 保險 - 所得稅")
    void shouldCalculatePayslip() {
        Payslip payslip = createDraftPayslip();

        SalaryStructure structure = createMonthlyStructure(new BigDecimal("40000"));
        structure.addSalaryItem(SalaryItem.createEarning("F", "Food", new BigDecimal("2400")));

        PayrollCalculationDomainService.PayrollCalculationInput input = PayrollCalculationDomainService.PayrollCalculationInput
                .builder()
                .insuranceDeductions(InsuranceDeductions.of(new BigDecimal("1000"), BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO))
                .build();

        mockCalculatorsDefault();
        when(incomeTaxCalculator.calculate(any(), any()))
                .thenReturn(new BigDecimal("500"));

        // 更新為新簽名：加入空的法扣與預借列表
        service.calculate(payslip, structure, input, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());

        // 40000 + 2400 = 42400 (gross)
        // 42400 - 1000 (ins) - 500 (tax) = 40900 (net)
        assertThat(payslip.getBaseSalary()).isEqualByComparingTo("40000");
        assertThat(payslip.getGrossWage()).isEqualByComparingTo("42400");
        assertThat(payslip.getNetWage()).isEqualByComparingTo("40900");
        assertThat(payslip.getIncomeTax()).isEqualByComparingTo("500");
        assertThat(payslip.getLegalDeductionAmount()).isEqualByComparingTo("0");
        assertThat(payslip.getSalaryAdvanceRepayment()).isEqualByComparingTo("0");
    }

    // ==================== 法扣款測試 ====================

    @Test
    @DisplayName("法扣款應依法定上限從淨薪中扣除")
    void shouldDeductLegalGarnishmentFromNetWage() {
        // 底薪 60000, 無津貼, 保險 2000, 所得稅 1000
        // taxableIncome = 60000
        // 淨薪（用於法扣計算）= 60000 - 2000 - 1000 = 57000
        // 法扣上限 = min(57000/3=19000, 57000 - 14230*1.2 - 0 = 57000-17076=39924) = 19000
        // 法扣扣除 = min(19000, 剩餘500000) = 19000
        // net = 60000 - 2000 - 1000 - 19000 = 38000

        Payslip payslip = createDraftPayslip();
        SalaryStructure structure = createMonthlyStructure(new BigDecimal("60000"));

        LegalDeduction ld = new LegalDeduction(
                DeductionId.generate(), "E", "COURT-001",
                GarnishmentType.COURT_ORDER, new BigDecimal("500000"),
                1, LocalDate.now(), "台北地方法院");

        PayrollCalculationDomainService.PayrollCalculationInput input = PayrollCalculationDomainService.PayrollCalculationInput
                .builder()
                .insuranceDeductions(InsuranceDeductions.of(
                        new BigDecimal("1000"), new BigDecimal("1000"), BigDecimal.ZERO, BigDecimal.ZERO))
                .minimumLivingCost(new BigDecimal("14230"))
                .dependentCost(BigDecimal.ZERO)
                .build();

        mockCalculatorsDefault();
        when(incomeTaxCalculator.calculate(any(), any()))
                .thenReturn(new BigDecimal("1000"));

        service.calculate(payslip, structure, input, Collections.emptyList(),
                List.of(ld), Collections.emptyList());

        assertThat(payslip.getLegalDeductionAmount()).isEqualByComparingTo("19000");
        assertThat(payslip.getNetWage()).isEqualByComparingTo("38000");
        // 確認 LegalDeduction 狀態已更新
        assertThat(ld.getDeductedAmount()).isEqualByComparingTo("19000");
        assertThat(ld.getRemainingAmount()).isEqualByComparingTo("481000");
    }

    // ==================== 預借薪資測試 ====================

    @Test
    @DisplayName("預借薪資應從薪資單中扣回每期金額")
    void shouldRepayAdvanceFromPayslip() {
        // 底薪 50000, 保險 1500, 所得稅 800
        // 預借 approved=30000, installmentMonths=6, installmentAmount=5000
        // net = 50000 - 1500 - 800 - 5000 = 42700

        Payslip payslip = createDraftPayslip();
        SalaryStructure structure = createMonthlyStructure(new BigDecimal("50000"));

        SalaryAdvance sa = SalaryAdvance.reconstitute(
                AdvanceId.generate(), "E", new BigDecimal("30000"),
                new BigDecimal("30000"), 6, new BigDecimal("5000"),
                BigDecimal.ZERO, new BigDecimal("30000"),
                LocalDate.now().minusMonths(1), LocalDate.now().minusDays(15),
                AdvanceStatus.DISBURSED, "急用", null, "MGR-001");

        PayrollCalculationDomainService.PayrollCalculationInput input = PayrollCalculationDomainService.PayrollCalculationInput
                .builder()
                .insuranceDeductions(InsuranceDeductions.of(
                        new BigDecimal("750"), new BigDecimal("750"), BigDecimal.ZERO, BigDecimal.ZERO))
                .build();

        mockCalculatorsDefault();
        when(incomeTaxCalculator.calculate(any(), any()))
                .thenReturn(new BigDecimal("800"));

        service.calculate(payslip, structure, input, Collections.emptyList(),
                Collections.emptyList(), List.of(sa));

        assertThat(payslip.getSalaryAdvanceRepayment()).isEqualByComparingTo("5000");
        assertThat(payslip.getNetWage()).isEqualByComparingTo("42700");
        // 預借狀態更新
        assertThat(sa.getRepaidAmount()).isEqualByComparingTo("5000");
        assertThat(sa.getRemainingBalance()).isEqualByComparingTo("25000");
        assertThat(sa.getStatus()).isEqualTo(AdvanceStatus.REPAYING);
    }

    // ==================== 法扣 + 預借 合併測試 ====================

    @Test
    @DisplayName("法扣與預借應同時正確扣除且優先順序正確")
    void shouldProcessBothLegalDeductionAndAdvanceWithCorrectPriority() {
        // 底薪 60000, 保險 2000, 所得稅 1000
        // 法扣上限 19000（同上面測試計算）, 預借每月 5000
        // net = 60000 - 2000 - 1000 - 19000 - 5000 = 33000

        Payslip payslip = createDraftPayslip();
        SalaryStructure structure = createMonthlyStructure(new BigDecimal("60000"));

        LegalDeduction ld = new LegalDeduction(
                DeductionId.generate(), "E", "COURT-001",
                GarnishmentType.COURT_ORDER, new BigDecimal("500000"),
                1, LocalDate.now(), "台北地方法院");

        SalaryAdvance sa = SalaryAdvance.reconstitute(
                AdvanceId.generate(), "E", new BigDecimal("30000"),
                new BigDecimal("30000"), 6, new BigDecimal("5000"),
                BigDecimal.ZERO, new BigDecimal("30000"),
                LocalDate.now().minusMonths(1), LocalDate.now().minusDays(15),
                AdvanceStatus.DISBURSED, "急用", null, "MGR-001");

        PayrollCalculationDomainService.PayrollCalculationInput input = PayrollCalculationDomainService.PayrollCalculationInput
                .builder()
                .insuranceDeductions(InsuranceDeductions.of(
                        new BigDecimal("1000"), new BigDecimal("1000"), BigDecimal.ZERO, BigDecimal.ZERO))
                .minimumLivingCost(new BigDecimal("14230"))
                .dependentCost(BigDecimal.ZERO)
                .build();

        mockCalculatorsDefault();
        when(incomeTaxCalculator.calculate(any(), any()))
                .thenReturn(new BigDecimal("1000"));

        service.calculate(payslip, structure, input, Collections.emptyList(),
                List.of(ld), List.of(sa));

        assertThat(payslip.getLegalDeductionAmount()).isEqualByComparingTo("19000");
        assertThat(payslip.getSalaryAdvanceRepayment()).isEqualByComparingTo("5000");
        assertThat(payslip.getGrossWage()).isEqualByComparingTo("60000");
        assertThat(payslip.getNetWage()).isEqualByComparingTo("33000");
    }
}
