package com.company.hrms.payroll.application.service.task;

import java.math.BigDecimal;
import java.util.Collections;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.valueobject.InsuranceDeductions;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;
import com.company.hrms.payroll.domain.service.PayrollCalculationDomainService;
import com.company.hrms.payroll.domain.service.PayrollCalculationDomainService.PayrollCalculationInput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 計算薪資單任務
 * 遍歷所有符合條件的薪資結構，計算並儲存薪資單
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CalculatePayslipsTask implements PipelineTask<CalculatePayrollContext> {

    private final IPayslipRepository payslipRepository;
    private final PayrollCalculationDomainService calculationService;

    @Override
    public void execute(CalculatePayrollContext context) {
        for (SalaryStructure struct : context.getEligibleStructures()) {
            try {
                // 建立薪資單草稿
                Payslip payslip = Payslip.create(
                        context.getPayrollRun().getId(),
                        struct.getEmployeeId(),
                        struct.getEmployeeId(), // 員工編號暫用 ID
                        "員工 " + struct.getEmployeeId(), // 員工姓名暫用
                        context.getPayrollRun().getPayPeriod(),
                        context.getPayrollRun().getPayDate());

                // 準備計算輸入（目前使用預設值）
                PayrollCalculationInput input = PayrollCalculationInput.builder()
                        .workingHours(new BigDecimal("160"))
                        .weekdayOvertimeHours(BigDecimal.ZERO)
                        .restDayOvertimeHours(BigDecimal.ZERO)
                        .holidayOvertimeHours(BigDecimal.ZERO)
                        .unpaidLeaveHours(BigDecimal.ZERO)
                        .sickLeaveHours(BigDecimal.ZERO)
                        .insuranceDeductions(InsuranceDeductions.empty())
                        .build();

                // 執行計算
                calculationService.calculate(payslip, struct, input, Collections.emptyList());

                // 儲存
                payslipRepository.save(payslip);

                // 更新統計
                context.addSuccessPayslip(payslip);

            } catch (Exception e) {
                log.error("計算薪資單失敗，員工ID: {}, 錯誤: {}", struct.getEmployeeId(), e.getMessage());
                context.incrementFailCount();
            }
        }
    }

    @Override
    public String getName() {
        return "CalculatePayslipsTask";
    }
}
