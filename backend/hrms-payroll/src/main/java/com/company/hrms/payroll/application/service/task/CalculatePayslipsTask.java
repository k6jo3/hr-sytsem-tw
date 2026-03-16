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
