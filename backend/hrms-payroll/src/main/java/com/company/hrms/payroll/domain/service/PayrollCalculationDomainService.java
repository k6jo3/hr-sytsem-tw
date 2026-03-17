package com.company.hrms.payroll.domain.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.entity.PayslipItem;
import com.company.hrms.payroll.domain.model.valueobject.InsuranceDeductions;
import com.company.hrms.payroll.domain.model.valueobject.OvertimePayDetail;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.TaxBracket;

import lombok.RequiredArgsConstructor;

/**
 * 薪資計算引擎 Domain Service
 * 負責執行核心薪資計算邏輯，整合各項數據產生最終薪資單
 */
@Service
@RequiredArgsConstructor
public class PayrollCalculationDomainService {

    private final OvertimePayCalculator overtimePayCalculator;
    private final IncomeTaxCalculator incomeTaxCalculator;
    private final LeaveDeductionCalculator leaveDeductionCalculator;

    /**
     * 執行薪資計算
     *
     * @param payslip          薪資單 (草稿)
     * @param structure        薪資結構
     * @param input            外部輸入數據 (差勤、保險等)
     * @param taxBrackets      所得稅級距表
     * @param legalDeductions  法扣款列表（ACTIVE 狀態，依優先順序排列）
     * @param salaryAdvances   預借薪資列表（DISBURSED 或 REPAYING 狀態）
     */
    public void calculate(Payslip payslip,
            SalaryStructure structure,
            PayrollCalculationInput input,
            List<TaxBracket> taxBrackets,
            List<LegalDeduction> legalDeductions,
            List<SalaryAdvance> salaryAdvances) {

        if (!payslip.isDraft()) {
            throw new DomainException("PAY_CALC_NOT_DRAFT", "僅能計算草稿狀態的薪資單");
        }

        // 1. 設定底薪
        BigDecimal baseSalary = (structure.getPayrollSystem() == PayrollSystem.MONTHLY)
                ? structure.getMonthlySalary()
                : structure.getHourlyRate().multiply(input.workingHours);

        payslip.setBaseSalary(baseSalary);

        // 2. 加入固定薪資項目 (來自結構)
        structure.getItems().forEach(item -> {
            payslip.addEarningItem(PayslipItem.fromSalaryItem(item));
        });

        // 3. 計算加班費
        BigDecimal hourlyRate = structure.getOvertimeHourlyRate();
        OvertimePayDetail overtimePay = overtimePayCalculator.calculate(
                hourlyRate,
                input.weekdayOvertimeHours,
                input.restDayOvertimeHours,
                input.holidayOvertimeHours);
        payslip.setOvertimePay(overtimePay);

        // 4. 計算請假扣款
        BigDecimal leaveDeduction = leaveDeductionCalculator.calculate(
                hourlyRate,
                input.unpaidLeaveHours,
                input.sickLeaveHours);
        payslip.setLeaveDeduction(leaveDeduction);

        // 5. 設定保險扣除 (直接使用外部傳入的計算結果，通常由 Insurance Service 提供)
        payslip.setInsuranceDeductions(input.insuranceDeductions);

        // 6. 預算應發薪資 (用於計算所得稅)
        BigDecimal taxableIncome = baseSalary
                .add(payslip.getTotalEarnings())
                .add(overtimePay.getTotal())
                .subtract(leaveDeduction);

        // 7. 計算所得稅
        BigDecimal incomeTax = incomeTaxCalculator.calculate(taxableIncome, taxBrackets);
        payslip.setIncomeTax(incomeTax);

        // 8. 法扣款扣除
        BigDecimal totalLegalDeduction = BigDecimal.ZERO;
        if (legalDeductions != null && !legalDeductions.isEmpty()) {
            // 計算法扣可用額度：淨薪 = 應稅所得 - 保險 - 所得稅
            BigDecimal netForGarnishment = taxableIncome
                    .subtract(input.getInsuranceDeductions().getTotal())
                    .subtract(incomeTax);

            // 法定可扣上限（使用預設最低生活費 14,230 + 扶養費 0）
            BigDecimal maxGarnishment = LegalDeduction.calculateMaxGarnishment(
                    netForGarnishment,
                    input.getMinimumLivingCost() != null ? input.getMinimumLivingCost() : new BigDecimal("14230"),
                    input.getDependentCost() != null ? input.getDependentCost() : BigDecimal.ZERO);

            BigDecimal remainingGarnishment = maxGarnishment;

            // 依優先順序逐筆扣除
            for (LegalDeduction ld : legalDeductions) {
                if (remainingGarnishment.compareTo(BigDecimal.ZERO) <= 0) break;
                BigDecimal deducted = ld.deduct(remainingGarnishment);
                totalLegalDeduction = totalLegalDeduction.add(deducted);
                remainingGarnishment = remainingGarnishment.subtract(deducted);
            }
        }
        payslip.setLegalDeductionAmount(totalLegalDeduction);

        // 9. 預借薪資扣回
        BigDecimal totalAdvanceRepayment = BigDecimal.ZERO;
        if (salaryAdvances != null && !salaryAdvances.isEmpty()) {
            for (SalaryAdvance sa : salaryAdvances) {
                BigDecimal repaid = sa.repay(sa.getInstallmentAmount());
                totalAdvanceRepayment = totalAdvanceRepayment.add(repaid);
            }
        }
        payslip.setSalaryAdvanceRepayment(totalAdvanceRepayment);

        // 10. 執行總計算 (產生 Gross 和 Net)
        payslip.calculate();
    }

    /**
     * 薪資計算輸入數據 DTO (Inner Class)
     */
    @lombok.Builder
    @lombok.Data
    public static class PayrollCalculationInput {
        /**
         * 實際工時 (時薪制用)
         */
        private BigDecimal workingHours;

        /**
         * 平日加班時數
         */
        private BigDecimal weekdayOvertimeHours;

        /**
         * 休息日加班時數
         */
        private BigDecimal restDayOvertimeHours;

        /**
         * 國定假日加班時數
         */
        private BigDecimal holidayOvertimeHours;

        /**
         * 事假/無薪假時數
         */
        private BigDecimal unpaidLeaveHours;

        /**
         * 病假時數
         */
        private BigDecimal sickLeaveHours;

        /**
         * 保險扣除額 (勞健保)
         */
        private InsuranceDeductions insuranceDeductions;

        /**
         * 最低生活費（用於法扣上限計算）
         */
        private BigDecimal minimumLivingCost;

        /**
         * 扶養費用（用於法扣上限計算）
         */
        private BigDecimal dependentCost;
    }
}
