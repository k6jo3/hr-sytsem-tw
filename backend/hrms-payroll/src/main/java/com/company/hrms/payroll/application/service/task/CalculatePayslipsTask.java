package com.company.hrms.payroll.application.service.task;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.CalculatePayrollContext;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.valueobject.InsuranceDeductions;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;
import com.company.hrms.payroll.domain.service.PayrollCalculationDomainService;
import com.company.hrms.payroll.domain.service.PayrollCalculationDomainService.PayrollCalculationInput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 計算薪資單任務
 * 遍歷所有符合條件的薪資結構，計算並儲存薪資單
 * 整合法扣款與預借薪資扣回
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CalculatePayslipsTask implements PipelineTask<CalculatePayrollContext> {

        private final IPayslipRepository payslipRepository;
        private final PayrollCalculationDomainService calculationService;
        private final ILegalDeductionRepository legalDeductionRepository;
        private final ISalaryAdvanceRepository salaryAdvanceRepository;

        @Override
        public void execute(CalculatePayrollContext context) {
                for (SalaryStructure struct : context.getEligibleStructures()) {
                        try {
                                // 獲取員工基本資訊
                                com.company.hrms.payroll.infrastructure.client.organization.dto.EmployeeSummaryDto empInfo = context
                                                .getEmployeeInfoMap().get(struct.getEmployeeId());
                                String empCode = empInfo != null ? empInfo.getEmployeeCode() : struct.getEmployeeId();
                                String empName = empInfo != null
                                                ? empInfo.getFullName()
                                                : "員工 " + struct.getEmployeeId();

                                // 建立薪資單草稿
                                Payslip payslip = Payslip.create(
                                                context.getPayrollRun().getId(),
                                                struct.getEmployeeId(),
                                                empCode,
                                                empName,
                                                context.getPayrollRun().getPayPeriod(),
                                                context.getPayrollRun().getPayDate());

                                // 獲取考勤數據 (HR03)
                                com.company.hrms.payroll.infrastructure.client.attendance.AttendanceServiceClient.MonthlyReportItem att = context
                                                .getAttendanceMap().get(struct.getEmployeeId());

                                // 獲取保險數據 (HR05)
                                com.company.hrms.payroll.infrastructure.client.insurance.dto.FeeCalculationResponseDto ins = context
                                                .getInsuranceMap().get(struct.getEmployeeId());

                                PayrollCalculationInput input = PayrollCalculationInput.builder()
                                                .workingHours(att != null ? att.getTotalWorkingHours()
                                                                : new BigDecimal("160"))
                                                .weekdayOvertimeHours(att != null ? att.getWorkdayOvertimeHours()
                                                                : BigDecimal.ZERO)
                                                .restDayOvertimeHours(att != null ? att.getRestDayOvertimeHours()
                                                                : BigDecimal.ZERO)
                                                .holidayOvertimeHours(att != null ? att.getHolidayOvertimeHours()
                                                                : BigDecimal.ZERO)
                                                .unpaidLeaveHours(att != null
                                                                ? (att.getUnpaidLeaveHours() != null
                                                                                ? att.getUnpaidLeaveHours()
                                                                                : BigDecimal.ZERO)
                                                                : BigDecimal.ZERO)
                                                .sickLeaveHours(att != null && att.getSickLeaveHours() != null
                                                                ? att.getSickLeaveHours()
                                                                : BigDecimal.ZERO)
                                                .insuranceDeductions(ins != null ? InsuranceDeductions.of(
                                                                ins.getLaborInsurance(),
                                                                ins.getHealthInsurance(),
                                                                ins.getPensionSelfContribution(),
                                                                BigDecimal.ZERO // 補充保費目前預設為0
                                                ) : InsuranceDeductions.empty())
                                                .build();

                                // 載入法扣款記錄（ACTIVE 狀態，依優先順序排列）
                                List<LegalDeduction> legalDeductions = legalDeductionRepository
                                                .findActiveByEmployeeId(struct.getEmployeeId());

                                // 載入預借薪資記錄（DISBURSED 或 REPAYING 狀態）
                                List<SalaryAdvance> salaryAdvances = salaryAdvanceRepository
                                                .findActiveByEmployeeId(struct.getEmployeeId());

                                // 執行計算（含法扣與預借扣回）
                                calculationService.calculate(payslip, struct, input, Collections.emptyList(),
                                                legalDeductions, salaryAdvances);

                                // 持久化法扣款更新（已扣金額/狀態可能變更）
                                legalDeductions.forEach(legalDeductionRepository::save);

                                // 持久化預借扣回更新（已扣金額/狀態可能變更）
                                salaryAdvances.forEach(salaryAdvanceRepository::save);

                                // 儲存薪資單
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
