package com.company.hrms.payroll.application.factory;

import com.company.hrms.payroll.application.dto.response.PayslipResponse;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;

public class PayslipDtoFactory {

    public static PayslipResponse toResponse(Payslip payslip) {
        return PayslipResponse.builder()
                .id(payslip.getId().getValue())
                .payrollRunId(payslip.getPayrollRunId().getValue())
                .employeeId(payslip.getEmployeeId())
                .employeeNumber(payslip.getEmployeeNumber())
                .employeeName(payslip.getEmployeeName())
                .periodStartDate(payslip.getPayPeriod().getStartDate())
                .periodEndDate(payslip.getPayPeriod().getEndDate())
                .payDate(payslip.getPayDate())
                .baseSalary(payslip.getBaseSalary())
                .grossWage(payslip.getGrossWage())
                .netWage(payslip.getNetWage())
                .totalEarnings(payslip.getTotalEarnings())
                .totalDeductions(payslip.getTotalDeductions())
                .incomeTax(payslip.getIncomeTax())
                .insuranceDeductions(
                        payslip.getInsuranceDeductions() != null ? payslip.getInsuranceDeductions().getTotal()
                                : java.math.BigDecimal.ZERO)
                .leaveDeduction(payslip.getLeaveDeduction())
                .overtimePay(payslip.getOvertimePay() != null ? payslip.getOvertimePay().getTotal()
                        : java.math.BigDecimal.ZERO)
                .status(payslip.getStatus().name())
                .pdfUrl(payslip.getPdfUrl())
                .build();
    }
}
