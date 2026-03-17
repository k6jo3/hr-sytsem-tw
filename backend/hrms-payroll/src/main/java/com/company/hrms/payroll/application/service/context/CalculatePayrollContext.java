package com.company.hrms.payroll.application.service.context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.CalculatePayrollRequest;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 計算薪資 Context
 * 用於在 Pipeline 中傳遞薪資計算相關資料
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CalculatePayrollContext extends PipelineContext {

    // === 輸入 ===
    private final CalculatePayrollRequest request;
    private final JWTModel currentUser;

    // === 中間資料 ===
    private PayrollRun payrollRun;
    private List<SalaryStructure> eligibleStructures = new ArrayList<>();
    private List<Payslip> calculatedPayslips = new ArrayList<>();

    // 考勤數據 Map (EmployeeId -> Item)
    private java.util.Map<String, com.company.hrms.payroll.infrastructure.client.attendance.AttendanceServiceClient.MonthlyReportItem> attendanceMap = new java.util.HashMap<>();

    // 員工資訊 Map (EmployeeId -> DTO)
    private java.util.Map<String, com.company.hrms.payroll.infrastructure.client.organization.dto.EmployeeSummaryDto> employeeInfoMap = new java.util.HashMap<>();

    // 保險扣除 Map (EmployeeId -> DTO)
    private java.util.Map<String, com.company.hrms.payroll.infrastructure.client.insurance.dto.FeeCalculationResponseDto> insuranceMap = new java.util.HashMap<>();

    // === 統計資料 ===
    private int successCount = 0;
    private int failCount = 0;
    private BigDecimal totalGross = BigDecimal.ZERO;
    private BigDecimal totalNet = BigDecimal.ZERO;
    private BigDecimal totalDeductions = BigDecimal.ZERO;
    private BigDecimal totalOvertime = BigDecimal.ZERO;
    private BigDecimal totalLegalDeductions = BigDecimal.ZERO;
    private BigDecimal totalAdvanceRepayments = BigDecimal.ZERO;

    public CalculatePayrollContext(CalculatePayrollRequest request, JWTModel currentUser) {
        this.request = request;
        this.currentUser = currentUser;
    }

    /**
     * 新增成功計算的薪資單並更新統計
     */
    public void addSuccessPayslip(Payslip payslip) {
        calculatedPayslips.add(payslip);
        successCount++;

        if (payslip.getGrossWage() != null) {
            totalGross = totalGross.add(payslip.getGrossWage());
        }
        if (payslip.getNetWage() != null) {
            totalNet = totalNet.add(payslip.getNetWage());
        }
        totalDeductions = totalDeductions.add(payslip.getTotalDeductions());
        if (payslip.getOvertimePay() != null) {
            totalOvertime = totalOvertime.add(payslip.getOvertimePay().getTotal());
        }
        if (payslip.getLegalDeductionAmount() != null) {
            totalLegalDeductions = totalLegalDeductions.add(payslip.getLegalDeductionAmount());
        }
        if (payslip.getSalaryAdvanceRepayment() != null) {
            totalAdvanceRepayments = totalAdvanceRepayments.add(payslip.getSalaryAdvanceRepayment());
        }
    }

    /**
     * 增加失敗計數
     */
    public void incrementFailCount() {
        failCount++;
    }
}
